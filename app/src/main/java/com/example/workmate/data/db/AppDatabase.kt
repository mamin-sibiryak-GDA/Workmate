package com.example.workmate.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.workmate.model.Character

// Главный класс базы данных Room
// Он связывает сущности (таблицы) с DAO (доступом к данным)
@Database(
    entities = [Character::class], // Список таблиц, которыми управляет БД
    version = 1,                   // Версия БД (увеличивается при миграциях)
    exportSchema = false           // Не экспортировать схему (можно true для документации)
)
abstract class AppDatabase : RoomDatabase() {
    // Абстрактный метод, возвращающий DAO для работы с таблицей персонажей
    abstract fun characterDao(): CharacterDao

}
