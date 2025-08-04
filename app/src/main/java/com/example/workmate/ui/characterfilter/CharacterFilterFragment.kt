package com.example.workmate.ui.characterfilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.workmate.databinding.FragmentCharacterFilterBinding

// Экран фильтрации персонажей по имени, статусу, виду и полу
class
CharacterFilterFragment : Fragment() {

    // ViewBinding для доступа к XML
    private var _binding: FragmentCharacterFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инфлейтим layout через ViewBinding
        _binding = FragmentCharacterFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка "Применить фильтр"
        binding.buttonApply.setOnClickListener {
            // Чтение введённых фильтров
            val name = binding.editName.text.toString().takeIf { it.isNotBlank() }
            val status = binding.editStatus.text.toString().takeIf { it.isNotBlank() }
            val species = binding.editSpecies.text.toString().takeIf { it.isNotBlank() }
            val gender = binding.editGender.text.toString().takeIf { it.isNotBlank() }

            // Возвращаемся к списку с результатами фильтрации
            val action = CharacterFilterFragmentDirections
                .actionCharacterFilterFragmentToCharacterListFragment(
                    name = name,
                    status = status,
                    species = species,
                    gender = gender
                )
            findNavController().navigate(action)
        }

        // Кнопка "Очистить"
        binding.buttonClear.setOnClickListener {
            binding.editName.setText("")
            binding.editStatus.setText("")
            binding.editSpecies.setText("")
            binding.editGender.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очищаем привязку
    }
}
