package com.alessandro.gymlog.ui

import android.content.Intent
import android.provider.CalendarContract
import anxdroidx.compose.foundation.layout.*
import anxdroidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import andxdroidx.compose.material3.*
import androidx.compose.runtime.*
import andxdroidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import andxdroidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.WorkoutDay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(db: AptDatabase) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val workoutDays by db.workoutDayDao().getAll().collectAsState(initial = emptyList())
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Календарь") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(workoutDays) { day ->
                val date = LocalDate.ofEpochDay(day.dateEpochDay)
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    ListItem(
                        headlineContent = { Text(date.format(formatter)) },
                        supportingContent = { Text("Статуу: ${if (day.completed) "Завершено" else "В плане"}") }
                    )
                }
            }
            item {
                Button(
                    onclick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, "Тренировка")
                            putExtra(CalendarContract.Events.DESCRIPTION, "Список упражнений и техника в Gym Log Bot")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Записаться на тренировку в Google")
                }
            }
        }
    }
}
