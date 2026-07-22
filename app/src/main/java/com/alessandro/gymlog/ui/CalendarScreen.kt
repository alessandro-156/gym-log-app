package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import java.util.*

@Composable
fun CalendarScreen(db: AppDatabase) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Календарь тренировок", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Функция интеграции с Google Calendar будет доступна в следующем обновлении.", 
             style = MaterialTheme.typography.bodyMedium)
        
        // Временная заглушка списка дней
        LazyColumn(Modifier.fillMaxSize()) {
            items((1..30).toList()) { day ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("День $day", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
