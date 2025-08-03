package com.example.workmate.ui.characterlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.workmate.R
import com.example.workmate.model.Character

// Адаптер для отображения списка персонажей в RecyclerView
class CharacterAdapter(
    private var characters: List<Character>,              // Список персонажей
    private val onItemClick: (Character) -> Unit          // Обработчик клика по персонажу
) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    // ViewHolder описывает одну карточку персонажа
    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val infoText: TextView = itemView.findViewById(R.id.infoText)
    }

    // Создание ViewHolder из XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position]

        holder.nameText.text = character.name
        holder.infoText.text = "${character.species}, ${character.status}, ${character.gender}"

        // Загрузка изображения через Coil
        holder.imageView.load(character.image) {
            crossfade(true)
            placeholder(R.drawable.placeholder) // заглушка на время загрузки
        }

        // Обработка нажатия на карточку
        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    // Кол-во элементов в списке
    override fun getItemCount(): Int = characters.size

    // Обновление списка данных в адаптере
    fun submitList(newCharacters: List<Character>) {
        characters = newCharacters
        notifyDataSetChanged() // Обновляем UI (в проде лучше использовать DiffUtil)
    }
}
