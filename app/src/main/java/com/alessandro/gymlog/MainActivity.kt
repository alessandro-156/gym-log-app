package com.alessandro.gymlog

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.Exercise
import com.alessandro.gymlog.ui.*
import com.alessandro.gymlog.ui.theme.GymLogTheme
import com.alessandro.gymlog.ui.theme.ThemeMode
import com.alessandro.gymlog.ai.AiClient

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
            var isMinimized by remember { mutableStateOf(false) }

            GymLogTheme(themeMode) {
                Scaffold(
                    bottomBar = {
                        if (activeProgramId == null || isMinimized) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = tab == 0,
                                    onClick = { tab = 0 },
                                    icon = { Icon(Icons.Filled.FitnessCenter, "Упр") },
                                    label = { Text("Упражнения", maxLines = 1) }
                                )
                                NavigationBarItem(
                                    selected = tab == 1,
                                    onClick = { tab = 1 },
                                    icon = { Icon(Icons.Filled.List, "Прог") },
                                    label = { Text("Программы", maxLines = 1) }
                                )
                                NavigationBarItem(
                                    selected = tab == 2,
                                    onClick = { tab = 2 },
                                    icon = { Icon(Icons.Filled.DateRange, "Кал") },
                                    label = { Text("Календарь", maxLines = 1) }
                                )
                                NavigationBarItem(
                                    selected = tab == 3,
                                    onClick = { tab = 3 },
                                    icon = { Icon(Icons.Filled.Settings, "Настр") },
                                    label = { Text("Настройки", maxLines = 1) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        if (activeProgramId != null && !isMinimized) {
                            TrainingScreen(
                                db, activeProgramId!!,
                                { activeProgramId = null; isMinimized = false },
                                { isMinimized = true }
                            )
                        } else {
                            Box {
                                when (tab) {
                                    0 -> ExercisesScreen(db) { exercise -> 
                                        askAboutExercise(exercise)
                                    }
                                    1 -> ProgramsScreen(db) { id -> activeProgramId = id; isMinimized = false }
                                    2 -> CalendarScreen(db)
                                    3 -> SettingsScreen(themeMode, { mode ->
                                        scope.launch { Settings.setThemeMode(appContext, mode) }
                                    }, appContext)
                                }
                                if (isMinimized && activeProgramId != null) {
                                    Surface(
                                        Modifier.fillMaxWidth().clickable { isMinimized = false },
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shadowElevation = 8.dp
                                    ) {
                                        Text("📂 Тренировка активна. Нажмите, чтобы развернуть", 
                                             Modifier.padding(12.dp), style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun askAboutExercise(exercise: Exercise) {
        AiClient.getExerciseInfo(exercise.name) { response ->
            runOnUiThread {
               // В будущем здесь будет красивое окно, а пока выводим результат в Toast для проверки
               Toast.makeText(this, response, Toast.LENGTH_LONG).show()
            }
        }
    }
}
