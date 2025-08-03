package com.example.workmate.ui.characterdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.workmate.data.api.RetrofitInstance
import com.example.workmate.data.db.AppDatabase
import com.example.workmate.data.repository.CharacterRepository
import com.example.workmate.databinding.FragmentCharacterDetailBinding
import com.example.workmate.ui.characterlist.CharacterViewModel
import com.example.workmate.ui.characterlist.CharacterViewModelFactory
import kotlinx.coroutines.launch
import androidx.room.Room

// Фрагмент отображает детали конкретного персонажа
class CharacterDetailFragment : Fragment() {

    private var _binding: FragmentCharacterDetailBinding? = null
    private val binding get() = _binding!! // ViewBinding для доступа к элементам XML

    private val args: CharacterDetailFragmentArgs by navArgs() // Получаем аргумент (ID персонажа)

    // ViewModel создаётся вручную с репозиторием (без DI)
    private val viewModel: CharacterViewModel by viewModels {
        val db = Room.databaseBuilder(
            requireContext().applicationContext, // Контекст
            AppDatabase::class.java,              // Класс БД
            "app_database"                        // Имя файла
        ).build()
        val repo = CharacterRepository(RetrofitInstance.api, db.characterDao())
        CharacterViewModelFactory(repo) // Фабрика с передачей репозитория
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false) // Инфлейтим layout через ViewBinding
        return binding.root // Возвращаем корневой элемент
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Загружаем персонажа по ID, переданному через SafeArgs
        lifecycleScope.launch {
            try {
                val character = RetrofitInstance.api.getCharacterDetail(args.characterId) // Запрос по ID

                // Отображаем данные персонажа в UI
                binding.imageView.load(character.image) // Загружаем изображение через Coil
                binding.nameText.text = character.name
                binding.statusText.text = "Status: ${character.status}"
                binding.speciesText.text = "Species: ${character.species}"
                binding.genderText.text = "Gender: ${character.gender}"
                binding.originText.text = "Origin: ${character.origin.name}"
                binding.locationText.text = "Location: ${character.location.name}"

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка загрузки персонажа", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очищаем ViewBinding, чтобы избежать утечек памяти
    }
}
