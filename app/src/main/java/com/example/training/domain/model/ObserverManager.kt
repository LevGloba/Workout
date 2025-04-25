package com.example.training.domain.model

interface ObserverTask {
    fun complete()
}

class ObserverTaskManager(private val body: () -> Unit): ObserverTask {
    override fun complete() = body()
}