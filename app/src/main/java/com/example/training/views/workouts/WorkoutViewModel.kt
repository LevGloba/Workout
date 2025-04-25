package com.example.training.views.workouts

import android.content.pm.ActivityInfo
import android.util.Log
import com.example.training.Constants
import com.example.training.domain.EnumQuality
import com.example.training.domain.NetworkManager
import com.example.training.domain.Requests
import com.example.training.domain.model.BaseViewModel
import com.example.training.domain.model.ObserverTask
import com.example.training.domain.model.ObserverTaskManager
import com.example.training.domain.model.launch
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow

sealed class WorkoutUIState {
    sealed class LoadingState: WorkoutUIState() {
        //Отображает состояние загрузки
        data object Loading : LoadingState()

        //Отображает контент
        data class Content(
            val urlVideo: String = "",
            val stateScreen: StateScreen = StateScreen.DefaultScreen,
            val position: Long = 0,
            val quality: EnumQuality = EnumQuality.HD
        ) : LoadingState()
    }

    //Начальное состояние, ничего не отображает
    data object Empty: WorkoutUIState()

    //Отображает ошибку
    data object Error: WorkoutUIState()

    //Отображает, что данных нет
    data object EmptyUrlVideo: WorkoutUIState()

    data object VideoError: WorkoutUIState()
}

//Состояние видео
sealed class StateScreen {
    //Во весь экран
    data object FullScreen : StateScreen()
    //Обычное состояние
    data object DefaultScreen : StateScreen()
}

@HiltViewModel
class WorkoutViewModel @AssistedInject constructor(
    private val requests: Requests,
    networkManager: NetworkManager,
    private val workoutId: Int
): BaseViewModel<WorkoutUIState>(WorkoutUIState.Empty,networkManager){

    private var urlVideo = ""
    private var id = -1

    override val observerTaskManager: ObserverTask
        get() = ObserverTaskManager {
            //Запрашивает тренировку
            job = launch {
                try {
                    //Отображает загрузку
                    mutableStateFlow.value = WorkoutUIState.LoadingState.Loading
                    if (isActive) {
                        //Проверяет, если данные пришли пустыми, то, через время повторный запрос
                        while (countRetry > 0) {
                            delay(500)
                            urlVideo = Constants.BASE_URL + requests.getWorkoutVideo(id).link
                            if (urlVideo.isNotEmpty()) {
                                mutableStateFlow.value = WorkoutUIState.LoadingState.Content(
                                    urlVideo = urlVideo
                                )
                                countRetry = .0
                            } else {
                                countRetry--
                                if (countRetry > 0)
                                    delay((oneMints / countRetry.pow(countRetry)).toLong())
                                else
                                    mutableStateFlow.value = WorkoutUIState.EmptyUrlVideo
                            }
                        }
                        countRetry = 3.0
                    }

                } catch (e: Throwable) {
                    Log.e("query error workout video", "1______________________$e")
                    //Выводит ошибку
                    if (e is CancellationException)
                        Log.e("query error workout video", "cancel______________________$e")
                    else
                        mutableStateFlow.value = WorkoutUIState.Error
                } finally {
                    removeTask()
                }
            }
        }

    //Очищает и отменяет задания
    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }

    fun setId(id: Int) {
        if (id != this.id) {
            this.id = id
            putTask()
        }
    }

    //Изменяет ориентацию программы и сохроняет положение ползунка
    fun changeSizeScreen(orientation: Int, pos: Long) {
        (stateFlow.value as? WorkoutUIState.LoadingState.Content)?.apply {
            mutableStateFlow.value = copy(
                stateScreen = if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    StateScreen.DefaultScreen
                else
                    StateScreen.FullScreen,
                position = pos
            )
        }
    }

    //Устанавливает качество
    fun setQuality(index: Int, pos: Long) {
        (stateFlow.value as? WorkoutUIState.LoadingState.Content)?.apply {
            mutableStateFlow.value = copy(
                quality = when(index) {
                    0 -> EnumQuality.FULL_HD
                    1 -> EnumQuality.HD
                    else -> EnumQuality.SD
                },
                position = pos
            )
        }
    }

    //Сохраняет позицию ползунка
    fun savePosition(pos: Long) {
        (stateFlow.value as? WorkoutUIState.LoadingState.Content)?.apply {
            mutableStateFlow.value = copy(position = pos)
        }
    }

    //Проверяет тип ошибки и вызывает соответствующий диалог ошибки
    fun callAlertForError(networkError: Boolean = false) {
        mutableStateFlow.value = if (networkError) WorkoutUIState.VideoError
            else WorkoutUIState.Error
    }
}
