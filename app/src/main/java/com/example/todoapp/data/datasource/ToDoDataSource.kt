package com.example.todoapp.data.datasource

import com.example.todoapp.data.model.ToDoData
import com.example.todoapp.room.ToDoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class ToDoDataSource(private val toDoDao: ToDoDao) {

    fun getAllData(): Flow<List<ToDoData>> {
        return toDoDao.getAllData()
    }

    fun sortByHighPriority(): Flow<List<ToDoData>> {
        return toDoDao.sortByHighPriority()
    }

    fun sortByLowPriority(): Flow<List<ToDoData>> {
        return toDoDao.sortByLowPriority()
    }

    suspend fun searchDatabase(query: String): List<ToDoData> = withContext(Dispatchers.IO) {
        toDoDao.searchDatabase(query)
    }

    suspend fun insertData(toDoData: ToDoData) {
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData) {
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData) {
        toDoDao.deleteData(toDoData)
    }

    suspend fun deleteAllData() {
        toDoDao.deleteAllData()
    }


}