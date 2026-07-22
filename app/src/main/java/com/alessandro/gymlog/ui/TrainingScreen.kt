package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.Exercise
import com.alessandro.gymlog.data.ExerciseDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(exerciseDao: ExerciseDao) {
    val scope = rememberCoroutineScope()
    val exercises by exerciseDao.getAllExercises().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Моя тренировка") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(exercises) { exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = exercise.name, style = MaterialTheme.typography.headlineSmall)
                            Text(text = "Вес: ${exercise.weight} кг")
                            Text(text = "Подходы: ${exercise.sets}")
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Новое упражнение") },
                text = {
                    TextField(value = name, onValueChange = { name = it }, label = { Text("Название") })
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            exerciseDao.insert(Exercise(name = name, weight = 0.0, sets = 0, reps = 0, increment = 2.5))
                            showDialog = false
                            name = ""
                        }
                    }) { Text("Добавить") }
                }
            )
        }
    }
}
