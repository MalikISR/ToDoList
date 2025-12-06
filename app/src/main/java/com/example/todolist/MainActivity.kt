package com.example.todolist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.todolist.notification.NotificationChannels
import com.example.todolist.presentation.note.NoteScreen
import com.example.todolist.presentation.productivity.ProductivityScreen
import com.example.todolist.presentation.profile.ProfileScreen
import com.example.todolist.presentation.notedetail.NoteDetailScreen
import com.example.todolist.ui.navigation.BottomNavScreen
import com.example.todolist.ui.theme.TodolistTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startNoteId = intent.getStringExtra("extra_note_id")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        NotificationChannels.createAll(this)

        setContent {
            TodolistTheme {
                val navController = rememberNavController()
                MainScaffold(
                    navController = navController,
                    startNoteId = startNoteId
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

}

@Composable
fun MainScaffold(
    navController: NavHostController,
    startNoteId: String?
) {
    val currentStartNoteId by rememberUpdatedState(startNoteId)

    LaunchedEffect(currentStartNoteId) {
        if (currentStartNoteId != null) {
            navController.navigate("note_detail/$currentStartNoteId") {
                popUpTo(BottomNavScreen.Notes.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    val items = listOf(
        BottomNavScreen.Notes,
        BottomNavScreen.Productivity,
        BottomNavScreen.Profile
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val showBottomBar = when {
                currentRoute == null -> true
                currentRoute.startsWith("note_detail") -> false
                else -> true
            }

            if (showBottomBar) {
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
                    }
                )
            }

            composable(BottomNavScreen.Productivity.route) { ProductivityScreen() }

            composable(BottomNavScreen.Profile.route) { ProfileScreen() }

            composable(
                route = "note_detail/{noteId}",
                arguments = listOf(navArgument("noteId") { type = NavType.StringType })
            ) {
                NoteDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
