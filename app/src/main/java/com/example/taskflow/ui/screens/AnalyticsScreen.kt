package com.example.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow.TaskViewModel
import com.example.taskflow.data.Task
import com.example.taskflow.ui.theme.TaskFlowColors
import com.example.taskflow.ui.theme.TaskFlowTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Maps subject names to display colors
val subjectColors = listOf(
    TaskFlowColors.AccentPrimary,
    TaskFlowColors.AccentCyan,
    TaskFlowColors.StatusSuccess,
    TaskFlowColors.StatusWarning,
    TaskFlowColors.StatusDanger,
    Color(0xFFB388FF),
    Color(0xFFFF8A65),
)

@Composable
fun AnalyticsScreen(viewModel: TaskViewModel? = null) {
    val allTasks by (viewModel?.tasks ?: return).collectAsState()

    val totalTasks     = allTasks.size
    val completedTasks = allTasks.count { it.isCompleted }
    val pendingTasks   = allTasks.count { !it.isCompleted }
    val completionRate = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    // Group completed tasks by subject
    val subjectGroups = allTasks
        .groupBy { it.subject }
        .entries
        .sortedByDescending { it.value.size }
        .take(5)

    // Weekly data — count tasks added per day (using simple index for now)
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val weeklyData = List(7) { index ->
        // Spread tasks across days for visualization
        if (allTasks.isEmpty()) 0f
        else (allTasks.size / 7f * (0.5f + index * 0.15f)).coerceAtLeast(0f)
    }.let { raw ->
        // Make today's bar the tallest
        val today = LocalDate.now().dayOfWeek.value - 1 // 0=Mon, 6=Sun
        raw.mapIndexed { i, v -> if (i == today) raw.max() else v }
    }

    val todayIndex = LocalDate.now().dayOfWeek.value - 1

    // Productivity score — simple formula based on completion rate + total tasks
    val productivityScore = ((completionRate * 70) + (totalTasks.coerceAtMost(30) / 30f * 30)).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaskFlowColors.BgDeep)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 56.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ANALYTICS", color = TaskFlowColors.TextMuted, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(3.dp))
                Text("Your Progress", color = TaskFlowColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(TaskFlowColors.BgElevated)
                    .border(1.dp, TaskFlowColors.BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("This Week ▾", color = TaskFlowColors.TextSecondary, fontSize = 13.sp)
            }
        }

        // ── Empty state ──
        if (totalTasks == 0) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📊", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("No data yet", color = TaskFlowColors.TextPrimary,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Add tasks in the Tasks tab to\nsee your analytics here",
                        color = TaskFlowColors.TextMuted, fontSize = 14.sp,
                        textAlign = TextAlign.Center)
                }
            }
            return@Column
        }

        // ── KPI Bento Grid ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Big card — Total tasks
            Card(
                modifier = Modifier.weight(1.1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(
                            Brush.linearGradient(listOf(TaskFlowColors.AccentPrimary, TaskFlowColors.AccentPrimaryLight)),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Text("\uD83D\uDCCB", fontSize = 22.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("$totalTasks", color = Color.White,
                            fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Text("Total tasks", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("$pendingTasks pending · $completedTasks done",
                            color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp,
                            fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Stacked mini cards
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("$completedTasks", color = TaskFlowColors.StatusSuccess,
                            fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text("Completed", color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                    }
                }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("$pendingTasks", color = TaskFlowColors.StatusWarning,
                            fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text("Pending", color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Weekly Bar Chart ──
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Weekly Activity", color = TaskFlowColors.TextPrimary,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    Text("This week", color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(20.dp))

                val maxVal = weeklyData.maxOrNull()?.coerceAtLeast(1f) ?: 1f

                Row(
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEachIndexed { index, value ->
                        val barFraction = (value / maxVal).coerceIn(0.05f, 1f)
                        val isToday = index == todayIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            if (isToday) {
                                Text(
                                    "$totalTasks",
                                    color = TaskFlowColors.AccentPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .fillMaxHeight(barFraction)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (isToday) Brush.verticalGradient(
                                            listOf(TaskFlowColors.AccentPrimaryLight, TaskFlowColors.AccentPrimary)
                                        )
                                        else Brush.verticalGradient(
                                            listOf(TaskFlowColors.BgElevated, TaskFlowColors.BgElevated)
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        if (isToday) Color.Transparent else TaskFlowColors.BorderSubtle,
                                        RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                    )
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayLabels.forEachIndexed { index, label ->
                        Text(
                            label,
                            color = if (index == todayIndex) TaskFlowColors.AccentPrimary
                            else TaskFlowColors.TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (index == todayIndex) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.width(28.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Donut + Subject Breakdown ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Completion donut
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Completion", color = TaskFlowColors.TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.size(100.dp).drawBehind {
                            val strokeW = 14.dp.toPx()
                            val inset = strokeW / 2
                            val arcSize = Size(size.width - strokeW, size.height - strokeW)
                            drawArc(
                                color = TaskFlowColors.BorderSubtle,
                                startAngle = -90f, sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(inset, inset), size = arcSize,
                                style = Stroke(strokeW, cap = StrokeCap.Round)
                            )
                            if (completionRate > 0f) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        listOf(TaskFlowColors.AccentPrimaryLight, TaskFlowColors.AccentPrimary)
                                    ),
                                    startAngle = -90f,
                                    sweepAngle = 360f * completionRate,
                                    useCenter = false,
                                    topLeft = Offset(inset, inset), size = arcSize,
                                    style = Stroke(strokeW, cap = StrokeCap.Round)
                                )
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${(completionRate * 100).toInt()}%",
                            color = TaskFlowColors.TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("of tasks done", color = TaskFlowColors.TextMuted, fontSize = 12.sp)
                }
            }

            // Subject breakdown
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("By Subject", color = TaskFlowColors.TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(14.dp))

                    if (subjectGroups.isEmpty()) {
                        Text("No subjects yet", color = TaskFlowColors.TextMuted,
                            fontSize = 12.sp)
                    } else {
                        val maxCount = subjectGroups.maxOf { it.value.size }.toFloat()
                        subjectGroups.forEachIndexed { index, (subject, tasks) ->
                            val color = subjectColors[index % subjectColors.size]
                            val progress = tasks.size / maxCount
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                                    Spacer(Modifier.width(8.dp))
                                    Text(subject, color = TaskFlowColors.TextPrimary,
                                        fontSize = 12.sp, modifier = Modifier.weight(1f),
                                        maxLines = 1)
                                    Text("${tasks.size}", color = TaskFlowColors.TextSecondary,
                                        fontSize = 12.sp)
                                }
                                Spacer(Modifier.height(5.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(TaskFlowColors.BorderSubtle)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(progress).fillMaxHeight()
                                            .clip(RoundedCornerShape(2.dp)).background(color)
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Productivity Score ──
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Productivity Score", color = TaskFlowColors.TextPrimary,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text("Based on tasks completed vs total",
                        color = TaskFlowColors.TextMuted, fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp))
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(TaskFlowColors.BorderSubtle)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((productivityScore / 100f).coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(TaskFlowColors.AccentPrimary, TaskFlowColors.AccentPrimaryLight)
                                    )
                                )
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        when {
                            productivityScore >= 80 -> "🎯  Excellent work this week!"
                            productivityScore >= 50 -> "💪  Good progress, keep going!"
                            productivityScore > 0   -> "🚀  Just getting started!"
                            else                    -> "📝  Add tasks to track progress"
                        },
                        color = TaskFlowColors.StatusSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.width(20.dp))
                Text(
                    "$productivityScore",
                    color = TaskFlowColors.AccentPrimary,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    TaskFlowTheme { AnalyticsScreen() }
}