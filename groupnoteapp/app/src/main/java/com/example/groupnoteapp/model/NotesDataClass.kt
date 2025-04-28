package com.example.groupnoteapp.model

data class NotesDataClass(
    val userName : String ?= null,
    val noteId: String? = null,
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
