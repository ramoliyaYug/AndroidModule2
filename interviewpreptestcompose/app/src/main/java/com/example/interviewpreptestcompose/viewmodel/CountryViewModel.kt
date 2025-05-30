package com.example.interviewpreptestcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interviewpreptestcompose.api.CountryApiService
import com.example.interviewpreptestcompose.data.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CountryUiState(
    val countries: List<Country> = emptyList(),
    val filteredCountries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class CountryViewModel : ViewModel() {
    private val apiService = CountryApiService()

    private val _uiState = MutableStateFlow(CountryUiState())
    val uiState: StateFlow<CountryUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    fun loadCountries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            apiService.getAllCountries()
                .onSuccess { countries ->
                    _uiState.value = _uiState.value.copy(
                        countries = countries,
                        filteredCountries = countries,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun searchCountries(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        val filtered = if (query.isBlank()) {
            _uiState.value.countries
        } else {
            _uiState.value.countries.filter { country ->
                country.name.common.contains(query, ignoreCase = true) ||
                        country.name.official.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(filteredCountries = filtered)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
