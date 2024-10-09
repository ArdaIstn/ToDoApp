package com.example.todoapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.data.converter.Converter
import com.example.todoapp.data.model.ToDoData


@Database(entities = [ToDoData::class], version = 1)
@TypeConverters(Converter::class)
abstract class DataBase : RoomDatabase() {
    abstract fun getToDoDao(): ToDoDao
}