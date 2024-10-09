package com.example.todoapp.data.repository

import com.example.todoapp.data.datasource.ToDoDataSource
import com.example.todoapp.data.model.ToDoData
import kotlinx.coroutines.flow.Flow

class ToDoRepository(private val toDoDataSource: ToDoDataSource) {

    fun getAllData(): Flow<List<ToDoData>> {
        return toDoDataSource.getAllData()
    }

    fun sortByHighPriority(): Flow<List<ToDoData>> {
        return toDoDataSource.sortByHighPriority()
    }

    fun sortByLowPriority(): Flow<List<ToDoData>> {
        return toDoDataSource.sortByLowPriority()
    }

    suspend fun searchDatabase(query: String): List<ToDoData> {
        return toDoDataSource.searchDatabase(query)
    }

    suspend fun insertData(toDoData: ToDoData) {
        toDoDataSource.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData) {
        toDoDataSource.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData) {
        toDoDataSource.deleteData(toDoData)
    }

    suspend fun deleteAllData() {
        toDoDataSource.deleteAllData()
    }

}