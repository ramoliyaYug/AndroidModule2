package com.example.interviewpreptestcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.interviewpreptestcompose.data.Country
import com.example.interviewpreptestcompose.ui.screens.CountryDetailScreen
import com.example.interviewpreptestcompose.ui.screens.CountryListScreen
import com.example.interviewpreptestcompose.ui.theme.InterviewpreptestcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterviewpreptestcomposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CountryExplorerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryExplorerApp() {
    val navController = rememberNavController()
    var selectedCountry by remember { mutableStateOf<Country?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Country Explorer") }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "country_list",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("country_list") {
                CountryListScreen(
                    onCountryClick = { country ->
                        selectedCountry = country
                        navController.navigate("country_detail")
                    }
                )
            }

            composable("country_detail") {
                selectedCountry?.let { country ->
                    CountryDetailScreen(
                        country = country,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
