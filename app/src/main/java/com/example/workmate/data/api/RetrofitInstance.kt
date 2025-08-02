package com.example.workmate.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton-объект, создающий и хранящий инстанс Retrofit
object RetrofitInstance {

    private const val BASE_URL = "https://rickandmortyapi.com/api/" // Базовый URL API

    // Создание ленивого Retrofit-инстанса и клиента API
    val api: CharacterApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Установка базового URL
            .addConverterFactory(GsonConverterFactory.create()) // Указание конвертера JSON -> Kotlin объекты
            .build() // Сборка Retrofit-инстанса
            .create(CharacterApi::class.java) // Создание реализации интерфейса CharacterApi
    }
}
