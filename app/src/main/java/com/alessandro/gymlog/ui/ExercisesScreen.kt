package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.Exercise
import com.alessandro.gymlog.ai.AiClient
import kotlinx.coroutines.launch

@Composable
fun ExercisesScreen(db: AppDatabase) {
    val exercises by db.exerciseDao().getAll().collectAsState(initial = emptyList())
    var showInfoDialog by remember { mutableStateOf<String?>(null) }
    var isLoadingAi by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Логика добавления */ }) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(exercises) { exercise ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                            Text("${exercise.currentWeight} кг", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            isLoadingAi = true
                            AiClient.getExerciseInfo(exercise.name) { info ->
                                showInfoDialog = info
                                isLoadingAi = false
                            }
                        }) {
                            if (isLoadingAi) CircularProgressIndicator(Modifier.size(24.dp))
                            else Icon(Icons.Filled.Info, "AI Info")
                        }
                    }
                }
            }
        }

        if (showInfoDialog != null) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = null },
                title = { Text("Совет от ИИ") },
                text = { Text(showInfoDialog!!) },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = null }) { Text("ОК") }
                }
            )
        }
    }
}
