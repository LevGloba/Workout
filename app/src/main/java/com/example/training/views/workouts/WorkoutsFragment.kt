package com.example.training.views.workouts

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.doOnPreDraw
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.training.Constants
import com.example.training.R
import com.example.training.databinding.FragmentWorkoutBinding
import com.example.training.domain.Quality
import com.example.training.domain.model.BaseFragment
import com.example.training.domain.model.observingStateWhenStart
import com.example.training.setTransitionName
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutsFragment : BaseFragment<FragmentWorkoutBinding>() {

    override fun getViewBinding(): FragmentWorkoutBinding =
        FragmentWorkoutBinding.inflate(layoutInflater)


    //Анонимный слушатель проигрываетля
    private val playerListener = object : Player.Listener {
        /*Не стал реализовывать повторный показ видео через 10 сек.
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            player?.apply {
                if (playbackState == Player.STATE_ENDED) lifecycleScope.launch {
                    delay(10000)
                    withContext(Dispatchers.Main) {
                        seekTo(0)
                    }
                }
            }
        }*/

        //Отслеживает ошибки внутри проигрываетля
        override fun onPlayerError(error: PlaybackException) {
            workoutViewModel.run {
                val cause = error.cause
                if (cause is HttpDataSource.InvalidResponseCodeException && cause.responseCode in 400 until 500)
                    callAlertForError(true)
                else
                    callAlertForError()
            }
        }
    }

    private var player: ExoPlayer? = Constants.player?.apply {
        addListener(playerListener)
        volume = 1F
    }
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val args: WorkoutsFragmentArgs by navArgs()
    private var defaultQualityIndex = 1
    private val arrayQuality = arrayOf("1080","720","480")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Прослушивает жест или кнопку назад
        // и выполняет остановку видео и выход из фрагмента
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            player?.stop()
            findNavController().popBackStack()
        }
        //Устанавливает анимацию
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.enter_transition)
        //Добавляет слушателя
        player?.addListener(playerListener)
        workoutViewModel.setId(args.workout.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        eventListener()
        observingState()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        workoutViewModel.savePosition(returnCurrentPosition())
    }

    override fun onDestroy() {
        player?.removeListener(playerListener)
        player = null
        super.onDestroy()
    }

    override fun eventListener() {
        binding.run {
            //Устанавливает позицию
            videoPlayerView.run {
                this.player = this@WorkoutsFragment.player
                setFullscreenButtonClickListener {
                    workoutViewModel.changeSizeScreen(
                        requireActivity().requestedOrientation,
                        returnCurrentPosition()
                    )
                }
            }
            args.workout.run {
                titleTv.run {
                    text = title
                    setTransitionName(this, transitionName + id)
                }
                descriptionTv.run {
                    text = description
                    setTransitionName(this, transitionName + id)
                }
                setTransitionName(containerInformation, containerInformation.transitionName + id)
            }
            //Вызывает анимацию, когда готова к отображению
            (containerInformation.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
            //Вызывает окно по выбору качества видео
            settingQuality.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.string_quality))
                    .setSingleChoiceItems(arrayQuality, defaultQualityIndex
                    ) { _, index ->
                        defaultQualityIndex = index
                        workoutViewModel.setQuality(index, returnCurrentPosition())
                    }
                    .show()
            }
        }
    }

    //Возвращает позицию ползунка
    private fun returnCurrentPosition(): Long = player?.currentPosition?: 0L

    //Изменяет качество видео
    private fun changeQualityParameters(quality: Quality) {
        val width = quality.widthToPx
        val height = quality.heightToPx
        player?.let { player ->
            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .setMaxVideoSize(width, height)
                .setMinVideoSize(width, height)
                .build()
        }
    }

    override fun observingState() {
        observingStateWhenStart {
            workoutViewModel.stateFlow.collect { workoutUi ->
                binding.run {
                    when (workoutUi) {

                        WorkoutUIState.Empty -> loadingOf()

                        WorkoutUIState.EmptyUrlVideo -> {
                            loadingOf()
                            prepareAlertDialog(
                                title = getString(R.string.string_couldnot_get_workout_list),
                                description = getString(R.string.string_please_try_again)
                            ) {
                                workoutViewModel.retryTask()
                            }
                        }

                        WorkoutUIState.Error -> {
                            loadingOf()
                            prepareAlertDialog(
                                title = getString(R.string.string_something_wrong),
                                description = getString(R.string.string_please_try_again)
                            ) {
                                workoutViewModel.retryTask()
                            }
                        }

                        is WorkoutUIState.LoadingState -> {

                            if (workoutUi is WorkoutUIState.LoadingState.Content) {
                                loadingOf()
                                workoutUi.run {

                                    player?.run {
                                        if (mediaItemCount <= 0)
                                            setMediaItem(MediaItem.fromUri(urlVideo))
                                        if (currentPosition != position && currentPosition < position)
                                            seekTo(position)
                                        prepare()
                                        play()
                                    }

                                    changeQualityParameters(quality.quality)

                                    when (stateScreen) {
                                        StateScreen.FullScreen ->
                                            setScreenStatus(
                                                View.GONE,
                                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                            )

                                        StateScreen.DefaultScreen ->
                                            setScreenStatus()

                                    }

                                }
                            } else
                                loadingOn()
                        }

                        WorkoutUIState.VideoError -> {
                            loadingOf()
                            prepareAlertDialog(
                                title = getString(R.string.string_check_network_connection),
                                description = getString(R.string.string_please_try_again)
                            ) {
                                player?.run {
                                    prepare()
                                    play()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun loadingOn() {
        binding.includeLayoutLoading.root.visibility = View.VISIBLE
    }

    override fun loadingOf() {
        binding.includeLayoutLoading.root.visibility = View.GONE
    }

    //Переворачивает экран, убирает лишние элементы
    private fun setScreenStatus(
        containerIsVisibility: Int = View.VISIBLE,
        orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    ) {
        binding.containerInformation.visibility = containerIsVisibility

        val requestedOrientation = requireActivity().requestedOrientation
        if (requestedOrientation != orientation)
            requireActivity().requestedOrientation = orientation

        binding.root.fitsSystemWindows = orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}