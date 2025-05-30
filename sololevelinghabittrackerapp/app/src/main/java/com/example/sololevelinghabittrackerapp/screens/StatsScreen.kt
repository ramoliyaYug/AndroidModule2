package com.example.sololevelinghabittrackerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sololevelinghabittrackerapp.data.UserData
import com.example.sololevelinghabittrackerapp.repository.FirebaseRepository
import com.example.sololevelinghabittrackerapp.ui.theme.*

@Composable
fun StatsScreen() {
    val repository = remember { FirebaseRepository() }
    val userData by repository.getUserData().collectAsState(initial = UserData())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Hunter Stats",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Level Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkPurple.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Level",
                        tint = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Level ${userData.level}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    Text(
                        text = "Hunter Rank: ${getHunterRank(userData.level)}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Stats Cards
        item {
            StatCard(
                title = "Strength",
                value = userData.strength,
                maxValue = 100,
                icon = Icons.Default.FitnessCenter,
                color = RedDanger,
                description = "Physical power and endurance"
            )
        }

        item {
            StatCard(
                title = "Intelligence",
                value = userData.intelligence,
                maxValue = 100,
                icon = Icons.Default.Psychology,
                color = BlueInfo,
                description = "Mental capacity and wisdom"
            )
        }

        item {
            StatCard(
                title = "Dexterity",
                value = userData.dexterity,
                maxValue = 100,
                icon = Icons.Default.Speed,
                color = GreenSuccess,
                description = "Agility and precision"
            )
        }

        // Progress Summary
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
                    Text(
                        text = "Progress Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProgressItem(
                        label = "Total EXP Earned",
                        value = "${calculateTotalExp(userData)}",
                        icon = Icons.Default.TrendingUp
                    )

                    ProgressItem(
                        label = "Habits Completed",
                        value = "${userData.habits.values.count { it.completed }}",
                        icon = Icons.Default.CheckCircle
                    )

                    ProgressItem(
                        label = "Gold Earned",
                        value = "${userData.gold}G",
                        icon = Icons.Default.MonetizationOn
                    )

                    ProgressItem(
                        label = "Current Streak",
                        value = "${userData.habits.values.maxOfOrNull { it.streak } ?: 0}",
                        icon = Icons.Default.Whatshot
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: Int,
    maxValue: Int,
    icon: ImageVector,
    color: Color,
    description: String
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "$value",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = value.toFloat() / maxValue.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$value / $maxValue",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ProgressItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = PurpleAccent,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

fun getHunterRank(level: Int): String {
    return when {
        level < 10 -> "E-Rank"
        level < 20 -> "D-Rank"
        level < 30 -> "C-Rank"
        level < 40 -> "B-Rank"
        level < 50 -> "A-Rank"
        level < 75 -> "S-Rank"
        else -> "National Level"
    }
}

fun calculateTotalExp(userData: UserData): Int {
    // Calculate total EXP based on level progression
    var totalExp = userData.currentExp
    for (i in 1 until userData.level) {
        totalExp += (100 * Math.pow(1.2, (i - 1).toDouble())).toInt()
    }
    return totalExp
}
