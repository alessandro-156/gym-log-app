package com.alessandro.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.ui.*
import com.alessandro.gymlog.ui.theme.GymLogTheme
import com.alessandro.gymlog.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.get(applicationContext)
        val appContext = applicationContext
        setContent {
            val scope = rememberCoroutineScope()
            val themeMode by Settings.themeMode(appContext).collectAsState(initial = ThemeMode.SYSTEM)
            var tab by remember { mutableStateOf(0) }
            var activeProgramId by remember { mutableStateOf<Long?>(null) }
            
            GymLogTheme(themeMode) {
                Scaffold(
                    bottomBar = {
                        if (activeProgramId == null) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = tab == 0,
                                    onClick = { tab = 0 },
                                    icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "Упражнения") },
                                    label = { Text("Упражнения") }
                                )
                                NavigationBarItem(
                                    selected = tab == 1,
                                    onClick = { tab = 1 },
                                    icon = { Icon(Icons.Filled.List, contentDescription = "Программы") },
                                    label = { Text("Программы") }
                                )
                                NavigationBarItem(
                                    selected = tab == 2,
                                    onClick = { tab = 2 },
                                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "Календарь") },
                                    label = { Text("Календарь") }
                                )
                                NavigationBarItem(
                                    selected = tab == 3,
                                    onClick = { tab = 3 },
                                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Настройки") },
                                    label = { Text("Настройки") }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        val pid = activeProgramId
                        if (pid != null) {
                            // Передаем db обратно, как было в оригинале
                            TrainingScreen(db, pid) { activeProgramId = null }
                        } else {
                            when (tab) {
                                0 -> ExercisesScreen(db)
                                1 -> ProgramsScreen(db) { id -> activeProgramId = id }
                                2 -> CalendarScreen(db)
                                3 -> SettingsScreen(themeMode) { mode ->
                                    scope.launch { Settings.setThemeMode(appContext, mode) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
