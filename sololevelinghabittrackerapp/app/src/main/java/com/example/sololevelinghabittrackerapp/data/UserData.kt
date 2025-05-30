package com.example.sololevelinghabittrackerapp.data

data class UserData(
    val name: String = "Hunter",
    val level: Int = 1,
    val currentExp: Int = 0,
    val requiredExp: Int = 100,
    val gold: Int = 0,
    val strength: Int = 10,
    val intelligence: Int = 10,
    val dexterity: Int = 10,
    val habits: Map<String, Habit> = emptyMap(),
    val inventory: Map<String, InventoryItem> = emptyMap(),
    val skills: Map<String, Skill> = emptyMap()
)

data class Habit(
    val id: String = "",
    val name: String = "",
    val exp: Int = 0,
    val completed: Boolean = false,
    val streak: Int = 0,
    val difficulty: HabitDifficulty = HabitDifficulty.EASY
)

enum class HabitDifficulty(val multiplier: Int, val displayName: String) {
    EASY(1, "Easy"),
    MEDIUM(2, "Medium"),
    HARD(3, "Hard"),
    EXTREME(5, "Extreme")
}

data class InventoryItem(
    val id: String = "",
    val name: String = "",
    val type: ItemType = ItemType.WEAPON,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val equipped: Boolean = false,
    val statBonus: Map<String, Int> = emptyMap()
)

enum class ItemType {
    WEAPON, ARMOR, POTION, ACCESSORY
}

enum class ItemRarity(val color: androidx.compose.ui.graphics.Color) {
    COMMON(androidx.compose.ui.graphics.Color.Gray),
    RARE(androidx.compose.ui.graphics.Color.Blue),
    EPIC(androidx.compose.ui.graphics.Color.Magenta),
    LEGENDARY(androidx.compose.ui.graphics.Color.Yellow)
}

data class Skill(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val unlocked: Boolean = false,
    val requiredLevel: Int = 1,
    val skillType: SkillType = SkillType.PASSIVE
)

enum class SkillType {
    PASSIVE, ACTIVE, ULTIMATE
}
