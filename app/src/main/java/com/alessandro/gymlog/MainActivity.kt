package com.alessandro.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                        if (activeProgramId == null) NavigationBar {
                            NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                                icon = { Icon(Icons.Filled.FitnessCenter, null) }, label = { Text("\u0423\u043f\u0440\u0430\u0436\u043d\u0435\u043d\u0438\u044f") })
                            NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                                icon = { Icon(Icons.Filled.List, null) }, label = { Text("\u041f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u044b") })
                            NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                                icon = { Icon(Icons.Filled.DateRange, null) }, label = { Text("\u041a\u0430\u043b\u0435\u043d\u0434\u0430\u0440\u044c") })
                            NavigationBarItem(selected = tab == 3, onClick = { tab = 3 },
                                icon = { Icon(Icons.Filled.Settings, null) }, label = { Text("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438") })
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        val pid = activeProgramId
                        if (pid != null) {
                            TrainingScreen(db, pid) { activeProgramId = null }
                        } else when (tab) {
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
