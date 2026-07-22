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
    val workoutDays by workoutDayDao.getAllWorkoutDays().collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Календарь тренировок") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(workoutDays) { day ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Тренировка: ${day.date}") },
                        supportingContent = { Text("Статус: Завершена") }
                    )
                }
            }
            
            item {
                Button(
                    onClick = {
                        scope.launch {
                            workoutDayDao.insert(WorkoutDay(date = LocalDate.now().toString()))
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Добавить сегодняшнюю тренировку")
                }
            }
        }
    }
}
