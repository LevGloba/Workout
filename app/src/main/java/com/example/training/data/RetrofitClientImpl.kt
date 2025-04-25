package com.example.training.data

import com.example.training.Constants
import com.example.training.domain.RetrofitClient
import com.example.training.domain.RetrofitServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientImpl  : RetrofitClient {

    private var retrofit: Retrofit? = null

    //Создаем службу
    private val retrofitService: RetrofitServices = getClient().create(RetrofitServices::class.java)

    //Создает клиента
    private fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    override fun getRetrofitService(): RetrofitServices = retrofitService
}