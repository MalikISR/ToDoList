package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.todolist.presentation.note.NoteScreen
import com.example.todolist.presentation.productivity.ProductivityScreen
import com.example.todolist.presentation.profile.ProfileScreen
import com.example.todolist.presentation.notedetail.NoteDetailScreen
import com.example.todolist.ui.navigation.BottomNavScreen
import com.example.todolist.ui.theme.TodolistTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodolistTheme {
                val navController = rememberNavController()
                MainScaffold(navController = navController)
            }
        }
    }
}

@Composable
fun MainScaffold(navController: NavHostController) {
    val items = listOf(
        BottomNavScreen.Notes,
        BottomNavScreen.Productivity,
        BottomNavScreen.Profile
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Notes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Экран заметок
            composable(BottomNavScreen.Notes.route) {
                NoteScreen(
                    onNoteClick = { noteId ->
                        navController.navigate("note_detail/$noteId")
                    },
                    onAddNoteClick = {
                        navController.navigate("note_detail_new")
                    }
                )
            }

            // Экран продуктивности
            composable(BottomNavScreen.Productivity.route) { ProductivityScreen() }

            // Профиль
            composable(BottomNavScreen.Profile.route) { ProfileScreen() }

            // Детали существующей заметки с аргументом Int
            composable(
                route = "note_detail/{noteId}",
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) {
                NoteDetailScreen(onBack = { navController.popBackStack() })
            }

            // Создание новой заметки (без аргумента)
            composable("note_detail_new") {
                NoteDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
