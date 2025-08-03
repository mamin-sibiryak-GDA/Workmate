package com.example.workmate.ui.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.workmate.data.api.RetrofitInstance
import com.example.workmate.data.db.AppDatabase
import com.example.workmate.data.repository.CharacterRepository
import com.example.workmate.databinding.FragmentCharacterListBinding
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.room.Room

// Фрагмент отображает список персонажей с поддержкой Pull-to-Refresh
class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CharacterAdapter

    // Создаём ViewModel вручную (без DI), передаём репозиторий
    private val viewModel: CharacterViewModel by viewModels {
        val db = Room.databaseBuilder(
            requireContext().applicationContext, // Контекст приложения
            AppDatabase::class.java,              // Класс базы данных
            "app_database"                        // Имя файла базы
        ).build() // заменим на нормальную инициализацию позже
        val repo = CharacterRepository(RetrofitInstance.api, db.characterDao())
        CharacterViewModelFactory(repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        adapter = CharacterAdapter(emptyList()) { character ->
            // TODO: Обработка нажатия на персонажа (открыть детали)
            Toast.makeText(requireContext(), character.name, Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        // Обработка Pull-to-Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadCharacters() // обновить список
        }

        // Подписка на данные из ViewModel
        lifecycleScope.launch {
            viewModel.characters.collectLatest {
                adapter.submitList(it)
            }
        }

        // Подписка на загрузку
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.swipeRefreshLayout.isRefreshing = loading
            }
        }

        // Подписка на ошибки
        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Загрузить данные при первом открытии
        viewModel.loadCharacters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
