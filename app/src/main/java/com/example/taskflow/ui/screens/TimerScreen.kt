package com.example.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow.TaskViewModel
import com.example.taskflow.ui.theme.TaskFlowColors
import com.example.taskflow.ui.theme.TaskFlowTheme
import kotlinx.coroutines.delay

// Shared timer settings — CustomizeScreen writes here, TimerScreen reads here
object TimerSettings {
    var focusMinutes by mutableStateOf(25)
    var shortBreakMinutes by mutableStateOf(5)
    var longBreakMinutes by mutableStateOf(15)
}

@Composable
fun TimerScreen(viewModel: TaskViewModel) {
    val modes = listOf("Pomodoro", "Short Break", "Long Break")

    var selectedMode by remember { mutableStateOf("Pomodoro") }
    var isPlaying by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(0) }

    // Total seconds based on current mode and settings
    val totalSeconds by remember(selectedMode) {
        derivedStateOf {
            when (selectedMode) {
                "Short Break" -> TimerSettings.shortBreakMinutes * 60
                "Long Break"  -> TimerSettings.longBreakMinutes * 60
                else          -> TimerSettings.focusMinutes * 60
            }
        }
    }

    var secondsLeft by remember(selectedMode) { mutableStateOf(totalSeconds) }

    // Reset secondsLeft whenever mode or settings change
    LaunchedEffect(selectedMode, TimerSettings.focusMinutes, TimerSettings.shortBreakMinutes, TimerSettings.longBreakMinutes) {
        secondsLeft = when (selectedMode) {
            "Short Break" -> TimerSettings.shortBreakMinutes * 60
            "Long Break"  -> TimerSettings.longBreakMinutes * 60
            else          -> TimerSettings.focusMinutes * 60
        }
        isPlaying = false
    }

    // Countdown tick
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (secondsLeft > 0 && isPlaying) {
                delay(1000L)
                secondsLeft--
            }
            if (secondsLeft == 0) {
                isPlaying = false
                if (selectedMode == "Pomodoro") {
                    completedSessions = (completedSessions + 1).coerceAtMost(4)
                }
            }
        }
    }

    // Format time as MM:SS
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeDisplay = "%02d:%02d".format(minutes, seconds)

    // Ring progress — starts full, drains to 0
    val totalSecondsFloat = totalSeconds.toFloat().coerceAtLeast(1f)
    val ringProgress = (secondsLeft / totalSecondsFloat).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize().background(TaskFlowColors.BgDeep)) {

        // Background glow
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.TopCenter)
                .offset(y = 60.dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(TaskFlowColors.AccentPrimary.copy(alpha = 0.22f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            Text(
                "FOCUS TIMER",
                color = TaskFlowColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(20.dp))

            // ── Mode chips ──
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                modes.forEach { mode ->
                    val isSelected = mode == selectedMode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TaskFlowColors.AccentPrimary else TaskFlowColors.BgElevated)
                            .border(1.dp, if (isSelected) Color.Transparent else TaskFlowColors.BorderSubtle, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedMode = mode
                                isPlaying = false
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            mode,
                            color = if (isSelected) Color.White else TaskFlowColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── Circular Timer Ring ──
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .drawBehind {
                        val strokeWidth = 14.dp.toPx()
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

                        // Background track
                        drawArc(
                            color = TaskFlowColors.BorderSubtle,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                        // Progress arc
                        if (ringProgress > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        TaskFlowColors.AccentPrimaryLight,
                                        TaskFlowColors.AccentPrimary,
                                        TaskFlowColors.AccentPrimaryLight
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = 360f * ringProgress,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        timeDisplay,
                        color = TaskFlowColors.TextPrimary,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when {
                            secondsLeft == 0 -> "Done! 🎉"
                            isPlaying        -> when (selectedMode) {
                                "Short Break" -> "Short break"
                                "Long Break"  -> "Long break"
                                else          -> "Focus time"
                            }
                            else             -> "Ready to start"
                        },
                        color = TaskFlowColors.TextMuted,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Session dots ──
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < completedSessions) TaskFlowColors.AccentPrimary
                                else TaskFlowColors.BgElevated
                            )
                            .border(
                                1.dp,
                                if (index < completedSessions) Color.Transparent else TaskFlowColors.BorderSubtle,
                                CircleShape
                            )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                if (completedSessions == 0) "No sessions yet"
                else "Session $completedSessions of 4 complete",
                color = TaskFlowColors.TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(40.dp))

            // ── Controls ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Reset button
                IconButton(
                    onClick = {
                        isPlaying = false
                        secondsLeft = when (selectedMode) {
                            "Short Break" -> TimerSettings.shortBreakMinutes * 60
                            "Long Break"  -> TimerSettings.longBreakMinutes * 60
                            else          -> TimerSettings.focusMinutes * 60
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TaskFlowColors.BgElevated)
                        .border(1.dp, TaskFlowColors.BorderSubtle, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TaskFlowColors.TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Play / Pause button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(TaskFlowColors.AccentPrimary, TaskFlowColors.AccentPrimaryLight)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        if (secondsLeft > 0) isPlaying = !isPlaying
                    }) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                // Skip button
                IconButton(
                    onClick = {
                        isPlaying = false
                        secondsLeft = 0
                        if (selectedMode == "Pomodoro") {
                            completedSessions = (completedSessions + 1).coerceAtMost(4)
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TaskFlowColors.BgElevated)
                        .border(1.dp, TaskFlowColors.BorderSubtle, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Skip",
                        tint = TaskFlowColors.TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Current Task Card ──
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(
                    "CURRENT TASK",
                    color = TaskFlowColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, TaskFlowColors.AccentPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(10.dp).clip(CircleShape)
                                .background(TaskFlowColors.AccentPrimary)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Select a task to focus on",
                                color = TaskFlowColors.TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Tap swap to choose from your task list",
                                color = TaskFlowColors.TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TaskFlowColors.BgElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = TaskFlowColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    TaskFlowTheme { }
}