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

    // UI-состояние: список персонажей, получаемый из базы через Flow
    val characters: StateFlow<List<Character>> = repository.characters
        .stateIn(
            viewModelScope,                            // Область жизни — ViewModel
            SharingStarted.WhileSubscribed(5000),      // Делимся потоком пока есть подписчики
            emptyList()                                // Начальное значение — пустой список
        )

    // Состояние загрузки (отображается спиннер)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading // Для подписки во View

    // Сообщение об ошибке (если загрузка провалится)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage // Для показа Toast

    // Текущая страница для пагинации
    private var currentPage = 1

    // Флаг достижения конца списка (если info.next == null)
    private var isLastPage = false

    // Храним текущие параметры фильтра
    private var currentName: String? = null
    private var currentStatus: String? = null
    private var currentSpecies: String? = null
    private var currentGender: String? = null

    // Метод для применения новых фильтров (вызывается из UI)
    fun applyFilters(
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ) {
        currentPage = 1                // Сброс на первую страницу
        isLastPage = false             // Сброс флага конца
        currentName = name             // Сохраняем параметры фильтрации
        currentStatus = status
        currentSpecies = species
        currentGender = gender
        loadCharacters()               // Загружаем с новыми параметрами
    }

    // Метод загрузки следующей страницы (или первой при фильтрации)
    fun loadCharacters() {
        // Если уже загружаем или конец списка — выходим
        if (_isLoading.value || isLastPage) return

        viewModelScope.launch {
            _isLoading.value = true           // Показываем индикатор загрузки
            _errorMessage.value = null       // Сбрасываем ошибку

            try {
                // Загружаем данные из API через репозиторий
                val response = repository.refreshCharacters(
                    page = currentPage,
                    name = currentName,
                    status = currentStatus,
                    species = currentSpecies,
                    gender = currentGender
                )

                // Если следующей страницы нет, устанавливаем флаг
                isLastPage = response.info.next == null

                // Если есть ещё страницы — увеличиваем счётчик
                if (!isLastPage) currentPage++

            } catch (e: Exception) {
                _errorMessage.value = e.message // Показываем сообщение об ошибке
            } finally {
                _isLoading.value = false        // Скрываем спиннер
            }
        }
    }

    // Метод для обновления списка (например, по свайпу вниз)
    fun refreshCharacters() {
        currentPage = 1                 // Начинаем сначала
        isLastPage = false             // Сбрасываем флаг конца
        loadCharacters()               // Загружаем первую страницу
    }
}
