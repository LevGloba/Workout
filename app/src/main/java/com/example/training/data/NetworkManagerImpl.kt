package com.example.training.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.training.domain.NetworkManager
import com.example.training.domain.model.ObserverTask
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

//Нужен для управления задачами, которые выполняют запросы в сеть
class NetworkManagerImpl @Inject constructor(@ApplicationContext private val context: Context): NetworkManager {

    private val queueTask =  LinkedBlockingQueue<ObserverTask>()

    override fun putTask(task: ObserverTask) {
        queueTask.add(task)
        if (isOnline())
            callFunctionCompleteTask()
    }

    //Выполняет список задач по FIFO
    private fun callFunctionCompleteTask() {
        queueTask.reversed().forEach {
            it.complete()
        }

    }

    override fun removeTask(task: ObserverTask) {
        queueTask.remove(task)
    }

    //Проверяет подключение к сети, но не доступ
    private fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}