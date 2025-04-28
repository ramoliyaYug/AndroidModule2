package com.example.composeretrofitexample.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApiViewModel : ViewModel() {
    private val _albumsState = MutableStateFlow<UiState>(UiState.Loading)
    val albumsState: StateFlow<UiState> = _albumsState.asStateFlow()

    private val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            try {
                val response = apiService.getAlbums()
                if (response.isSuccessful) {
                    response.body()?.let { albums ->
                        _albumsState.value = UiState.Success(albums)
                    } ?: run {
                        _albumsState.value = UiState.Error("Response body is empty")
                    }
                } else {
                    _albumsState.value = UiState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _albumsState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val albums: Api) : UiState()
        data class Error(val message: String) : UiState()
    }
}