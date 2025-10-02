package com.example.todolist.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Notes : BottomNavScreen("notes", "Заметки", Icons.Default.List)
    object Productivity : BottomNavScreen("productivity", "Продуктивность", Icons.Default.Notifications)
    object Profile : BottomNavScreen("profile", "Профиль", Icons.Default.Person)
}