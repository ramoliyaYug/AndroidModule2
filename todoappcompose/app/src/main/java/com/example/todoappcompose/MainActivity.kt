package com.example.todoappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoappcompose.ui.theme.TaskViewModel
import com.example.todoappcompose.ui.theme.TodoApp
import com.example.todoappcompose.ui.theme.TodoappcomposeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskViewModel(application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoappcomposeTheme {
                TodoApp(viewModel)
            }
        }
    }
}