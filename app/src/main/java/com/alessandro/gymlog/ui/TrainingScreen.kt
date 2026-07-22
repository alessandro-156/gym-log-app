package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    db: AppDatabase,
    programId: Long,
    onFinish: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var increasedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var restSecondsLeft by remember { mutableStateOf(0) }
    var restRunning by remember { mutableStateOf(false) }

    LaunchedEffect(programId) {
        exercises = db.programDao().getExercisesForProgram(programId)
    }

    LaunchedEffect(restRunning) {
        while (restRunning && restSecondsLeft > 0) {
            delay(1000)
            restSecondsLeft--
        }
        if (restSecondsLeft <= 0) restRunning = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Тренировка", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        if (restRunning) {
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Отдых: ${restSecondsLeft / 60}:${(restSecondsLeft % 60).toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.displaySmall)
                    TextButton(onClick = { restRunning = false; restSecondsLeft = 0 }) { Text("Пропустить") }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        LazyColumn(Modifier.weight(1f)) {
            items(exercises, key = { it.id }) { ex ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(ex.name, style = MaterialTheme.typography.titleMedium)
                        val info = buildString {
                            if (ex.currentWeight > 0f) append("Вес: ${ex.currentWeight} кг · ")
                            append("${ex.sets} подх Х ${ex.reps}")
                            if (ex.durationMinutes > 0) append(" · ${ex.durationMinutes} мин")
                        }
                        Text(info, style = MaterialTheme.typography.bodyMedium)
                        Row {
                            if (ex.restMaxSeconds > 0) {
                                TextButton(onClick = {
                                    restSecondsLeft = ex.restMaxSeconds
                                    restRunning = true
                                }) { Text("Отдых") }
                            }
                            if (ex.currentWeight > 0f && ex.id !in increasedIds) {
                                TextButton(onClick = {
                                    scope.launch {
                                        val newWeight = ex.currentWeight + ex.weightIncrement
                                        db.exerciseDao().update(ex.copy(currentWeight = newWeight))
                                        db.historyDao().insert(
                                            WeightHistory(
                                                exerciseId = ex.id,
                                                weight = newWeight,
                                                dateEpochDay = LocalDate.now().toEpochDay()
                                            )
                                        )
                                        increasedIds = increasedIds + ex.id
                                        exercises = db.programDao().getExercisesForProgram(programId)
                                    }
                                }) { Text("Увеличить вес ↑") }
                            } else if (ex.id in increasedIds) {
                                TextButton(onClick = {}, enabled = false) { Text("Вес увеличен ✓") }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                scope.launch {
                    db.calendarDao().markCompleted(programId, LocalDate.now().toEpochDay())
                    onFinish()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Завершить тренировку") }
    }
}
