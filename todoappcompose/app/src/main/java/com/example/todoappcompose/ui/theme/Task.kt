package com.example.todoappcompose.ui.theme

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int ?= null,
    val title:String ?= null,
    var isDone: Boolean = false
)
