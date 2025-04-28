package com.example.todo.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}


class MainViewModel : ViewModel() {
    private val _tasksList = MutableStateFlow<UiState<ArrayList<Task>>>(UiState.Loading)
    val tasksList: StateFlow<UiState<ArrayList<Task>>> get() = _tasksList


    val taskText = MutableStateFlow("")


    init {
        fetchPosts()
    }


    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                _tasksList.value = UiState.Loading
                val tasksListData = RetrofitInstance.apiService.getTasks()
                _tasksList.value = UiState.Success(ArrayList(tasksListData))
            } catch (e: Exception) {
                e.printStackTrace()
                _tasksList.value = UiState.Error(e)
            }
        }
    }


    fun addTask() {
        if (taskText.value.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val data = RetrofitInstance.apiService.postTask(
                        PostTaskBody(title = taskText.value , isDone = false , isDeleted = false)
                    )
                    val newTask = Task(title = taskText.value, uniqueId = data.id)
                    if (_tasksList.value is UiState.Success) {
                        val currentTasks = (_tasksList.value as UiState.Success).data
                        currentTasks.add(newTask)
                        _tasksList.value = UiState.Success(currentTasks)
                        taskText.value = ""
                    }
                } catch (e : Exception) {


                }
            }
        }
    }


    fun updateTask(uniqueId: String, isDone: Boolean) {
        if (_tasksList.value is UiState.Success) {
            val currentTasks = (_tasksList.value as UiState.Success).data
            val index = currentTasks.indexOfFirst { it.uniqueId == uniqueId }
            if (index != -1) {
                val updatedTask = currentTasks[index].copy(isDone = isDone)
                val updatedTasks = currentTasks.toMutableList().apply {
                    set(index, updatedTask)
                }
                _tasksList.value = UiState.Success(ArrayList(updatedTasks))
            }
        }
        viewModelScope.launch {
            try {
                RetrofitInstance.apiService.postUpdatesTask(
                    PostUpdatedTaskBody(uniqueId = uniqueId , updates = Updates(isDone = isDone , isDeleted = false))
                )
            } catch (e : Exception) {


            }
        }
    }


    fun deleteTask(uniqueId: String) {
        if (_tasksList.value is UiState.Success) {
            val currentTasks = (_tasksList.value as UiState.Success).data
            val index = currentTasks.indexOfFirst { it.uniqueId == uniqueId }
            if (index != -1) {
                val updatedTask = currentTasks[index].copy(isDeleted = true)
                val updatedTasks = currentTasks.toMutableList().apply {
                    set(index, updatedTask)
                }
                _tasksList.value = UiState.Success(ArrayList(updatedTasks))
            }
            viewModelScope.launch {
                try {
                    RetrofitInstance.apiService.postUpdatesTask(
                        PostUpdatedTaskBody(uniqueId = uniqueId , updates = Updates(isDone = false , isDeleted = true))
                    )
                } catch (e : Exception) {


                }
            }
        }
    }
    fun refreshTasks() {
        fetchPosts()
    }
}
