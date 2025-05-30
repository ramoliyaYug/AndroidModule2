package com.example.sololevelinghabittrackerapp.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sololevelinghabittrackerapp.data.*
import com.example.sololevelinghabittrackerapp.repository.FirebaseRepository
import com.example.sololevelinghabittrackerapp.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Preview (showBackground = true)
@Composable
fun AuthScreen() {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hunterName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val repository = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo/Title
            Icon(
                Icons.Default.Whatshot,
                contentDescription = "Level Up",
                tint = GoldAccent,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "LEVEL UP",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Hunter's Guild",
                fontSize = 16.sp,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Auth Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkPurple.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (isLogin) "Welcome Back, Hunter" else "Join the Guild",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hunter Name (only for signup)
                    if (!isLogin) {
                        OutlinedTextField(
                            value = hunterName,
                            onValueChange = { hunterName = it },
                            label = { Text("Hunter Name", color = Color.Gray) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Hunter Name",
                                    tint = PurpleAccent
                                )
                            },
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
                    }

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = PurpleAccent
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = PurpleAccent
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = PurpleAccent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = RedDanger,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Auth Button
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = ""

                                try {
                                    if (isLogin) {
                                        // Login
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                isLoading = false
                                                if (!task.isSuccessful) {
                                                    errorMessage = task.exception?.message ?: "Login failed"
                                                }
                                            }
                                    } else {
                                        // Sign up
                                        if (hunterName.isBlank()) {
                                            errorMessage = "Hunter name is required"
                                            isLoading = false
                                            return@launch
                                        }

                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    // Create initial user data
                                                    scope.launch {
                                                        val userId = task.result?.user?.uid ?: return@launch
                                                        val initialUserData = UserData(
                                                            name = hunterName,
                                                            level = 1,
                                                            currentExp = 0,
                                                            requiredExp = 100,
                                                            gold = 50,
                                                            strength = 10,
                                                            intelligence = 10,
                                                            dexterity = 10,
                                                            habits = emptyMap(),
                                                            inventory = getInitialInventory(),
                                                            skills = getInitialSkills()
                                                        )

                                                        repository.createHunter(userId, initialUserData)
                                                        isLoading = false
                                                    }
                                                } else {
                                                    isLoading = false
                                                    errorMessage = task.exception?.message ?: "Sign up failed"
                                                }
                                            }
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage = e.message ?: "An error occurred"
                                }
                            }
                        },
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && (isLogin || hunterName.isNotBlank()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleAccent,
                            disabledContainerColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (isLogin) "Enter Guild" else "Join Guild",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Switch between login/signup
                    TextButton(
                        onClick = {
                            isLogin = !isLogin
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isLogin) "New Hunter? Join the Guild" else "Already a Hunter? Enter Guild",
                            color = PurpleAccent
                        )
                    }
                }
            }
        }
    }
}

private fun getInitialInventory() = mapOf(
    "sword1" to InventoryItem(
        id = "sword1",
        name = "Training Sword",
        type = ItemType.WEAPON,
        rarity = ItemRarity.COMMON,
        equipped = true,
        statBonus = mapOf("strength" to 2)
    ),
    "potion1" to InventoryItem(
        id = "potion1",
        name = "Health Potion",
        type = ItemType.POTION,
        rarity = ItemRarity.COMMON,
        equipped = false,
        statBonus = emptyMap()
    )
)

private fun getInitialSkills() = mapOf(
    "skill1" to Skill(
        id = "skill1",
        name = "Basic Training",
        description = "Increases EXP gain by 5%",
        unlocked = true,
        requiredLevel = 1,
        skillType = SkillType.PASSIVE
    )
)
