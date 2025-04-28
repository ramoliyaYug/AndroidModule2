package com.example.composeretrofitexample.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ComponentLayout(
    modifier: Modifier = Modifier,
    viewModel: ApiViewModel = viewModel()
) {
    val uiState by viewModel.albumsState.collectAsState()

    when (val state = uiState) {
        is ApiViewModel.UiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ApiViewModel.UiState.Success -> {
            AlbumsList(
                modifier = modifier,
                albums = state.albums
            )
        }
        is ApiViewModel.UiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message)
            }
        }
    }
}

@Composable
fun AlbumsList(
    modifier: Modifier = Modifier,
    albums: Api
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(albums) { album ->
            AlbumItem(album = album)
        }
    }
}

@Composable
fun AlbumItem(album: ApiDataClass) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = album.title,
            modifier = Modifier.padding(16.dp)
        )
    }
}