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
    private val onItemClick: (Character) -> Unit          // Обработчик клика по персонажу
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    // ViewHolder описывает одну карточку персонажа
    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)   // Картинка персонажа
        val nameText: TextView = itemView.findViewById(R.id.nameText)      // Имя персонажа
        val infoText: TextView = itemView.findViewById(R.id.infoText)      // Информация (вид, статус, пол)
    }

    // Создание ViewHolder из XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position) // Получаем персонажа из списка

        holder.nameText.text = character.name // Устанавливаем имя

        // Используем ресурсную строку для infoText (вместо конкатенации)
        holder.infoText.text = holder.itemView.context.getString(
            R.string.character_info,
            character.species,
            character.status,
            character.gender
        )

        // Загрузка изображения через Coil
        holder.imageView.load(character.image) {
            crossfade(true)                               // Анимация появления
            placeholder(R.drawable.placeholder)           // Заглушка на время загрузки
        }

        // Обработка нажатия на карточку — вызываем коллбек
        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    // ViewHolder используют этот класс для определения отличий между элементами
    class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.id == newItem.id // Элементы те же, если совпадает ID
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem // Содержимое совпадает — можно не обновлять
        }
    }
}
