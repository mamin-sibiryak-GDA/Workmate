package com.example.workmate.ui.characterlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.workmate.R
import com.example.workmate.model.Character

// Адаптер для отображения списка персонажей в RecyclerView
class CharacterAdapter(
    private val onItemClick: (Character) -> Unit // Обработчик клика по персонажу
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    // ViewHolder описывает одну карточку персонажа
    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)     // Изображение персонажа
        val nameText: TextView = itemView.findViewById(R.id.nameText)        // Имя персонажа
        val infoText: TextView = itemView.findViewById(R.id.infoText)        // Описание персонажа
    }

    // Создание ViewHolder из XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false) // Подключаем разметку карточки
        return CharacterViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position) // Получаем персонажа по позиции

        // Устанавливаем имя
        holder.nameText.text = character.name

        // Форматируем описание через строку из ресурсов
        holder.infoText.text = holder.itemView.context.getString(
            R.string.character_info,
            character.species,
            character.status,
            character.gender
        )

        // Загружаем изображение с помощью Coil
        holder.imageView.load(character.image) {
            crossfade(true) // Плавная анимация
            placeholder(R.drawable.placeholder) // Заглушка
        }

        // Обработка клика по карточке
        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    // Сравнение элементов списка для ListAdapter
    class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.id == newItem.id // Сравниваем по уникальному ID
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem // Проверяем равенство содержимого
        }
    }
}