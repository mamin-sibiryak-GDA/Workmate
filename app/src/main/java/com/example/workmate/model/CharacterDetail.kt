package com.example.workmate.model

// Поля соответствуют JSON-ответу API /character/{id}
data class CharacterDetail(
    val id: Int,                 // ID персонажа
    val name: String,           // Имя
    val status: String,         // Статус (Alive, Dead, Unknown)
    val species: String,        // Вид
    val gender: String,         // Пол
    val origin: LocationInfo,   // Родная планета (объект с полем name)
    val location: LocationInfo, // Текущая локация
    val image: String           // Ссылка на изображение
)