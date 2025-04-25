package com.example.training.views.workouts_listed

import android.util.Log
import com.example.training.R
import com.example.training.domain.EnumWorkoutType
import com.example.training.domain.EnumWorkoutType.ALL
import com.example.training.domain.EnumWorkoutType.COMPLEX
import com.example.training.domain.EnumWorkoutType.ETHER
import com.example.training.domain.EnumWorkoutType.WORKOUT
import com.example.training.domain.NetworkManager
import com.example.training.domain.Requests
import com.example.training.domain.model.BaseViewModel
import com.example.training.domain.model.ObserverTask
import com.example.training.domain.model.ObserverTaskManager
import com.example.training.domain.model.Workout
import com.example.training.domain.model.WorkoutItemUI
import com.example.training.domain.model.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.pow

sealed class WorkoutsListedUIState {
    sealed class LoadingState: WorkoutsListedUIState() {
        //Отображает состояние загрузки
        data object Loading : LoadingState()

        //Отображает контент
        data class Content(
            val listWorkouts: List<WorkoutItemUI> = emptyList(),
            val filter: EnumWorkoutType = ALL,
            val searchText: String = ""
        ) : LoadingState()
    }

    //Начальное состояние, ничего не отображает
    data object Empty: WorkoutsListedUIState()

    //Отображает ошибку
    data object Error: WorkoutsListedUIState()

    //Отображает, что данных нет
    data object EmptyList: WorkoutsListedUIState()
}

@HiltViewModel
class WorkoutsListedViewModel @Inject constructor(
    private val requests: Requests,
    networkManager: NetworkManager
) :
    BaseViewModel<WorkoutsListedUIState>(WorkoutsListedUIState.Empty, networkManager) {

     private var jobFilter: Job? = null

    //Изначальный список тренировок
    private var defaultList = emptyList<WorkoutItemUI>()

    //Задача отправляемая в networkManager
    override val observerTaskManager: ObserverTask = ObserverTaskManager{
            launch {
                //Запрашивает список тренировок
                try {
                    //Отображает загрузку
                    mutableStateFlow.value = WorkoutsListedUIState.LoadingState.Loading
                    //Проверяет, если данные пришли пустыми, то, через время повторный запрос
                    while (countRetry > 0) {
                        delay(500)
                        defaultList = requests.getWorkoutList().prepare()
                        if (defaultList.isNotEmpty()) {
                            mutableStateFlow.value = WorkoutsListedUIState.LoadingState.Content(
                                listWorkouts = defaultList
                            )
                            countRetry = .0
                        } else {
                            countRetry--
                            if (countRetry > 0)
                                delay((oneMints / countRetry.pow(countRetry)).toLong())
                            else
                                mutableStateFlow.value = WorkoutsListedUIState.EmptyList
                        }
                    }
                    countRetry = 3.0
                } catch (e: Throwable) {
                    Log.e("query error", "1______________________$e")
                    //Выводит ошибку
                    mutableStateFlow.value = WorkoutsListedUIState.Error
                } finally {
                    removeTask()
                }
            }
        }


    //Кладет задачу в NetworkManager
    init {
        putTask()
    }

    // Устанавливает фильтр
    fun setFilterList(numberChip: Int) {
        jobFilter?.cancel()
        jobFilter = launch {
            val type = when (numberChip) {
                R.id.workout_button -> WORKOUT
                R.id.complex_button -> COMPLEX
                R.id.ether_button -> ETHER
                else -> ALL
            }

            mutableStateFlow.value = WorkoutsListedUIState.LoadingState.Content().copy(
                listWorkouts = if (type == ALL) defaultList
                else defaultList.filter { it.typeUI == type },
                filter = type
            )
        }
    }

    //Фильтрация спсика по названию тернировки
    fun searchByText(string: String) {
        jobFilter?.cancel()
        jobFilter = launch {
            delay(1000)
            mutableStateFlow.value = WorkoutsListedUIState.LoadingState.Content().copy(
                listWorkouts = defaultList.filter { it.title.lowercase().contains(string.lowercase()) },
                searchText = string
            )
        }
    }

    //Возвращения изначального списка
    fun returnDefaultListWorkouts() = launch {
        mutableStateFlow.value = WorkoutsListedUIState.LoadingState.Content().copy(
            listWorkouts = defaultList
        )
    }
}

//Подготавливает данные для отображения в ui
private fun List<Workout>.prepare(): List<WorkoutItemUI> {
    val mut = mutableListOf<WorkoutItemUI>()
    forEach {
        mut.add(it.prepare())
    }
    return mut
}

private fun Workout.prepare(): WorkoutItemUI =
    WorkoutItemUI(
        id = id,
        title = title,
        description = description.orEmpty(),
        typeUI = returnType(type),
        time = if (duration.contains(Regex("^\\d+\$"))) duration else "<1"
    )


//Определяет тип тренировки по числу для ui
private fun returnType(typeInt: Int): EnumWorkoutType {
    return when(typeInt) {
        1 -> WORKOUT
        2 -> ETHER
        3 -> COMPLEX
        else -> WORKOUT
    }
}