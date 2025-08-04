package com.example.workmate.ui.characterlist

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.workmate.R
import com.example.workmate.model.Character

// Адаптер для отображения списка персонажей
class CharacterAdapter(
    private val onItemClick: (Character) -> Unit
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    // ViewHolder описывает одну карточку
    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val infoText: TextView = itemView.findViewById(R.id.infoText)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
        val statusDot: View = itemView.findViewById(R.id.statusDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)

        holder.nameText.text = character.name

        holder.infoText.text = holder.itemView.context.getString(
            R.string.character_info,
            character.species,
            character.gender
        )

        holder.imageView.load(character.image) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
        }

        // Настройка текста и цвета статуса
        holder.statusText.text = character.status

        val dotColor = when (character.status.lowercase()) {
            "alive" -> R.color.green
            "dead" -> R.color.red
            else -> R.color.gray
        }

        val background = holder.statusDot.background as GradientDrawable
        background.setColor(ContextCompat.getColor(holder.itemView.context, dotColor))

        // Обработка клика по элементу
        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem
        }
    }
}
