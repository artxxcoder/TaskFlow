package com.example.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskflow.TaskViewModel
import com.example.taskflow.data.Task
import com.example.taskflow.ui.theme.TaskFlowColors
import com.example.taskflow.ui.theme.TaskFlowTheme

val filterChips = listOf("All Tasks", "Pending", "Completed")

@Composable
fun TasksScreen(viewModel: TaskViewModel? = null) {
    var selectedFilter by remember { mutableStateOf("All Tasks") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Collect tasks from database, fall back to empty list
    val allTasks by (viewModel?.tasks ?: return).collectAsState()

    val displayedTasks = when (selectedFilter) {
        "Pending"   -> allTasks.filter { !it.isCompleted }
        "Completed" -> allTasks.filter { it.isCompleted }
        else        -> allTasks
    }

    val totalCount    = allTasks.size
    val pendingCount  = allTasks.count { !it.isCompleted }
    val doneCount     = allTasks.count { it.isCompleted }

    Scaffold(
        containerColor = TaskFlowColors.BgDeep,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TaskFlowColors.AccentPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add Task", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item { TasksHeader() }
            item { StatCardsRow(totalCount, pendingCount, doneCount) }
            item {
                FilterChipsRow(selected = selectedFilter, onSelect = { selectedFilter = it })
            }
            item {
                if (displayedTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📭", fontSize = 40.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = when (selectedFilter) {
                                    "Completed" -> "No completed tasks yet"
                                    "Pending"   -> "No pending tasks!"
                                    else        -> "No tasks yet — tap + to add one"
                                },
                                color = TaskFlowColors.TextMuted,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (selectedFilter == "Completed") "Completed Tasks" else "Upcoming Deadlines",
                        color = TaskFlowColors.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }
            items(displayedTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onToggleComplete = { viewModel?.toggleComplete(task) },
                    onDelete = { viewModel?.deleteTask(task) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, subject, dueDate, priority ->
                viewModel?.addTask(title, subject, dueDate, priority)
                showAddDialog = false
            }
        )
    }
}

// ── HEADER ───────────────────────────────────────────────
@Composable
fun TasksHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 56.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Good morning 👋", color = TaskFlowColors.TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(3.dp))
            Text("My Tasks", color = TaskFlowColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(TaskFlowColors.AccentPrimary, TaskFlowColors.AccentCyan))),
            contentAlignment = Alignment.Center
        ) {
            Text("AJ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── STAT CARDS ───────────────────────────────────────────
@Composable
fun StatCardsRow(total: Int, pending: Int, done: Int) {
    val stats = listOf(
        Triple("$total",   "Total",     TaskFlowColors.AccentPrimary),
        Triple("$pending", "Pending",   TaskFlowColors.StatusWarning),
        Triple("$done",    "Done",      TaskFlowColors.StatusSuccess),
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { (value, label, color) ->
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(value, color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(label, color = TaskFlowColors.TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

// ── FILTER CHIPS ─────────────────────────────────────────
@Composable
fun FilterChipsRow(selected: String, onSelect: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filterChips) { chip ->
            val isSelected = chip == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) TaskFlowColors.AccentPrimary else TaskFlowColors.BgElevated)
                    .border(1.dp, if (isSelected) Color.Transparent else TaskFlowColors.BorderSubtle, RoundedCornerShape(8.dp))
                    .clickable { onSelect(chip) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chip,
                    color = if (isSelected) Color.White else TaskFlowColors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── TASK CARD ────────────────────────────────────────────
@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val priorityColor = when (task.priority) {
        "HIGH"   -> TaskFlowColors.StatusDanger
        "MEDIUM" -> TaskFlowColors.StatusWarning
        else     -> TaskFlowColors.StatusSuccess
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaskFlowColors.BgCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TaskFlowColors.BorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority stripe
            Box(
                modifier = Modifier.width(4.dp).height(48.dp)
                    .clip(RoundedCornerShape(2.dp)).background(priorityColor)
            )
            Spacer(Modifier.width(14.dp))

            // Checkbox
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) TaskFlowColors.AccentPrimary else TaskFlowColors.BgElevated)
                    .border(1.5.dp, if (task.isCompleted) TaskFlowColors.AccentPrimary else TaskFlowColors.BorderSubtle, CircleShape)
                    .clickable { onToggleComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(14.dp))

            // Title + subject
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) TaskFlowColors.TextMuted else TaskFlowColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${task.subject}  ·  ${task.dueDate}",
                    color = TaskFlowColors.TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            // Delete button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TaskFlowColors.StatusDanger.copy(alpha = 0.12f))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TaskFlowColors.StatusDanger,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── ADD TASK DIALOG ──────────────────────────────────────
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }

    val priorities = listOf("HIGH", "MEDIUM", "LOW")
    val priorityColors = mapOf(
        "HIGH"   to TaskFlowColors.StatusDanger,
        "MEDIUM" to TaskFlowColors.StatusWarning,
        "LOW"    to TaskFlowColors.StatusSuccess
    )
    val priorityLabels = mapOf(
        "HIGH"   to "High",
        "MEDIUM" to "Medium",
        "LOW"    to "Low"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TaskFlowColors.BgCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("New Task", color = TaskFlowColors.TextPrimary,
                fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true,
                    colors = taskTextFieldColors()
                )

                // Subject
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    singleLine = true,
                    colors = taskTextFieldColors()
                )

                // Due date
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due date  (e.g. Feb 28)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = taskTextFieldColors()
                )

                // Priority
                Text("Priority", color = TaskFlowColors.TextMuted, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorities.forEach { priority ->
                        val isSelected = priority == selectedPriority
                        val color = priorityColors[priority] ?: TaskFlowColors.AccentPrimary
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) color.copy(alpha = 0.2f) else TaskFlowColors.BgElevated)
                                .border(1.dp, if (isSelected) color else TaskFlowColors.BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedPriority = priority }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                priorityLabels[priority] ?: priority,
                                color = if (isSelected) color else TaskFlowColors.TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            title.trim(),
                            subject.trim().ifBlank { "General" },
                            dueDate.trim().ifBlank { "No due date" },
                            selectedPriority
                        )
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaskFlowColors.AccentPrimary)
            ) {
                Text("Save Task", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TaskFlowColors.TextMuted)
            }
        }
    )
}

@Composable
fun taskTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = TaskFlowColors.AccentPrimary,
    unfocusedBorderColor    = TaskFlowColors.BorderSubtle,
    focusedContainerColor   = TaskFlowColors.BgElevated,
    unfocusedContainerColor = TaskFlowColors.BgElevated,
    focusedTextColor        = TaskFlowColors.TextPrimary,
    unfocusedTextColor      = TaskFlowColors.TextPrimary,
    cursorColor             = TaskFlowColors.AccentPrimary,
    focusedLabelColor       = TaskFlowColors.AccentPrimary,
    unfocusedLabelColor     = TaskFlowColors.TextMuted
)

@Preview(showBackground = true)
@Composable
fun TasksScreenPreview() {
    TaskFlowTheme { TasksScreen() }
}