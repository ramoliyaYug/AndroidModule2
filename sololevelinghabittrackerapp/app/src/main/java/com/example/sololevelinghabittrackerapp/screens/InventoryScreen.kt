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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sololevelinghabittrackerapp.data.*
import com.example.sololevelinghabittrackerapp.repository.FirebaseRepository
import com.example.sololevelinghabittrackerapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen() {
    val repository = remember { FirebaseRepository() }
    val userData by repository.getUserData().collectAsState(initial = UserData())
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventory",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = "Gold",
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${userData.gold}G",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }
        }

        // Inventory Items
        items(userData.inventory.values.toList()) { item ->
            InventoryItemCard(
                item = item,
                onEquip = {
                    scope.launch {
                        repository.equipItem(item.id)
                    }
                },
                onUnequip = {
                    scope.launch {
                        repository.unequipItem(item.id)
                    }
                },
                onUse = {
                    scope.launch {
                        repository.usePotion(item.id)
                    }
                }
            )
        }

        // Empty state
        if (userData.inventory.isEmpty()) {
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
                            Icons.Default.Inventory,
                            contentDescription = "Empty inventory",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your inventory is empty",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Complete quests to earn items!",
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
    onUse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkPurple.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item Icon
                Icon(
                    getItemIcon(item.type),
                    contentDescription = item.name,
                    tint = item.rarity.color,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.rarity.color
                        )

                        if (item.equipped) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Equipped",
                                tint = GreenSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "${item.rarity.name} ${item.type.name}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Action Button
                when (item.type) {
                    ItemType.POTION -> {
                        Button(
                            onClick = onUse,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenSuccess
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Use",
                                fontSize = 12.sp
                            )
                        }
                    }
                    else -> {
                        Button(
                            onClick = if (item.equipped) onUnequip else onEquip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.equipped)
                                    Color.Gray.copy(alpha = 0.3f)
                                else
                                    PurpleAccent
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (item.equipped) "Unequip" else "Equip",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Stat Bonuses
            if (item.statBonus.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Stat Bonuses:",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                item.statBonus.forEach { (stat, bonus) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            getStatIcon(stat),
                            contentDescription = stat,
                            tint = getStatColor(stat),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${stat.replaceFirstChar { it.uppercase() }}: +$bonus",
                            fontSize = 12.sp,
                            color = getStatColor(stat)
                        )
                    }
                }
            }
        }
    }
}

fun getItemIcon(type: ItemType): ImageVector {
    return when (type) {
        ItemType.WEAPON -> Icons.Default.Whatshot
        ItemType.ARMOR -> Icons.Default.Shield
        ItemType.POTION -> Icons.Default.LocalDrink
        ItemType.ACCESSORY -> Icons.Default.Diamond
    }
}

fun getStatIcon(stat: String): ImageVector {
    return when (stat.lowercase()) {
        "strength" -> Icons.Default.FitnessCenter
        "intelligence" -> Icons.Default.Psychology
        "dexterity" -> Icons.Default.Speed
        else -> Icons.Default.TrendingUp
    }
}

fun getStatColor(stat: String): Color {
    return when (stat.lowercase()) {
        "strength" -> RedDanger
        "intelligence" -> BlueInfo
        "dexterity" -> GreenSuccess
        else -> Color.Gray
    }
}
