package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.ui.screens.*
import com.example.taskflow.ui.theme.TaskFlowColors
import com.example.taskflow.ui.theme.TaskFlowTheme

data class NavItem(val label: String, val icon: ImageVector, val index: Int)

val navItems = listOf(
    NavItem("Tasks",     Icons.Default.CheckCircle,  0),
    NavItem("Timer",     Icons.Default.Timer,        1),
    NavItem("Analytics", Icons.Default.BarChart,     2),
    NavItem("Profile",   Icons.Default.Person,       3),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskFlowTheme {
                TaskFlowApp()
            }
        }
    }
}

@Composable
fun TaskFlowApp() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        MainShell()
    }
}

@Composable
fun MainShell() {
    var selectedTab by remember { mutableStateOf(0) }
    val taskViewModel: TaskViewModel = viewModel()

    Scaffold(
        containerColor = TaskFlowColors.BgDeep,
        bottomBar = {
            TaskFlowBottomNav(
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> TasksScreen(viewModel = taskViewModel)
                1 -> TimerScreen(viewModel = taskViewModel)
                2 -> AnalyticsScreen(viewModel = taskViewModel)
                3 -> CustomizeScreen()
            }
        }
    }
}

@Composable
fun TaskFlowBottomNav(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar(
        containerColor = TaskFlowColors.BgSurface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        navItems.forEach { item ->
            val isSelected = item.index == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(item.index) },
                icon = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) TaskFlowColors.AccentPrimaryAlpha
                                else Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = { Text(item.label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = TaskFlowColors.AccentPrimary,
                    unselectedIconColor = TaskFlowColors.TextMuted,
                    selectedTextColor   = TaskFlowColors.AccentPrimary,
                    unselectedTextColor = TaskFlowColors.TextMuted,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}