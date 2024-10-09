package com.example.todoapp.data.converter

import androidx.room.TypeConverter
import com.example.todoapp.data.model.Priority


class Converter {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }
    // Priority türü String türüne dönüştürülür ve veritabanında String olarak saklanır.

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
    // String olarak saklanan bu değerler tekrar Priority türüne dönüştürülür ve kullanılır.


}