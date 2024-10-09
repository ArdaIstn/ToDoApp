package com.example.todoapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.model.ToDoData
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    // Get all the data from the database
    @Query("SELECT * FROM ToDoTable ORDER BY id ASC")
    fun getAllData(): Flow<List<ToDoData>>

    // Insert the data object
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(toDoData: ToDoData)

    // Update the data object
    @Update
    suspend fun updateData(toDoData: ToDoData)

    // Delete the data object
    @Delete
    suspend fun deleteData(toDoData: ToDoData)

    // Delete all the data from the database
    @Query("DELETE FROM ToDoTable")
    suspend fun deleteAllData()

    // Search function to get data that matches the search query
    @Query("SELECT * FROM ToDoTable WHERE title LIKE :searchQuery")
    suspend fun searchDatabase(searchQuery: String): List<ToDoData>

    // Sort functions to get data in different order
    @Query("SELECT * FROM ToDoTable ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END ")
    fun sortByHighPriority(): Flow<List<ToDoData>>

    // Sort functions to get data in different order
    @Query("SELECT * FROM ToDoTable ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END ")
    fun sortByLowPriority(): Flow<List<ToDoData>>


}