package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // Важный импорт
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.Exercise
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(db: AppDatabase, programId: Long, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    // Если getAllExercises() не находит, проверьте ExerciseDao.kt
    val exercises by db.exerciseDao().getAllExercises().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Тренировка") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(exercises) { exercise ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = exercise.name, style = MaterialTheme.typography.headlineSmall)
                        Text(text = "Вес: ${exercise.currentWeight} кг")
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Новое упражнение") },
                text = { TextField(value = exerciseName, onValueChange = { exerciseName = it }) },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            db.exerciseDao().insert(Exercise(
                                name = exerciseName,
                                currentWeight = 0.0f,
                                weightIncrement = 2.5f,
                                sets = 0,
                                reps = 0,
                                durationMinutes = 0,
                                restMinSeconds = 60,
                                restMaxSeconds = 120
                            ))
                            showDialog = false
                            exerciseName = ""
                        }
                    }) { Text("Добавить") }
                }
            )
        }
    }
}
