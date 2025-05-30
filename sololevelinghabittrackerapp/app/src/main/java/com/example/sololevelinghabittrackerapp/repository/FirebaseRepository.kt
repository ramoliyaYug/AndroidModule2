package com.example.sololevelinghabittrackerapp.repository

import com.example.sololevelinghabittrackerapp.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentHunterRef(): DatabaseReference? {
        val userId = auth.currentUser?.uid ?: return null
        return database.getReference("hunters").child(userId)
    }

    fun getUserData(): Flow<UserData> = callbackFlow {
        val hunterRef = getCurrentHunterRef()
        if (hunterRef == null) {
            trySend(UserData())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserData::class.java) ?: UserData()
                trySend(userData)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        hunterRef.addValueEventListener(listener)
        awaitClose { hunterRef.removeEventListener(listener) }
    }

    suspend fun createHunter(userId: String, userData: UserData) {
        database.getReference("hunters").child(userId).setValue(userData).await()
    }

    suspend fun updateUserData(userData: UserData) {
        getCurrentHunterRef()?.setValue(userData)?.await()
    }

    suspend fun addHabit(habit: Habit) {
        getCurrentHunterRef()?.child("habits")?.child(habit.id)?.setValue(habit)?.await()
    }

    suspend fun completeHabit(habitId: String): Boolean {
        val hunterRef = getCurrentHunterRef() ?: return false

        try {
            val habitRef = hunterRef.child("habits").child(habitId)
            val snapshot = habitRef.get().await()
            val habit = snapshot.getValue(Habit::class.java) ?: return false

            if (habit.completed) return false

            // Update habit
            val updatedHabit = habit.copy(
                completed = true,
                streak = habit.streak + 1
            )
            habitRef.setValue(updatedHabit).await()

            // Update user stats
            val userSnapshot = hunterRef.get().await()
            val userData = userSnapshot.getValue(UserData::class.java) ?: return false

            val expGain = habit.exp * habit.difficulty.multiplier
            val goldGain = expGain / 2
            var newExp = userData.currentExp + expGain
            var newLevel = userData.level
            var newRequiredExp = userData.requiredExp
            var newStrength = userData.strength
            var newIntelligence = userData.intelligence
            var newDexterity = userData.dexterity

            // Level up logic
            var leveledUp = false
            while (newExp >= newRequiredExp) {
                newExp -= newRequiredExp
                newLevel++
                newRequiredExp = (newRequiredExp * 1.2).toInt()
                leveledUp = true

                // Stat increases on level up
                newStrength += 2
                newIntelligence += 2
                newDexterity += 2
            }

            val updatedUserData = userData.copy(
                currentExp = newExp,
                level = newLevel,
                requiredExp = newRequiredExp,
                gold = userData.gold + goldGain,
                strength = newStrength,
                intelligence = newIntelligence,
                dexterity = newDexterity
            )

            hunterRef.setValue(updatedUserData).await()

            // Award items on level up
            if (leveledUp) {
                awardLevelUpRewards(newLevel)
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

    private suspend fun awardLevelUpRewards(level: Int) {
        val hunterRef = getCurrentHunterRef() ?: return

        when (level) {
            5 -> {
                val ironSword = InventoryItem(
                    id = "iron_sword_$level",
                    name = "Iron Sword",
                    type = ItemType.WEAPON,
                    rarity = ItemRarity.COMMON,
                    equipped = false,
                    statBonus = mapOf("strength" to 5)
                )
                hunterRef.child("inventory").child(ironSword.id).setValue(ironSword).await()
            }
            10 -> {
                val leatherArmor = InventoryItem(
                    id = "leather_armor_$level",
                    name = "Leather Armor",
                    type = ItemType.ARMOR,
                    rarity = ItemRarity.RARE,
                    equipped = false,
                    statBonus = mapOf("dexterity" to 8, "strength" to 3)
                )
                hunterRef.child("inventory").child(leatherArmor.id).setValue(leatherArmor).await()
            }
            15 -> {
                val shadowBlade = InventoryItem(
                    id = "shadow_blade_$level",
                    name = "Shadow Blade",
                    type = ItemType.WEAPON,
                    rarity = ItemRarity.EPIC,
                    equipped = false,
                    statBonus = mapOf("strength" to 15, "dexterity" to 10)
                )
                hunterRef.child("inventory").child(shadowBlade.id).setValue(shadowBlade).await()
            }
        }
    }

    suspend fun equipItem(itemId: String) {
        val hunterRef = getCurrentHunterRef() ?: return

        try {
            val userSnapshot = hunterRef.get().await()
            val userData = userSnapshot.getValue(UserData::class.java) ?: return
            val item = userData.inventory[itemId] ?: return

            if (item.type == ItemType.POTION) return

            // Unequip other items of the same type
            val updatedInventory = userData.inventory.mapValues { (_, inventoryItem) ->
                if (inventoryItem.type == item.type && inventoryItem.equipped) {
                    inventoryItem.copy(equipped = false)
                } else if (inventoryItem.id == itemId) {
                    inventoryItem.copy(equipped = true)
                } else {
                    inventoryItem
                }
            }

            hunterRef.child("inventory").setValue(updatedInventory).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun unequipItem(itemId: String) {
        val hunterRef = getCurrentHunterRef() ?: return

        try {
            hunterRef.child("inventory").child(itemId).child("equipped").setValue(false).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun usePotion(itemId: String) {
        val hunterRef = getCurrentHunterRef() ?: return

        try {
            // Remove potion from inventory
            hunterRef.child("inventory").child(itemId).removeValue().await()

            // Add health/mana effect (for demo, we'll just add some gold)
            val userSnapshot = hunterRef.get().await()
            val userData = userSnapshot.getValue(UserData::class.java) ?: return

            hunterRef.child("gold").setValue(userData.gold + 10).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun unlockSkill(skillId: String): Boolean {
        val hunterRef = getCurrentHunterRef() ?: return false

        try {
            val userSnapshot = hunterRef.get().await()
            val userData = userSnapshot.getValue(UserData::class.java) ?: return false

            // Check if skill exists in user's skills map
            val skill = userData.skills[skillId]

            if (skill != null) {
                // Skill exists, check if it can be unlocked
                if (skill.unlocked || userData.level < skill.requiredLevel) return false

                // Unlock existing skill
                hunterRef.child("skills").child(skillId).child("unlocked").setValue(true).await()
            } else {
                // Skill doesn't exist, create it based on skillId
                val newSkill = when (skillId) {
                    "shadow_step" -> Skill(
                        id = "shadow_step",
                        name = "Shadow Step",
                        description = "Increases movement speed by 20% for 10 seconds",
                        unlocked = true,
                        requiredLevel = 5,
                        skillType = SkillType.ACTIVE
                    )
                    "mana_burn" -> Skill(
                        id = "mana_burn",
                        name = "Mana Burn",
                        description = "Deals damage based on intelligence stat",
                        unlocked = true,
                        requiredLevel = 10,
                        skillType = SkillType.ACTIVE
                    )
                    "berserker_rage" -> Skill(
                        id = "berserker_rage",
                        name = "Berserker's Rage",
                        description = "Ultimate: Double damage for 30 seconds, but take 50% more damage",
                        unlocked = true,
                        requiredLevel = 15,
                        skillType = SkillType.ULTIMATE
                    )
                    "shadow_clone" -> Skill(
                        id = "shadow_clone",
                        name = "Shadow Clone",
                        description = "Ultimate: Create a shadow clone that mimics your actions",
                        unlocked = true,
                        requiredLevel = 25,
                        skillType = SkillType.ULTIMATE
                    )
                    "monarch_authority" -> Skill(
                        id = "monarch_authority",
                        name = "Monarch's Authority",
                        description = "Legendary Ultimate: Overwhelming presence that dominates all enemies",
                        unlocked = true,
                        requiredLevel = 50,
                        skillType = SkillType.ULTIMATE
                    )
                    else -> return false
                }

                // Check level requirement
                if (userData.level < newSkill.requiredLevel) return false

                // Add new skill to database
                hunterRef.child("skills").child(skillId).setValue(newSkill).await()
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun resetDailyHabits() {
        val hunterRef = getCurrentHunterRef() ?: return

        try {
            val userSnapshot = hunterRef.get().await()
            val userData = userSnapshot.getValue(UserData::class.java) ?: return

            val resetHabits = userData.habits.mapValues { (_, habit) ->
                habit.copy(completed = false)
            }

            hunterRef.child("habits").setValue(resetHabits).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun deleteHabit(habitId: String) {
        getCurrentHunterRef()?.child("habits")?.child(habitId)?.removeValue()?.await()
    }

    fun signOut() {
        auth.signOut()
    }
}
