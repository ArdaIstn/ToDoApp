package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.ToDoData
import com.example.todoapp.data.repository.ToDoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Burda temel listeleme işlemleri yapılacaktır.
// Delete veya update durumlarında anlık olarak gösterilecektir.

@HiltViewModel
class ListViewModel @Inject constructor(private val toDoRepository: ToDoRepository) : ViewModel() {


    private val _toDoList = MutableStateFlow<List<ToDoData>>(emptyList())
    val toDoList: StateFlow<List<ToDoData>> = _toDoList.asStateFlow()


    private val _searchedList = MutableLiveData<List<ToDoData>>()
    val searchedList: LiveData<List<ToDoData>>
        get() = _searchedList


    init {
        getAllData()
    }

    fun sortByHighPriority() {
        viewModelScope.launch {
            toDoRepository.sortByHighPriority().collect { toDoList ->
                _toDoList.value = toDoList
            }
        }
    }

    fun sortByLowPriority() {
        viewModelScope.launch {
            toDoRepository.sortByLowPriority().collect { toDoList ->
                _toDoList.value = toDoList
            }
        }
    }

    private fun getAllData() {
        viewModelScope.launch {
            toDoRepository.getAllData().collect { toDoList ->
                _toDoList.value = toDoList
            }
        }
    }

    fun searchDataByQuery(query: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val searchResult = toDoRepository.searchDatabase(query)
            _searchedList.value = searchResult
        }
    }


    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.Main) {
            toDoRepository.deleteAllData()
        }
    }

    fun deleteData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.Main) {
            toDoRepository.deleteData(toDoData)
        }

    }

    fun insertData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.Main) {
            toDoRepository.insertData(toDoData)
        }

    }

}
