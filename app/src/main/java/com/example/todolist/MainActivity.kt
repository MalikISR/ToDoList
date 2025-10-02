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
import com.example.todolist.presentation.auth.FirebaseAuthScreen
import com.example.todolist.presentation.note.NoteScreen
import com.example.todolist.presentation.productivity.ProductivityScreen
import com.example.todolist.presentation.profile.ProfileScreen
import com.example.todolist.presentation.notedetail.NoteDetailScreen
import com.example.todolist.ui.navigation.BottomNavScreen
import com.example.todolist.ui.theme.TodolistTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodolistTheme {
                val navController = rememberNavController()
                val currentUser = FirebaseAuth.getInstance().currentUser

                if (currentUser == null) {
                    // если пользователь не авторизован → сразу авторизация
                    FirebaseAuthScreen(
                        onAuthSuccess = {
                            navController.navigate(BottomNavScreen.Notes.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                } else {
                    // иначе показываем нижнюю панель
                    MainScaffold(navController)
                }
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
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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
            composable(BottomNavScreen.Productivity.route) { ProductivityScreen() }
            composable(BottomNavScreen.Profile.route) { ProfileScreen() }

            // Экран деталей заметки
            composable("note_detail/{noteId}") {
                NoteDetailScreen(onBack = { navController.popBackStack() })
            }
            composable("note_detail_new") {
                NoteDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
