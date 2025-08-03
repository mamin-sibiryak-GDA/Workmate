package com.example.workmate.ui.characterlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
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
        val imageView: ImageView = itemView.findViewById(R.id.imageView)       // Аватарка
        val nameText: TextView = itemView.findViewById(R.id.nameText)          // Имя персонажа
        val infoText: TextView = itemView.findViewById(R.id.infoText)          // Вид, статус, пол
    }

    // Создание ViewHolder из XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false) // Подключаем макет item_character.xml
        return CharacterViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position] // Получаем персонажа по позиции

        holder.nameText.text = character.name // Устанавливаем имя персонажа

        // Устанавливаем описание (вид, статус, пол) через строку из ресурсов
        holder.infoText.text = holder.itemView.context.getString(
            R.string.character_info,           // Строка: "%1$s, %2$s, %3$s"
            character.species,
            character.status,
            character.gender
        )

        // Загрузка изображения через Coil
        holder.imageView.load(character.image) {
            crossfade(true)                    // Плавная анимация появления
            placeholder(R.drawable.placeholder) // Заглушка на время загрузки
        }

        // Обработка нажатия на карточку
        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    // Кол-во элементов в списке
    override fun getItemCount(): Int = characters.size

    // Обновление списка данных с использованием DiffUtil (вместо notifyDataSetChanged)
    fun submitList(newCharacters: List<Character>) {
        val diffCallback = CharacterDiffCallback(characters, newCharacters) // Сравниваем старый и новый списки
        val diffResult = DiffUtil.calculateDiff(diffCallback)               // Вычисляем отличия

        characters = newCharacters                                          // Обновляем список
        diffResult.dispatchUpdatesTo(this)                                  // Применяем изменения в RecyclerView
    }

    // Класс сравнения элементов списка для DiffUtil
    class CharacterDiffCallback(
        private val oldList: List<Character>,     // Старый список
        private val newList: List<Character>      // Новый список
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        // Сравниваем по id (один и тот же персонаж?)
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        // Сравниваем по содержимому (есть ли изменения?)
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
