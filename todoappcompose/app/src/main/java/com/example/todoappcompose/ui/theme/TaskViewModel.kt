// TaskViewModel.kt
package com.example.todoappcompose.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        TaskDatabase::class.java,
        "task_database"
    ).build()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = database.taskDao().getAll()
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            val task = Task(title = title)
            database.taskDao().insert(task)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            database.taskDao().delete(task)
            loadTasks()
        }
    }

    fun updateTaskStatus(task: Task, isDone: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = isDone)
            database.taskDao().delete(task)
            database.taskDao().insert(updatedTask)
            loadTasks()
        }
    }
}