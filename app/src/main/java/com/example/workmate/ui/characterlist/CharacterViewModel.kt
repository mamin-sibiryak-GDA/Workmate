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

    // Храним номер текущей страницы
    private var currentPage = 1

    // Флаг, что достигли последней страницы (info.next == null)
    private var isLastPage = false

    // Метод запуска загрузки данных (например, при запуске экрана или скролле до конца)
    fun loadCharacters(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ) {
        // Если уже идёт загрузка или достигли конца, ничего не делаем
        if (_isLoading.value || isLastPage) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Загружаем текущую страницу
                val response = repository.refreshCharacters(
                    page = currentPage,
                    name = name,
                    status = status,
                    species = species,
                    gender = gender
                )

                // Если следующей страницы нет, выставляем флаг
                if (response.info.next == null) {
                    isLastPage = true
                } else {
                    currentPage++ // иначе увеличиваем счётчик страницы
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Метод для перезагрузки списка с первой страницы (например, по свайпу)
    fun refresh() {
        currentPage = 1
        isLastPage = false
        loadCharacters()
    }
}

