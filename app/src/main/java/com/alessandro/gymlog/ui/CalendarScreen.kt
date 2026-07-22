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
import java.util.*

@Composable
fun CalendarScreen(db: AppDatabase) {
    val context = LocalContext.current
    // Получаем просто список программ, так как это точно есть в DAO
    val programs by db.programDao().getAll().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Запланировать тренировку", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Выберите программу для добавления в Google Календарь:", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        
        LazyColumn(Modifier.fillMaxSize()) {
            items(programs) { program ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, "Тренировка: ${program.name}")
                            putExtra(CalendarContract.Events.DESCRIPTION, "Запланировано в GymLog")
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, Calendar.getInstance().timeInMillis)
                            putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(program.name, style = MaterialTheme.typography.titleLarge)
                        Text("Нажмите, чтобы создать событие в календаре", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
