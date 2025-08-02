package com.example.workmate.data.db

import androidx.room.*
import com.example.workmate.model.Character
import kotlinx.coroutines.flow.Flow

// Интерфейс описывает операции с таблицей персонажей
@Dao
interface CharacterDao {

    // Вставка списка персонажей. При конфликте по id — заменит старое значение.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<Character>)

    // Удаление всех записей из таблицы
    @Query("DELETE FROM characters")
    suspend fun clearAll()

    // Получение всех персонажей. Flow позволяет слушать изменения в БД.
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<Character>>
}
