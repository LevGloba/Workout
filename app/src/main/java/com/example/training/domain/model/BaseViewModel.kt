package com.example.training.domain.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.training.domain.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<State>(
    defaultState: State,
    private val networkManager: NetworkManager
) : ViewModel() {
    protected val mutableStateFlow = MutableStateFlow(defaultState)
    val stateFlow = mutableStateFlow.asStateFlow()
    protected abstract val observerTaskManager: ObserverTask
    protected val oneMints = 60_000
    protected var countRetry: Double = 3.0
    protected var job: Job? = null

    protected fun putTask() = networkManager.putTask(observerTaskManager)
    protected fun removeTask() = networkManager.removeTask(observerTaskManager)

    //Положить задачу повторно
    fun retryTask() {
        putTask()
    }
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(context, start, block)
