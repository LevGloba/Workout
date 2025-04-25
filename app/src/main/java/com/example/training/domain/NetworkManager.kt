package com.example.training.domain

import com.example.training.domain.model.ObserverTask

interface NetworkManager {
    fun putTask(task: ObserverTask)
    fun removeTask(task: ObserverTask)
}