package com.example.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow.ui.theme.TaskFlowColors
import com.example.taskflow.ui.theme.TaskFlowTheme

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TaskFlowColors.BgDeep,
                        Color(0xFF0D0F1E),
                        TaskFlowColors.BgDeep
                    )
                )
            )
    ) {
        // Top indigo glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .align(Alignment.TopCenter)
                .blur(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TaskFlowColors.AccentPrimary.copy(alpha = 0.45f),
                            TaskFlowColors.AccentPrimary.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        // Bottom cyan glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.BottomCenter)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TaskFlowColors.AccentCyan.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 500f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // App Logo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                TaskFlowColors.AccentPrimary,
                                TaskFlowColors.AccentPrimaryLight
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✓", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TASKFLOW",
                color = TaskFlowColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Welcome\nback.",
                color = TaskFlowColors.TextPrimary,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sign in to continue your journey",
                color = TaskFlowColors.TextSecondary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Email
            Text(
                text = "EMAIL",
                color = TaskFlowColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("your@email.com", color = TaskFlowColors.TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null,
                        tint = TaskFlowColors.TextMuted, modifier = Modifier.size(20.dp))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TaskFlowColors.AccentPrimary,
                    unfocusedBorderColor = TaskFlowColors.BorderSubtle,
                    focusedContainerColor   = TaskFlowColors.BgElevated,
                    unfocusedContainerColor = TaskFlowColors.BgElevated,
                    focusedTextColor   = TaskFlowColors.TextPrimary,
                    unfocusedTextColor = TaskFlowColors.TextPrimary,
                    cursorColor        = TaskFlowColors.AccentPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Password
            Text(
                text = "PASSWORD",
                color = TaskFlowColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••", color = TaskFlowColors.TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null,
                        tint = TaskFlowColors.TextMuted, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TaskFlowColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TaskFlowColors.AccentPrimary,
                    unfocusedBorderColor = TaskFlowColors.BorderSubtle,
                    focusedContainerColor   = TaskFlowColors.BgElevated,
                    unfocusedContainerColor = TaskFlowColors.BgElevated,
                    focusedTextColor   = TaskFlowColors.TextPrimary,
                    unfocusedTextColor = TaskFlowColors.TextPrimary,
                    cursorColor        = TaskFlowColors.AccentPrimary
                ),
                singleLine = true
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = {}) {
                    Text("Forgot password?", color = TaskFlowColors.AccentPrimary,
                        fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sign In Button — calls onLoginSuccess on tap
            Button(
                onClick = { onLoginSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    TaskFlowColors.AccentPrimary,
                                    TaskFlowColors.AccentPrimaryLight
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sign In", color = Color.White, fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OR divider
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = TaskFlowColors.BorderSubtle)
                Text("  or continue with  ", color = TaskFlowColors.TextMuted, fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = TaskFlowColors.BorderSubtle)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onLoginSuccess() },
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = TaskFlowColors.BgElevated)
                ) {
                    Text("G  Google", color = TaskFlowColors.TextPrimary, fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = { onLoginSuccess() },
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = TaskFlowColors.BgElevated)
                ) {
                    Text("  Apple", color = TaskFlowColors.TextPrimary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TaskFlowColors.TextSecondary)) { append("Don't have an account? ") }
                        withStyle(SpanStyle(color = TaskFlowColors.AccentPrimary, fontWeight = FontWeight.SemiBold)) { append("Sign Up") }
                    },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TaskFlowTheme { LoginScreen() }
}