package com.example.todoappcompose.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TodoApp(viewModel: TaskViewModel) {
    var taskText by remember { mutableStateOf("") }
    val tasks by viewModel.tasks.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("To-Do List", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                label = { Text("Enter Task") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (taskText.isNotEmpty()) {
                    viewModel.addTask(taskText)
                    taskText = ""
                }
            }) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(tasks.size) { index ->
                TaskItem(
                    task = tasks[index],
                    onCheckedChange = { isDone ->
                        viewModel.updateTaskStatus(tasks[index], isDone)
                    },
                    onDelete = {
                        viewModel.deleteTask(tasks[index])
                    }
                )
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
            task.title?.let {
                Text(
                    text = it,
                    style = if (task.isDone) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}