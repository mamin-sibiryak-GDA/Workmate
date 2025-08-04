package com.example.workmate.ui.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.workmate.data.api.RetrofitInstance
import com.example.workmate.data.db.AppDatabase
import com.example.workmate.data.repository.CharacterRepository
import com.example.workmate.databinding.FragmentCharacterListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.workmate.R

// Фрагмент отображает список персонажей с поддержкой Pull-to-Refresh и фильтра
class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!! // ViewBinding для доступа к XML

    private lateinit var adapter: CharacterAdapter // Адаптер списка

    // Получаем фильтры из аргументов навигации
    private val args: CharacterListFragmentArgs by navArgs()

    // ViewModel с репозиторием (без DI)
    private val viewModel: CharacterViewModel by viewModels {
        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
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

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Подключаем меню
                menuInflater.inflate(R.menu.character_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        // Навигация к фильтру
                        val action = CharacterListFragmentDirections
                            .actionCharacterListFragmentToCharacterFilterFragment()
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Настраиваем адаптер с переходом к деталям по клику
        adapter = CharacterAdapter { character ->
            val action = CharacterListFragmentDirections
                .actionCharacterListFragmentToCharacterDetailFragment(character.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        // Pull-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshCharacters()
        }

        // Пагинация
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem + 5 >= totalItemCount) {
                    viewModel.loadCharacters()
                }
            }
        })

        // Слушаем список персонажей
        lifecycleScope.launch {
            viewModel.characters.collectLatest {
                adapter.submitList(it)
            }
        }

        // Слушаем загрузку
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.swipeRefreshLayout.isRefreshing = loading
            }
        }

        // Слушаем ошибки
        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Применяем фильтры, если они были переданы
        viewModel.applyFilters(
            name = args.name,
            status = args.status,
            species = args.species,
            gender = args.gender
        )


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
