package com.example.workmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Класс-модель одного персонажа из API "Рик и Морти"
// Также используется как Entity (таблица) в базе данных Room
@Entity(tableName = "characters")
data class Character(
    @PrimaryKey val id: Int,      // Уникальный идентификатор персонажа (ключ в таблице)
    val name: String,             // Имя персонажа
    val status: String,           // Статус (Alive, Dead, unknown)
    val species: String,          // Вид (например, Human, Alien)
    val gender: String,           // Пол (Male, Female, Genderless, unknown)
    val image: String             // Ссылка на изображение персонажа
)