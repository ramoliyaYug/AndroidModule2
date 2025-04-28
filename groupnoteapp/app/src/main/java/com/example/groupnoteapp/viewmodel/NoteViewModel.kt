package com.example.groupnoteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupnoteapp.model.NotesDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val _notes = MutableStateFlow<List<NotesDataClass>>(emptyList())
    val notes: StateFlow<List<NotesDataClass>> = _notes

    private val _selectedNote = MutableStateFlow<NotesDataClass?>(null)
    val selectedNote: StateFlow<NotesDataClass?> = _selectedNote

    init {
        observeNotes()
    }

    private fun observeNotes() {
        database.child("notes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notesList = mutableListOf<NotesDataClass>()
                for (noteSnapshot in snapshot.children) {
                    noteSnapshot.getValue(NotesDataClass::class.java)?.let {
                        notesList.add(it)
                    }
                }
                viewModelScope.launch {
                    _notes.emit(notesList.sortedByDescending { it.timestamp })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun addNote(title: String, content: String, username: String) {
        val noteId = database.child("notes").push().key ?: return
        val note = NotesDataClass(
            userName = username,
            noteId = noteId,
            title = title,
            content = content
        )
        database.child("notes").child(noteId).setValue(note)
    }

    fun deleteNote(noteId: String) {
        database.child("notes").child(noteId).removeValue()
    }

    fun selectNote(note: NotesDataClass) {
        viewModelScope.launch {
            _selectedNote.emit(note)
        }
    }

    fun clearSelectedNote() {
        viewModelScope.launch {
            _selectedNote.emit(null)
        }
    }

    fun updateNote(noteId: String, title: String, content: String) {
        val note = _notes.value.find { it.noteId == noteId } ?: return
        val updatedNote = note.copy(
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        database.child("notes").child(noteId).setValue(updatedNote)
    }
}