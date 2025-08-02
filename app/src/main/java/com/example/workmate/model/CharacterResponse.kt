package com.example.workmate.model

// Класс-обёртка для ответа API, содержащего список персонажей и информацию о страницах
data class CharacterResponse(
    val results: List<Character>, // Список персонажей
    val info: PageInfo            // Объект с информацией о пагинации
)

// Вспомогательный класс, описывающий информацию о пагинации
data class PageInfo(
    val count: Int,      // Общее количество персонажей
    val pages: Int,      // Общее количество страниц
    val next: String?,   // URL следующей страницы (может быть null)
    val prev: String?    // URL предыдущей страницы (может быть null)
)
