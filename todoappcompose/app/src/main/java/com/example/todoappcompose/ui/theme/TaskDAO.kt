package com.example.todoappcompose.ui.theme

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDAO {
    @Insert
    suspend fun insert(task: Task)

    @Query("SELECT * FROM Task")
    suspend fun getAll(): List<Task>

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)
}