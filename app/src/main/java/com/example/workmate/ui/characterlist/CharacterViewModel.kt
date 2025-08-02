package com.example.workmate.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workmate.data.repository.CharacterRepository
import com.example.workmate.model.Character
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ViewModel — связывает Repository и UI, управляет состоянием экрана
class CharacterViewModel(
    private val repository: CharacterRepository // Инъекция репозитория
) : ViewModel() {

    // UI-состояние: список персонажей
    val characters: StateFlow<List<Character>> = repository.characters
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // UI-состояние: индикатор загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // UI-состояние: сообщение об ошибке (если есть)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Метод запуска загрузки данных (например, при запуске экрана или pull-to-refresh)
    fun loadCharacters(
        page: Int = 1,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true          // Показать прогресс
                _errorMessage.value = null       // Сбросить ошибку

                repository.refreshCharacters(
                    page = page,
                    name = name,
                    status = status,
                    species = species,
                    gender = gender
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message  // Показать ошибку
            } finally {
                _isLoading.value = false         // Скрыть прогресс
            }
        }
    }
}
