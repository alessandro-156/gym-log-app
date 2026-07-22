package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.WorkoutDayDao
import com.alessandro.gymlog.data.WorkoutDay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(workoutDayDao: WorkoutDayDao) {
    val scope = rememberCoroutineScope()
    // Используем правильный метод из вашего DAO
    val workoutDays by workoutDayDao.getAllDays().collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Календарь тренировок") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(workoutDays) { day ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    ListItem(
                        headlineContent = { Text("День: ${day.dateEpochDay}") },
                        supportingContent = { Text("Программа ID: ${day.programId}") }
                    )
                }
            }
            item {
                Button(onClick = {
                    scope.launch {
                        // Используем конструктор вашей сущности WorkoutDay
                        workoutDayDao.insert(WorkoutDay(dateEpochDay = LocalDate.now().toEpochDay(), programId = 0))
                    }
                }) { Text("Добавить сегодня") }
            }
        }
    }
}
