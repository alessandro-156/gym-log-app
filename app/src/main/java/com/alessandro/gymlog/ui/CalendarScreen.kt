package com.alessandro.gymlog.ui

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.ProgramWithExercises
import java.util.*

@Composable
fun CalendarScreen(db: AppDatabase) {
    val context = LocalContext.current
    val programs by db.programDao().getProgramsWithExercises().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Запланировать тренировку", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Выберите программу, чтобы добавить её в Google Календарь:", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        
        LazyColumn(Modifier.fillMaxSize()) {
            items(programs) { programWithExercises ->
                val program = programWithExercises.program
                val exercises = programWithExercises.exercises
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        // Формируем текст описания из списка упражнений
                        val description = exercises.joinToString("\n") { 
                            "${it.name}: ${it.sets} по ${it.reps} (${it.currentWeight} кг)" 
                        }
                        
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, "Тренировка: ${program.name}")
                            putExtra(CalendarContract.Events.DESCRIPTION, description)
                            // По умолчанию ставим время на текущий момент
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, Calendar.getInstance().timeInMillis)
                            putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(program.name, style = MaterialTheme.typography.titleLarge)
                        Text("${exercises.size} упражнений", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
