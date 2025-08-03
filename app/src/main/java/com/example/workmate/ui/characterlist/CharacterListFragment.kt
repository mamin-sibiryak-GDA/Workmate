package com.example.workmate.ui.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.workmate.data.api.RetrofitInstance
import com.example.workmate.data.db.AppDatabase
import com.example.workmate.data.repository.CharacterRepository
import com.example.workmate.databinding.FragmentCharacterListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Фрагмент отображает список персонажей с поддержкой Pull-to-Refresh
class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!! // Привязка к XML через ViewBinding

    private lateinit var adapter: CharacterAdapter // Адаптер с поддержкой ListAdapter

    // Создаём ViewModel вручную (без DI), передаём репозиторий
    private val viewModel: CharacterViewModel by viewModels {
        val db = Room.databaseBuilder(
            requireContext().applicationContext,       // Контекст приложения
            AppDatabase::class.java,                   // Класс базы данных
            "app_database"                             // Имя файла базы
        ).build()
        val repo = CharacterRepository(RetrofitInstance.api, db.characterDao())
        CharacterViewModelFactory(repo) // Фабрика для ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false) // Инфлейтим XML с помощью ViewBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка адаптера и обработка кликов
        adapter = CharacterAdapter { character ->
            // Переход к деталям персонажа через Navigation Component
            val action = CharacterListFragmentDirections
                .actionCharacterListFragmentToCharacterDetailFragment(character.id)
            findNavController().navigate(action)
        }

        // Настройка RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Сетка 2 колонки
        binding.recyclerView.adapter = adapter // Назначаем адаптер

        // Обработка Pull-to-Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshCharacters() // Обновить данные вручную
        }

        // Подписка на список персонажей
        lifecycleScope.launch {
            viewModel.characters.collectLatest { list ->
                adapter.submitList(list) // Передаём список в ListAdapter
            }
        }

        // Подписка на индикатор загрузки
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.swipeRefreshLayout.isRefreshing = isLoading // Показываем/скрываем спиннер
            }
        }

        // Подписка на ошибки
        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() // Показываем ошибку
                }
            }
        }

        // Пагинация при достижении конца списка
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem + 5 >= totalItemCount) {
                    viewModel.loadCharacters() // Загружаем следующую страницу
                }
            }
        })

        // Загрузить первую страницу при открытии экрана
        viewModel.loadCharacters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Освобождаем привязку во избежание утечек памяти
    }
}
