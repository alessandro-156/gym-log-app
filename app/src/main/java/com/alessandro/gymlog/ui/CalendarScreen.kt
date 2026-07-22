package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // Важный импорт для делегатов
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.WorkoutDay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(db: AppDatabase) {
    val scope = rememberCoroutineScope()
    // Проверьте в WorkoutDayDao, как называется метод получения всех дней. 
    // Если getAllDays() выдает ошибку, попробуйте getAll() или посмотрите файл WorkoutDayDao.kt
    val workoutDays by db.workoutDayDao().getAllDays().collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Календарь") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(workoutDays) { day ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    ListItem(
                        headlineContent = { Text("День: ${day.dateEpochDay}") }
                    )
                }
            }
            item {
                Button(onClick = {
                    scope.launch {
                        db.workoutDayDao().insert(WorkoutDay(dateEpochDay = LocalDate.now().toEpochDay(), programId = 0))
                    }
                }) { Text("Отметить сегодня") }
            }
        }
    }
}
