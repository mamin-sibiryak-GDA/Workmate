package com.example.workmate.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workmate.data.repository.CharacterRepository

// Фабрика ViewModel, используется чтобы передавать зависимость — CharacterRepository
class CharacterViewModelFactory(
    private val repository: CharacterRepository
) : ViewModelProvider.Factory {

    // Метод создаёт экземпляр ViewModel с нужным репозиторием
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверка, что ViewModel запрашивается именно та, которую мы можем создать
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

