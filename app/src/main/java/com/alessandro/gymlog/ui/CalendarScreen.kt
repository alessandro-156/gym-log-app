package com.alessandro.gymlog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(db: AppDatabase) {
    val scope = rememberCoroutineScope()

    var month by remember { mutableStateOf(YearMonth.now()) }
    var days by remember { mutableStateOf<List<WorkoutDay>>(emptyList()) }
    var programs by remember { mutableStateOf<List<Program>>(emptyList()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var history by remember { mutableStateOf<List<Pair<String, WeightHistory>>>(emptyList()) }

    fun reload() {
        scope.launch {
            val from = month.atDay(1).toEpochDay()
            val to = month.atEndOfMonth().toEpochDay()
            days = db.workoutDayDao().getBetween(from, to)
            programs = db.programDao().getAllOnce()
            val exs = db.exerciseDao().getAllOnce().associateBy { it.id }
            history = db.historyDao().getBetween(from, to)
                .map { (exs[it.exerciseId].name ?: ")") to it }
        }
    }

    LaunchedEffect(month) { reload() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { month = month.minusMonths(1) }) { Text("<") }
            Text("${month.monthValue}.${month.year}", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { month = month.plusMonths(1) }) { Text(">") }
        }

        val firstOffset = month.atDay(1).dayOfWeek.normalized().value - 1
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(260.dp)
        ) {
            items(firstOffset) { Box(Modifier.size(36.dp)) }
            items(month.lengthOfMonth()) { i ->
                val date = month.atDay(i + 1)
                val entry = days.firstOrNull { it.dateEpochDay == date.toEpochDay() }
                val color) = when {
                    entry?.completed == true -> MaterialTheme.colorScheme.primary
                    entry != null -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
                Box(
                    Modifier
                        .padding(2.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { selectedDay = date },
                    contentAlignment = Alignment.Center
                ) { Text("${i + 1}") }
            }
        }

        selectedDay?.let { date ->
            Text("День: $date", style = MaterialTheme.typography.titleMedium)
            Row {
                programs.forEach { p ->
                    TextButton(onClick = {
                        scope.launch {
                            db.workoutDayDao().insert(WorkoutDay(dateEpochDay = date.toEpochDay(), programId = p.id))
                            reload()
                        }
                    }) { Text("+ ${p.name}") }
                }
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("Прогресс", style = MaterialTheme.typography.titleMedium)
        if (history.isEmpty()) {
            Text(Пока нет изменений веса", style = MaterialTheme.typography.bodySmall)
        } else {
            history.forEach { (name, h) ->
                Text("• $name: ${h.weight} кг (дата: ${LocalDate.ofEpochDay(h.dateEpochDay)})")
            }
        }
    }
}
