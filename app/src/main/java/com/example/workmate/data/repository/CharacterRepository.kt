package com.example.workmate.data.repository

import com.example.workmate.data.api.CharacterApi
import com.example.workmate.data.db.CharacterDao
import com.example.workmate.model.Character
import com.example.workmate.model.CharacterResponse
import kotlinx.coroutines.flow.Flow

// Репозиторий объединяет API и локальную БД (Room)
class CharacterRepository(
    private val api: CharacterApi,       // Клиент API (Retrofit)
    private val dao: CharacterDao        // Интерфейс доступа к БД
) {
    // Получение списка персонажей из базы (наблюдаемый Flow)
    val characters: Flow<List<Character>> = dao.getAllCharacters()

    // Обновление персонажей: сначала получаем с сервера, потом сохраняем в БД
    suspend fun refreshCharacters(
        page: Int = 1,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ): CharacterResponse {
        // 1. Загружаем список с сервера с учётом фильтров
        val response = api.getCharacters(page, name, status, species, gender)

        // 2. Очищаем текущую таблицу (если это первая страница)
        if (page == 1) dao.clearAll()

        // 3. Сохраняем новые данные в локальную БД
        dao.insertAll(response.results)

        // 4. Возвращаем ответ с info.next
        return response
    }

}
