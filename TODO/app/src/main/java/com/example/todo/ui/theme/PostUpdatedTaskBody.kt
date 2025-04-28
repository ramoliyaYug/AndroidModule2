package com.example.todo.ui.theme

data class PostUpdatedTaskBody(
    var action : String = "update",
    var uniqueId : String,
    var updates : Updates
) {
}


data class Updates(
    var isDone : Boolean,
    var isDeleted : Boolean
)