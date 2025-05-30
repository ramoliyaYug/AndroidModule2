package com.example.sololevelinghabittrackerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.sololevelinghabittrackerapp.ui.theme.DarkPurple
import com.example.sololevelinghabittrackerapp.ui.theme.DarkSlate
import com.example.sololevelinghabittrackerapp.ui.theme.PurpleAccent
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.sololevelinghabittrackerapp.auth.AuthScreen
import com.example.sololevelinghabittrackerapp.screens.*
import com.example.sololevelinghabittrackerapp.repository.FirebaseRepository
import com.example.sololevelinghabittrackerapp.data.UserData

data class BottomNavItem(val title: String, val icon: ImageVector)

@Preview (showBackground = true)
@Composable
fun LevelUpApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    // Global level tracking - this persists across navigation
    var lastKnownLevel by remember { mutableStateOf(0) }
    var levelUpShownForLevel by remember { mutableStateOf(setOf<Int>()) }

    val repository = remember { FirebaseRepository() }

    // Listen to authentication state changes
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            currentUser = auth.currentUser
            // Reset level tracking when user changes
            if (auth.currentUser == null) {
                lastKnownLevel = 0
                levelUpShownForLevel = setOf()
            }
        }
    }

    if (currentUser == null) {
        // Show authentication screen if user is not logged in
        AuthScreen()
    } else {
        // Get user data to track level globally
        val userData by repository.getUserData().collectAsState(initial = UserData())

        // Initialize last known level on first load
        LaunchedEffect(userData.level) {
            if (lastKnownLevel == 0 && userData.level > 0) {
                lastKnownLevel = userData.level
                // Mark current level as "shown" to prevent showing dialog for existing level
                levelUpShownForLevel = levelUpShownForLevel + userData.level
            }
        }

        // Show main app if user is logged in
        val tabs = listOf(
            BottomNavItem("Home", Icons.Default.Home),
            BottomNavItem("Stats", Icons.Default.BarChart),
            BottomNavItem("Inventory", Icons.Default.Inventory),
            BottomNavItem("Skills", Icons.Default.AutoAwesome)
        )

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSlate.copy(alpha = 0.95f),
                    contentColor = PurpleAccent
                ) {
                    tabs.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title,
                                    tint = if (selectedTab == index) PurpleAccent else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    item.title,
                                    color = if (selectedTab == index) PurpleAccent else Color.Gray
                                )
                            },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PurpleAccent,
                                selectedTextColor = PurpleAccent,
                                indicatorColor = PurpleAccent.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DarkSlate,
                                DarkPurple,
                                DarkSlate
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        userData = userData,
                        lastKnownLevel = lastKnownLevel,
                        levelUpShownForLevel = levelUpShownForLevel,
                        onLevelUpShown = { level ->
                            levelUpShownForLevel = levelUpShownForLevel + level
                            lastKnownLevel = level
                        },
                        onSignOut = {
                            repository.signOut()
                            lastKnownLevel = 0
                            levelUpShownForLevel = setOf()
                        }
                    )
                    1 -> StatsScreen()
                    2 -> InventoryScreen()
                    3 -> SkillsScreen()
                }
            }
        }
    }
}
