package com.pairlink.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField

/**
 * Login Screen supporting flexible phone validation, keyboard imePadding, and clear error handling.
 */
@Composable
fun LoginScreen(
    onLoginSubmit: (phone: String, pin: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val cleanDigits = phone.filter { it.isDigit() }
    val isPhoneValid = cleanDigits.length in 8..15
    val displayError = localError ?: errorMessage

    AnimatedMeshBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DesignTokens.Radius.ExtraLarge),
                contentPadding = 28.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "PairLink",
                            color = DesignTokens.Colors.PrimaryPink,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter your sanctuary.",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 15.sp
                        )
                    }

                    // Error Banner
                    AnimatedVisibility(
                        visible = !displayError.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                .background(DesignTokens.Colors.DangerRoseSurface)
                                .border(1.dp, DesignTokens.Colors.DangerRose.copy(alpha = 0.5f), RoundedCornerShape(DesignTokens.Radius.Medium))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = DesignTokens.Colors.DangerRose,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = displayError ?: "",
                                color = Color(0xFFFFE4E6),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Form Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        GlassTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                localError = null
                                onClearError()
                            },
                            placeholder = "Phone Number (e.g. 9876543210)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Phone",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )

                        GlassTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                localError = null
                                onClearError()
                            },
                            placeholder = "Password (min. 6 characters)",
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { isPasswordVisible = !isPasswordVisible }
                                )
                            }
                        )

                        // Forgot Password Link
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { /* Reset password flow */ }
                            )
                        }
                    }

                    // Login Action Button with explicit feedback
                    GlassButton(
                        text = if (isLoading) "Logging in..." else "Login",
                        enabled = !isLoading,
                        onClick = {
                            when {
                                !isPhoneValid -> {
                                    localError = "Please enter a valid phone number (at least 8–10 digits)."
                                }
                                password.length < 6 -> {
                                    localError = "Password must be at least 6 characters."
                                }
                                else -> {
                                    localError = null
                                    onLoginSubmit(cleanDigits, password)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = if (isLoading) {
                            {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = DesignTokens.Colors.PrimaryPink,
                                    strokeWidth = 2.dp
                                )
                            }
                        } else null,
                        trailingIcon = if (!isLoading) {
                            {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Arrow",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )

                    // Footer Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Register",
                            color = DesignTokens.Colors.Lavender,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onNavigateToRegister)
                        )
                    }
                }
            }
        }
    }
}
