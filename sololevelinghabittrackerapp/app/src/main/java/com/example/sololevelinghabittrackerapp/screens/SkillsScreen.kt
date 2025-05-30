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
fun SkillsScreen() {
    val repository = remember { FirebaseRepository() }
    val userData by repository.getUserData().collectAsState(initial = UserData())
    val scope = rememberCoroutineScope()

    // Create a mutable state for skills to trigger recomposition
    var skillsState by remember { mutableStateOf(emptyList<Skill>()) }

    // Update skills when userData changes
    LaunchedEffect(userData) {
        val baseSkills = userData.skills.values.toList()
        val additionalSkills = mutableListOf<Skill>()

        if (userData.level >= 5 && !userData.skills.containsKey("shadow_step")) {
            additionalSkills.add(
                Skill(
                    id = "shadow_step",
                    name = "Shadow Step",
                    description = "Increases movement speed by 20% for 10 seconds",
                    unlocked = false,
                    requiredLevel = 5,
                    skillType = SkillType.ACTIVE
                )
            )
        }

        if (userData.level >= 10 && !userData.skills.containsKey("mana_burn")) {
            additionalSkills.add(
                Skill(
                    id = "mana_burn",
                    name = "Mana Burn",
                    description = "Deals damage based on intelligence stat",
                    unlocked = false,
                    requiredLevel = 10,
                    skillType = SkillType.ACTIVE
                )
            )
        }

        if (userData.level >= 15 && !userData.skills.containsKey("berserker_rage")) {
            additionalSkills.add(
                Skill(
                    id = "berserker_rage",
                    name = "Berserker's Rage",
                    description = "Ultimate: Double damage for 30 seconds, but take 50% more damage",
                    unlocked = false,
                    requiredLevel = 15,
                    skillType = SkillType.ULTIMATE
                )
            )
        }

        if (userData.level >= 25 && !userData.skills.containsKey("shadow_clone")) {
            additionalSkills.add(
                Skill(
                    id = "shadow_clone",
                    name = "Shadow Clone",
                    description = "Ultimate: Create a shadow clone that mimics your actions",
                    unlocked = false,
                    requiredLevel = 25,
                    skillType = SkillType.ULTIMATE
                )
            )
        }

        if (userData.level >= 50 && !userData.skills.containsKey("monarch_authority")) {
            additionalSkills.add(
                Skill(
                    id = "monarch_authority",
                    name = "Monarch's Authority",
                    description = "Legendary Ultimate: Overwhelming presence that dominates all enemies",
                    unlocked = false,
                    requiredLevel = 50,
                    skillType = SkillType.ULTIMATE
                )
            )
        }

        skillsState = baseSkills + additionalSkills
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Skills",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Level ${userData.level} Hunter",
                    fontSize = 16.sp,
                    color = GoldAccent
                )
            }
        }

        // Skills List
        items(skillsState) { skill ->
            SkillCard(
                skill = skill,
                userLevel = userData.level,
                onUnlock = {
                    scope.launch {
                        val success = repository.unlockSkill(skill.id)
                        if (success) {
                            // Force refresh by updating the skills state
                            skillsState = skillsState.map {
                                if (it.id == skill.id) it.copy(unlocked = true) else it
                            }
                        }
                    }
                }
            )
        }

        // Empty state
        if (skillsState.isEmpty()) {
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
                            Icons.Default.AutoAwesome,
                            contentDescription = "No skills",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No skills available",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Level up to unlock new skills!",
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
fun SkillCard(
    skill: Skill,
    userLevel: Int,
    onUnlock: () -> Unit
) {
    val canUnlock = userLevel >= skill.requiredLevel && !skill.unlocked

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                skill.unlocked -> DarkPurple.copy(alpha = 0.8f)
                canUnlock -> PurpleAccent.copy(alpha = 0.2f)
                else -> Color.Gray.copy(alpha = 0.2f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skill Icon
                Icon(
                    getSkillIcon(skill.skillType),
                    contentDescription = skill.name,
                    tint = getSkillColor(skill.skillType, skill.unlocked),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = skill.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (skill.unlocked) Color.White else Color.Gray
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Skill Type Badge
                        Surface(
                            color = getSkillColor(skill.skillType, skill.unlocked).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = skill.skillType.name,
                                fontSize = 10.sp,
                                color = getSkillColor(skill.skillType, skill.unlocked),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Required Level: ${skill.requiredLevel}",
                        fontSize = 12.sp,
                        color = if (userLevel >= skill.requiredLevel) GreenSuccess else RedDanger
                    )
                }

                // Unlock/Status Button
                when {
                    skill.unlocked -> {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Unlocked",
                            tint = GreenSuccess,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    canUnlock -> {
                        Button(
                            onClick = onUnlock,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PurpleAccent
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Unlock",
                                fontSize = 12.sp
                            )
                        }
                    }
                    else -> {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = skill.description,
                fontSize = 14.sp,
                color = if (skill.unlocked) Color.White else Color.Gray,
                lineHeight = 18.sp
            )

            // Special effects for ultimate skills
            if (skill.skillType == SkillType.ULTIMATE && skill.unlocked) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Whatshot,
                        contentDescription = "Ultimate",
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Ultimate Skill - High Impact",
                        fontSize = 12.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun getSkillIcon(skillType: SkillType): ImageVector {
    return when (skillType) {
        SkillType.PASSIVE -> Icons.Default.Shield
        SkillType.ACTIVE -> Icons.Default.Bolt
        SkillType.ULTIMATE -> Icons.Default.Whatshot
    }
}

fun getSkillColor(skillType: SkillType, unlocked: Boolean): Color {
    if (!unlocked) return Color.Gray

    return when (skillType) {
        SkillType.PASSIVE -> GreenSuccess
        SkillType.ACTIVE -> BlueInfo
        SkillType.ULTIMATE -> GoldAccent
    }
}
