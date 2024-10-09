package com.example.todoapp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.data.model.ToDoData

// DiffUtil sınıfı, iki liste arasındaki farkları hesaplar ve UI'da güncelleme yapılmasını sağlar.
// DiffUtil, RecyclerView'da sadece değişen öğelerin güncellenmesini sağlar.
// Örneğin, bir öğe eklenmişse sadece eklenen öğeyi günceller, bir öğe silinmişse sadece silinen öğeyi kaldırır.
class ToDoDiffUtil(
    private val oldList: List<ToDoData>, private val newList: List<ToDoData>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].description == newList[newItemPosition].description
                && oldList[oldItemPosition].priority == newList[newItemPosition].priority
    }
}