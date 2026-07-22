package com.alessandro.gymlog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.Exercise
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    db: AppDatabase,
    programId: Long,
    onBack: () -> Unit,
    onMinimize: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val exercises by db.programDao().getExercisesForProgram(programId).collectAsState(initial = emptyList())

    var currentExerciseIndex by remember { mutableStateOf(0) }
    var currentSetsDone by remember { mutableStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showWeightIncrementDialog by remember { mutableStateOf<Exercise?>(null) }
    var showWeightEditDialog by remember { mutableStateOf(false) }
    var editedWeight by remember { mutableStateOf("") }

    if (exercises.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentExercise = exercises[currentExerciseIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Тренировка", fontSize = 18.sp) },
                actions = {
                    TextButton(onClick = onMinimize) {
                        Text("СВЕРНУТЬ")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Индикаторы подходов
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(currentExercise.sets) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .background(
                                if (index < currentSetsDone) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = currentExercise.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${currentExercise.currentWeight} кг",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                editedWeight = currentExercise.currentWeight.toString()
                showWeightEditDialog = true
            }) {
                Text("ИЗМЕНИТЬ ВЕС")
            }

            Spacer(Modifier.weight(1f))

            if (currentSetsDone < currentExercise.sets) {
                Button(
                    onClick = { currentSetsDone++ },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("ПОДХОД ${currentSetsDone + 1}", fontSize = 18.sp)
                }
            } else {
                Button(
                    onClick = { showWeightIncrementDialog = currentExercise },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("СЛЕДУЮЩЕЕ УПРАЖНЕНИЕ", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { showExitDialog = true }) {
                Text("ЗАВЕРШИТЬ ТРЕНИРОВКУ", color = MaterialTheme.colorScheme.error)
            }
        }

        // Диалоги
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Завершить тренировку?") },
                text = { Text("Прогресс текущего упражнения не будет сохранен.") },
                confirmButton = {
                    TextButton(onClick = onBack) { Text("ДА") }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) { Text("НЕТ") }
                }
            )
        }

        showWeightIncrementDialog?.let { exercise ->
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Прогресс") },
                text = { Text("Увеличить вес на ${exercise.weightIncrement} кг для следующей тренировки?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            db.exerciseDao().update(exercise.copy(currentWeight = exercise.currentWeight + exercise.weightIncrement))
                            if (currentExerciseIndex < exercises.size - 1) {
                                currentExerciseIndex++
                                currentSetsDone = 0
                            } else {
                                onBack()
                            }
                            showWeightIncrementDialog = null
                        }
                    }) { Text("ДА") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        if (currentExerciseIndex < exercises.size - 1) {
                            currentExerciseIndex++
                            currentSetsDone = 0
                        } else {
                            onBack()
                        }
                        showWeightIncrementDialog = null
                    }) { Text("НЕТ") }
                }
            )
        }

        if (showWeightEditDialog) {
            AlertDialog(
                onDismissRequest = { showWeightEditDialog = false },
                title = { Text("Новый вес") },
                text = { TextField(value = editedWeight, onValueChange = { editedWeight = it }) },
                confirmButton = {
                    Button(onClick = {
                        val newWeight = editedWeight.toFloatOrNull() ?: currentExercise.currentWeight
                        scope.launch {
                            db.exerciseDao().update(currentExercise.copy(currentWeight = newWeight))
                            showWeightEditDialog = false
                        }
                    }) { Text("СОХРАНИТЬ") }
                }
            )
        }
    }
}
