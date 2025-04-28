package com.example.todo.ui.theme

data class Task(
    val title: String,
    var isDone: Boolean = false,
    var uniqueId :String,
    var isDeleted : Boolean = false
)
