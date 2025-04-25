package com.example.training.views.workouts_listed

import android.R.attr.fragment
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.example.training.R
import com.example.training.databinding.FragmentWorkoutsListedBinding
import com.example.training.domain.EnumWorkoutType
import com.example.training.domain.model.BaseFragment
import com.example.training.domain.model.WorkoutItemUI
import com.example.training.domain.model.observingStateWhenStart
import com.example.training.views.workouts_listed.adapter.WorkoutListedAdapter
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WorkoutsListedFragment : BaseFragment<FragmentWorkoutsListedBinding>() {

    private val workoutAdapter = WorkoutListedAdapter { workoutItemUI, extras, view -> openWorkout(workoutItemUI, extras, view) }

    override fun getViewBinding(): FragmentWorkoutsListedBinding =
        FragmentWorkoutsListedBinding.inflate(layoutInflater)

    private val workoutsListedViewModel: WorkoutsListedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.exit_transition);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        eventListener()
        observingState()
    }

    //Отслеживает взаимодествие пользователя с ui
    override fun eventListener() {
        binding.run {
            ViewCompat.setOnApplyWindowInsetsListener(searchLayer) { v, insets ->
                val systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.ime()
                )
                //Добавляет отступы у navigationBar и при открытии клавиатуры
                v.setPaddingRelative(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
            ViewCompat.setOnApplyWindowInsetsListener(workoutsListed) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                //Добавляет отступы у statusBar
                v.setPaddingRelative(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }
            chipGroup.setOnCheckedStateChangeListener { group, _ ->
                workoutsListedViewModel.setFilterList(group.checkedChipId)
            }
            workoutsListed.run {
                adapter = workoutAdapter
                layoutManager = LinearLayoutManager(context,GridLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
            }
            searchView.run {
                setOnCloseListener {
                    clearFocus()
                    workoutsListedViewModel.returnDefaultListWorkouts()
                    true
                }

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.d("text", "onQueryTextSubmit: $query")
                        query?.let {
                            workoutsListedViewModel.searchByText(query.trim())
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.d("text", "onQueryTextChange: $newText")
                        newText?.let {
                            workoutsListedViewModel.searchByText(newText.trim())
                        }
                        return false
                    }
                })
            }
        }
    }

    private fun openWorkout(workoutItemUI: WorkoutItemUI, extras: FragmentNavigator.Extras, view: View) {
        (getExitTransition() as TransitionSet).excludeTarget(view, true)
        findNavController()
            .navigate(
                WorkoutsListedFragmentDirections
                    .actionWorkoutsListedFragmentToWorkoutsFragment(workoutItemUI),
                navigatorExtras = extras
            )
    }

    //Наблюдает за состоянием класса и обрабатывает его
    override fun observingState() {
        observingStateWhenStart {
            workoutsListedViewModel.stateFlow.collect { workoutsListedUIState ->
                binding.run {
                    when (workoutsListedUIState) {

                        WorkoutsListedUIState.Empty -> {
                            loadingOf()
                        }

                        WorkoutsListedUIState.EmptyList -> {
                            loadingOf()
                            prepareAlertDialog(
                                title = getString(R.string.string_couldnot_get_workout_list),
                                description = getString(R.string.string_please_try_again)
                            ) {
                                workoutsListedViewModel.retryTask()
                            }
                        }

                        WorkoutsListedUIState.Error -> {
                            loadingOf()
                            prepareAlertDialog(
                                title = getString(R.string.string_something_wrong),
                                description = getString(R.string.string_please_try_again)
                            ) {
                                workoutsListedViewModel.retryTask()
                            }
                        }

                        is WorkoutsListedUIState.LoadingState -> {

                            if (workoutsListedUIState is WorkoutsListedUIState.LoadingState.Content) {
                                workoutsListedUIState.run {

                                    loadingOf()
                                    setVisibilityOfFiltering(
                                        when (filter) {
                                            EnumWorkoutType.WORKOUT -> workoutButton
                                            EnumWorkoutType.ALL -> null
                                            EnumWorkoutType.ETHER -> etherButton
                                            EnumWorkoutType.COMPLEX -> complexButton
                                        }
                                    )
                                    searchView.run {
                                        if (query.isBlank() && searchText.isNotBlank())
                                            setQuery(searchText, false)
                                    }

                                    workoutAdapter.submitList(listWorkouts)

                                    (binding.root.parent as? ViewGroup)?.doOnPreDraw {
                                        startPostponedEnterTransition()
                                    }
                                }
                            } else
                                loadingOn()
                        }
                    }
                }
            }
        }
    }

    private fun setVisibilityOfFiltering(chip: Chip?) {
        if (chip != null && !chip.isChecked)
            chip.isChecked = true
        else if (chip == null)
            binding.chipGroup.clearCheck()
    }

    override fun loadingOf() {
        binding.includeLayoutLoading.root.visibility = View.GONE
    }

    override fun loadingOn() {
        binding.includeLayoutLoading.root.visibility = View.VISIBLE
    }
}