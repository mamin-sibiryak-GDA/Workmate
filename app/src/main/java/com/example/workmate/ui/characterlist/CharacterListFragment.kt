package com.example.workmate.ui.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmate.data.api.RetrofitInstance
import com.example.workmate.data.db.AppDatabase
import com.example.workmate.data.repository.CharacterRepository
import com.example.workmate.databinding.FragmentCharacterListBinding
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.room.Room

// Фрагмент отображает список персонажей с поддержкой Pull-to-Refresh и пагинацией
class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CharacterAdapter

    // ViewModel инициализируется вручную (без DI)
    private val viewModel: CharacterViewModel by viewModels {
        val db = Room.databaseBuilder(
            requireContext().applicationContext, // Получаем контекст приложения
            AppDatabase::class.java,              // Класс базы данных
            "app_database"                        // Имя файла БД
        ).build()
        val repo = CharacterRepository(RetrofitInstance.api, db.characterDao()) // Создаём репозиторий
        CharacterViewModelFactory(repo) // Передаём репозиторий во ViewModel через фабрику
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Привязываем layout через ViewBinding
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка адаптера и обработка нажатий на персонажа
        adapter = CharacterAdapter(emptyList()) { character ->
            // Пока просто показываем имя персонажа (в будущем — откроем экран деталей)
            Toast.makeText(requireContext(), character.name, Toast.LENGTH_SHORT).show()
        }

        // RecyclerView с GridLayout на 2 колонки
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        // Слушатель для Pull-to-Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh() // Обновляем данные (сброс на первую страницу)
        }

        // Подписка на список персонажей
        lifecycleScope.launch {
            viewModel.characters.collectLatest { characters ->
                adapter.submitList(characters) // Обновляем данные адаптера
            }
        }

        // Подписка на статус загрузки — для отображения/скрытия индикатора
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.swipeRefreshLayout.isRefreshing = loading
            }
        }

        // Подписка на сообщения об ошибках
        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Слушатель прокрутки — для автозагрузки новых страниц
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                val isAtEnd = lastVisibleItem + 6 >= totalItemCount // Заранее грузим на ~6 элементов до конца
                val shouldLoadMore = isAtEnd && !viewModel.isLoading.value

                if (shouldLoadMore) {
                    viewModel.loadCharacters() // Загружаем следующую страницу
                }
            }
        })

        // Первая загрузка данных
        viewModel.loadCharacters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очищаем binding при уничтожении view
    }
}
