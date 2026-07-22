package com.alessandro.gymlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.ai.AiClient
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.Exercise
import kotlinx.coroutines.launch

@Composable
fun ExercisesScreen(db: AppDatabase) {
    val scope = rememberCoroutineScope()
    val exercises by db.exerciseDao().getAll().collectAsState(initial = emptyList())
    var editing by remember { mutableStateOf<Exercise?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var aiText by remember { mutableStateOf<String?>(null) }
    var aiLoading by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        if (exercises.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Нет упражнений. Нажми +")
            }
        } else {
            LazyColumn(Modifier.padding(padding).fillMaxSize()) {
                items(exercises, key = { it.id }) { ex ->
                    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(ex.name, style = MaterialTheme.typography.titleMedium)
                            val details = buildString {
                                if (ex.currentWeight > 0f) append("Вес: ${ex.currentWeight} кг (+${ex.weightIncrement})  ")
                                if (ex.sets > 0) append("${ex.sets}x${ex.reps}  ")
                                if (ex.durationMinutes > 0) append("${ex.durationMinutes} мин  ")
                                if (ex.restMaxSeconds > 0) append("Отдых ${ex.restMinSeconds}-${ex.restMaxSeconds} с")
                            }
                            if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.bodySmall)
                            Row {
                                TextButton(onClick = {
                                    aiLoading = true; aiText = ""
                                    scope.launch {
                                        aiText = AiClient.askAboutExercise(ex.name)
                                        aiLoading = false
                                    }
                                }) {
                                    Icon(Icons.Default.Info, null, Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Узнать у ИИ")
                                }
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = { editing = ex; showDialog = true }) {
                                    Icon(Icons.Default.Edit, "Изменить")
                                }
                                IconButton(onClick = { scope.launch { db.exerciseDao().delete(ex) } }) {
                                    Icon(Icons.Default.Delete, "Удалить")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ExerciseDialog(
            initial = editing,
            onDismiss = { showDialog = false },
            onSave = { ex ->
                scope.launch {
                    if (ex.id == 0L) db.exerciseDao().insert(ex) else db.exerciseDao().update(ex)
                }
                showDialog = false
            }
        )
    }

    if (aiText != null) {
        AlertDialog(
            onDismissRequest = { if (!aiLoading) aiText = null },
            confirmButton = { TextButton(onClick = { aiText = null }) { Text("Закрыть") } },
            title = { Text("ИИ-ассистент") },
            text = {
                if (aiLoading) Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Спрашиваю...")
                } else {
                    Column(Modifier.verticalScroll(rememberScrollState())) { Text(aiText ?: "") }
                }
            }
        )
    }
}

@Composable
fun ExerciseDialog(initial: Exercise?, onDismiss: () -> Unit, onSave: (Exercise) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var weight by remember { mutableStateOf(initial?.currentWeight?.takeIf { it > 0 }?.toString() ?: "") }
    var increment by remember { mutableStateOf(initial?.weightIncrement?.takeIf { it > 0 }?.toString() ?: "") }
    var sets by remember { mutableStateOf(initial?.sets?.takeIf { it > 0 }?.toString() ?: "") }
    var reps by remember { mutableStateOf(initial?.reps?.takeIf { it > 0 }?.toString() ?: "") }
    var duration by remember { mutableStateOf(initial?.durationMinutes?.takeIf { it > 0 }?.toString() ?: "") }
    var restMin by remember { mutableStateOf(initial?.restMinSeconds?.takeIf { it > 0 }?.toString() ?: "") }
    var restMax by remember { mutableStateOf(initial?.restMaxSeconds?.takeIf { it > 0 }?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новое упражнение" else "Изменить упражнение") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Название") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(weight, { weight = it }, label = { Text("Вес, кг") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(increment, { increment = it }, label = { Text("Шаг, кг") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(sets, { sets = it }, label = { Text("Подходы") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(reps, { reps = it }, label = { Text("Повторы") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(duration, { duration = it }, label = { Text("Время, мин (бег и т.п.)") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(restMin, { restMin = it }, label = { Text("Отдых от, с") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(restMax, { restMax = it }, label = { Text("Отдых до, с") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(Exercise(
                        id = initial?.id ?: 0,
                        name = name.trim(),
                        currentWeight = weight.replace(',', '.').toFloatOrNull() ?: 0f,
                        weightIncrement = increment.replace(',', '.').toFloatOrNull() ?: 0f,
                        sets = sets.toIntOrNull() ?: 0,
                        reps = reps.toIntOrNull() ?: 0,
                        durationMinutes = duration.toIntOrNull() ?: 0,
                        restMinSeconds = restMin.toIntOrNull() ?: 0,
                        restMaxSeconds = restMax.toIntOrNull() ?: 0
                    ))
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
