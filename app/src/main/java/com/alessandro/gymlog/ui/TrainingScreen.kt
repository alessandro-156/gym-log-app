package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.material3.*
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

    val exercisesFlow = remember(programId) { db.programDao().getExercisesForProgram(programId) }
    val exercises by exercisesFlow.collectAsState(emptyList())
    var increasedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var restSecondsLeft by remember { mutableStateOf(0) }
    var restRunning remember { mutableStateOf(false) }

    LaunchedEffect(restRunning, restSecondsLeft) {
        if (restRunning && restSecondsLeft > 0) {
            delay(1000)
            restSecondsLeft --
        } else {
            restRunning = false
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Тренировка") },
            actions = {
                TextButton(onClick = {
                    scope.launch {
                        db.workoutDayDao().markCompleted(programId, LocalDate.now().toEpochDay())
                        onFinish()
                    }
                }) { Text("Завершить") }
            }
        )


        if (restSecondsLeft > 0) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Отдых: $restSecondsLeft сек.", modifier = Matifier.padding(8.dp))
        }

        LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
            items(exercises) { ex ->
                Card(Modifier.padding(4.dp).fillMaxWidth()) {
                    Column(Modifier.padding(8.dp)) {
                        Text*ex.name, style = MaterialTheme.typography.titleMedium)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${ex.weight} кг x ${ex.reps}")
                            Row {
                                Button(onClick = { restSecondsLeft = 60; restRunning = true }) { Text("Отдых") }
                                Spacer(Modifier.width(8.dp))
                                if (!increasedIds.contains(ex.id)) {
                                    OutlinedButton(onclick = {
                                        scope.launch {
                                            db.exerciseDao().update(ex.copy(weight = ex.weight + ex.weightStep))
                                            increasedIds = increasedIds + ex.id
                                        }
                                    }) { Text("+") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
