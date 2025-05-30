package com.example.sololevelinghabittrackerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sololevelinghabittrackerapp.data.*
import com.example.sololevelinghabittrackerapp.repository.FirebaseRepository
import com.example.sololevelinghabittrackerapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userData: UserData = UserData(),
    lastKnownLevel: Int = 0,
    levelUpShownForLevel: Set<Int> = setOf(),
    onLevelUpShown: (Int) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val repository = remember { FirebaseRepository() }

    // Use passed userData if available, otherwise collect from repository
    val localUserData by repository.getUserData().collectAsState(initial = userData)
    val currentUserData = if (userData.level > 0) userData else localUserData

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showLevelUpDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Check for level up - only show if level increased and hasn't been shown for this level
    LaunchedEffect(currentUserData.level, lastKnownLevel, levelUpShownForLevel) {
        if (currentUserData.level > lastKnownLevel &&
            lastKnownLevel > 0 &&
            !levelUpShownForLevel.contains(currentUserData.level)) {
            showLevelUpDialog = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with greeting and level info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkPurple.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back,",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${currentUserData.name}!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = onSignOut
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Sign Out",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Level and Gold Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Level",
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Level ${currentUserData.level}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Gold",
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${currentUserData.gold}G",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // EXP Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EXP",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${currentUserData.currentExp} / ${currentUserData.requiredExp}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = if (currentUserData.requiredExp > 0) currentUserData.currentExp.toFloat() / currentUserData.requiredExp.toFloat() else 0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PurpleAccent,
                            trackColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        // Today's Habits Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Quests",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                FloatingActionButton(
                    onClick = { showAddHabitDialog = true },
                    containerColor = PurpleAccent,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Habit")
                }
            }
        }

        // Habits List
        items(currentUserData.habits.values.toList()) { habit ->
            HabitCard(
                habit = habit,
                onComplete = {
                    scope.launch {
                        repository.completeHabit(habit.id)
                    }
                },
                onDelete = {
                    scope.launch {
                        repository.deleteHabit(habit.id)
                    }
                }
            )
        }

        // Empty state
        if (currentUserData.habits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkPurple.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = "No habits",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No quests available",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Add your first habit to start leveling up!",
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }

    // Add Habit Dialog
    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onAdd = { habit ->
                scope.launch {
                    repository.addHabit(habit)
                    showAddHabitDialog = false
                }
            }
        )
    }

    // Level Up Dialog
    if (showLevelUpDialog) {
        LevelUpDialog(
            level = currentUserData.level,
            onDismiss = {
                showLevelUpDialog = false
                onLevelUpShown(currentUserData.level)
            }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.completed)
                GreenSuccess.copy(alpha = 0.2f)
            else
                DarkPurple.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onComplete,
                enabled = !habit.completed
            ) {
                Icon(
                    if (habit.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Complete",
                    tint = if (habit.completed) GreenSuccess else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${habit.exp * habit.difficulty.multiplier} EXP",
                        fontSize = 12.sp,
                        color = PurpleAccent
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "• ${habit.difficulty.displayName}",
                        fontSize = 12.sp,
                        color = when (habit.difficulty) {
                            HabitDifficulty.EASY -> GreenSuccess
                            HabitDifficulty.MEDIUM -> GoldAccent
                            HabitDifficulty.HARD -> Color(0xFFFF6B35)
                            HabitDifficulty.EXTREME -> RedDanger
                        }
                    )

                    if (habit.streak > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔥${habit.streak}",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6B35)
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = RedDanger
                )
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAdd: (Habit) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(HabitDifficulty.EASY) }
    var expReward by remember { mutableStateOf(10) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Quest",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Quest Name", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = PurpleAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Difficulty",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                HabitDifficulty.values().forEach { difficulty ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedDifficulty == difficulty,
                            onClick = {
                                selectedDifficulty = difficulty
                                expReward = when (difficulty) {
                                    HabitDifficulty.EASY -> 10
                                    HabitDifficulty.MEDIUM -> 20
                                    HabitDifficulty.HARD -> 35
                                    HabitDifficulty.EXTREME -> 50
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PurpleAccent
                            )
                        )
                        Text(
                            text = "${difficulty.displayName} (${expReward * difficulty.multiplier} EXP)",
                            color = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (habitName.isNotBlank()) {
                        val habit = Habit(
                            id = System.currentTimeMillis().toString(),
                            name = habitName,
                            exp = expReward,
                            difficulty = selectedDifficulty
                        )
                        onAdd(habit)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleAccent
                )
            ) {
                Text("Create Quest")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkPurple
    )
}

@Composable
fun LevelUpDialog(
    level: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉",
                    fontSize = 48.sp
                )
                Text(
                    text = "LEVEL UP!",
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Congratulations!",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "You've reached Level $level",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All stats increased by +2!",
                    color = GreenSuccess,
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = DarkSlate
                )
            ) {
                Text("Continue", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkPurple
    )
}
