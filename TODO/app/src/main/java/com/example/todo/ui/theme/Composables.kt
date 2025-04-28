package com.example.todo.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


@Composable
fun TodoApp(viewModel: MainViewModel) {
    val tasksListState by viewModel.tasksList.collectAsState()
    val taskText by viewModel.taskText.collectAsState()


    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Text("To-Do List", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.refreshTasks() }) {
                Text("refresh")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                value = taskText,
                onValueChange = { viewModel.taskText.value = it },
                label = { Text("Enter Task") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.addTask() }) {
                Text("Add")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))


        when (tasksListState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val tasks = (tasksListState as UiState.Success<ArrayList<Task>>).data.filter { !it.isDeleted }
                LazyColumn {
                    items(tasks) { task ->
                        if(task.isDeleted == false ) {
                            TaskItem(
                                task = task,
                                onCheckedChange = { isDone ->
                                    viewModel.updateTask(
                                        task.uniqueId,
                                        isDone
                                    )
                                },
                                onDelete = { viewModel.deleteTask(task.uniqueId) }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                Text("Error loading tasks", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Composable
fun TaskItem(task: Task, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onCheckedChange
            )
            Text(
                text = task.title,
                style = if (task.isDone) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }


        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    //TodoApp()
}
