package com.example.workmate.data.api

import com.example.workmate.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс описывает методы для запросов к API "Рик и Морти"
interface CharacterApi {

    // Получение списка персонажей с возможностью фильтрации и пагинации
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,               // Номер страницы (по умолчанию 1)
        @Query("name") name: String? = null,        // Фильтр по имени (опционально)
        @Query("status") status: String? = null,    // Фильтр по статусу
        @Query("species") species: String? = null,  // Фильтр по виду
        @Query("gender") gender: String? = null     // Фильтр по полу
    ): CharacterResponse // Возвращается объект-обёртка с результатами
}
