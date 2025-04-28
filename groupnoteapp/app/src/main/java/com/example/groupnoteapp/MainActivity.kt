package com.example.groupnoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupnoteapp.helpers.SharedPrefHelper
import com.example.groupnoteapp.view.*
import com.example.groupnoteapp.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefHelper.init(this)

        setContent {
            var username by remember { mutableStateOf(SharedPrefHelper.getUsername()) }
            val notes = viewModel.notes.collectAsStateWithLifecycle()
            val selectedNote = viewModel.selectedNote.collectAsStateWithLifecycle()

            if (username == null) {
                UsernameScreen { newUsername ->
                    SharedPrefHelper.saveUsername(newUsername)
                    username = newUsername
                }
            } else {
                when {
                    selectedNote.value != null -> {
                        NoteDetailScreen(
                            note = selectedNote.value!!,
                            currentUsername = username!!,
                            onBack = { viewModel.clearSelectedNote() },
                            onEdit = { title, content ->
                                selectedNote.value?.noteId?.let { noteId ->
                                    viewModel.updateNote(noteId, title, content)
                                }
                            },
                            onLogout = {
                                SharedPrefHelper.clearUsername()
                                username = null
                                viewModel.clearSelectedNote()
                            }
                        )
                    }
                    else -> {
                        NotesScreen(
                            notes = notes.value,
                            currentUsername = username!!,
                            onAddNote = { title, content ->
                                viewModel.addNote(title, content, username!!)
                            },
                            onDeleteNote = { noteId ->
                                viewModel.deleteNote(noteId)
                            },
                            onNoteClick = { note ->
                                viewModel.selectNote(note)
                            },
                            onLogout = {
                                SharedPrefHelper.clearUsername()
                                username = null
                            }
                        )
                    }
                }
            }
        }
    }
}