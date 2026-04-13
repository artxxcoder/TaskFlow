package com.example.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow.ui.theme.AppSettings
import com.example.taskflow.ui.theme.TaskFlowColors
import com.example.taskflow.ui.theme.TaskFlowTheme

// Available accent colors
val accentOptions = listOf(
    Color(0xFF6C63FF),  // Indigo (default)
    Color(0xFF00D4FF),  // Cyan
    Color(0xFF00E5A0),  // Green
    Color(0xFFFF5F7E),  // Rose
    Color(0xFFFFB347),  // Amber
)

@Composable
fun CustomizeScreen() {
    var deadlineReminders by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(false) }

    // Read directly from AppSettings so changes are instant
    val isDarkMode = AppSettings.isDarkMode
    val selectedAccent = AppSettings.accentColor

    // Timer sliders — write to TimerSettings
    var focusSlider by remember { mutableStateOf(TimerSettings.focusMinutes.toFloat()) }
    var shortBreakSlider by remember { mutableStateOf(TimerSettings.shortBreakMinutes.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaskFlowColors.Background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── Header ──
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 24.dp)
        ) {
            Text("CUSTOMIZE", color = TaskFlowColors.TextMuted, fontSize = 11.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(3.dp))
            Text("Your Space", color = TaskFlowColors.TextPrimary, fontSize = 28.sp,
                fontWeight = FontWeight.Bold)
        }

        // ── Profile Card ──
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TaskFlowColors.Card),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp)) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(TaskFlowColors.AccentPrimary, TaskFlowColors.AccentCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AJ", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier.size(22.dp).clip(CircleShape)
                            .background(TaskFlowColors.AccentPrimary)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Alexis Asence", color = TaskFlowColors.TextPrimary,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("clerickasence84@gmail.com", color = TaskFlowColors.TextMuted,
                        fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TaskFlowColors.AccentPrimaryAlpha)
                            .border(1.dp, TaskFlowColors.AccentPrimary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("I created the app!", color = TaskFlowColors.AccentPrimary,
                            fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null,
                    tint = TaskFlowColors.TextMuted)
            }
        }

        // ── Appearance ──
        SectionLabel("Appearance")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TaskFlowColors.Card),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
        ) {
            Column {
                // ── Dark / Light toggle ──
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingIcon(
                        if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        TaskFlowColors.AccentPrimary
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Theme Mode", color = TaskFlowColors.TextPrimary,
                            fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(if (isDarkMode) "Dark" else "Light",
                            color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Dark", "Light").forEachIndexed { index, label ->
                            val isActive = (index == 0) == isDarkMode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isActive) TaskFlowColors.AccentPrimary
                                        else TaskFlowColors.Elevated
                                    )
                                    .border(
                                        1.dp,
                                        if (isActive) Color.Transparent else TaskFlowColors.BorderSubtle,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        AppSettings.isDarkMode = (index == 0)  // ← live toggle
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label,
                                    color = if (isActive) Color.White else TaskFlowColors.TextSecondary,
                                    fontSize = 12.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(color = TaskFlowColors.BorderSubtle,
                    modifier = Modifier.padding(horizontal = 18.dp))

                // ── Accent color swatches ──
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingIcon(Icons.Default.Palette, TaskFlowColors.AccentCyan)
                        Spacer(Modifier.width(14.dp))
                        Text("Accent Color", color = TaskFlowColors.TextPrimary,
                            fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        accentOptions.forEach { color ->
                            val isSelected = color == selectedAccent
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isSelected) 3.dp else 0.dp,
                                        Color.White,
                                        CircleShape
                                    )
                                    .clickable {
                                        AppSettings.accentColor = color  // ← live accent change
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Text("✓", color = Color.White, fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Changes apply instantly across the app",
                        color = TaskFlowColors.TextMuted, fontSize = 11.sp)
                }
            }
        }

        // ── Timer Settings ──
        SectionLabel("Timer Settings")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TaskFlowColors.Card),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
        ) {
            Column {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingIcon(Icons.Default.Timer, TaskFlowColors.AccentPrimary)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Focus Duration", color = TaskFlowColors.TextPrimary,
                                fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("${focusSlider.toInt()} minutes",
                                color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                        }
                        Text("${focusSlider.toInt()}m", color = TaskFlowColors.AccentPrimary,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Slider(
                        value = focusSlider,
                        onValueChange = {
                            focusSlider = it
                            TimerSettings.focusMinutes = it.toInt()
                        },
                        valueRange = 5f..60f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = TaskFlowColors.AccentPrimary,
                            activeTrackColor = TaskFlowColors.AccentPrimary,
                            inactiveTrackColor = TaskFlowColors.BorderSubtle
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("5m", color = TaskFlowColors.TextMuted, fontSize = 11.sp)
                        Text("60m", color = TaskFlowColors.TextMuted, fontSize = 11.sp)
                    }
                }

                HorizontalDivider(color = TaskFlowColors.BorderSubtle,
                    modifier = Modifier.padding(horizontal = 18.dp))

                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingIcon(Icons.Default.FreeBreakfast, TaskFlowColors.StatusSuccess)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Short Break", color = TaskFlowColors.TextPrimary,
                                fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("${shortBreakSlider.toInt()} minutes",
                                color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                        }
                        Text("${shortBreakSlider.toInt()}m", color = TaskFlowColors.StatusSuccess,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Slider(
                        value = shortBreakSlider,
                        onValueChange = {
                            shortBreakSlider = it
                            TimerSettings.shortBreakMinutes = it.toInt()
                        },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = TaskFlowColors.StatusSuccess,
                            activeTrackColor = TaskFlowColors.StatusSuccess,
                            inactiveTrackColor = TaskFlowColors.BorderSubtle
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1m", color = TaskFlowColors.TextMuted, fontSize = 11.sp)
                        Text("15m", color = TaskFlowColors.TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        // ── Notifications ──
        SectionLabel("Notifications")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TaskFlowColors.Card),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
        ) {
            Column {
                NotifRow("Deadline Reminders", Icons.Default.NotificationsActive,
                    TaskFlowColors.StatusWarning, deadlineReminders) { deadlineReminders = !deadlineReminders }
                HorizontalDivider(color = TaskFlowColors.BorderSubtle,
                    modifier = Modifier.padding(horizontal = 18.dp))
                NotifRow("Sound Effects", Icons.Default.VolumeUp,
                    TaskFlowColors.AccentCyan, soundEffects) { soundEffects = !soundEffects }
                HorizontalDivider(color = TaskFlowColors.BorderSubtle,
                    modifier = Modifier.padding(horizontal = 18.dp))
                NotifRow("Vibration", Icons.Default.Vibration,
                    TaskFlowColors.StatusDanger, vibration) { vibration = !vibration }
            }
        }

        // ── Log Out ──
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 24.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.StatusDanger),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null,
                tint = TaskFlowColors.StatusDanger, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Log Out", color = TaskFlowColors.StatusDanger,
                fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TaskFlowColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
    )
}

@Composable
fun SettingIcon(icon: ImageVector, tint: Color) {
    Box(
        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
            .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun NotifRow(label: String, icon: ImageVector, iconColor: Color, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon, iconColor)
        Spacer(Modifier.width(14.dp))
        Text(label, color = TaskFlowColors.TextPrimary, fontSize = 15.sp,
            fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = TaskFlowColors.AccentPrimary,
                uncheckedThumbColor = TaskFlowColors.TextMuted,
                uncheckedTrackColor = TaskFlowColors.Elevated
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomizeScreenPreview() {
    TaskFlowTheme { CustomizeScreen() }
}