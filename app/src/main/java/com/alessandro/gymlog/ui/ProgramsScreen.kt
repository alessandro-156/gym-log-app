package com.alessandro.gymlog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.gymlog.data.AppDatabase
import com.alessandro.gymlog.data.Program
import com.alessandro.gymlog.data.ProgramExercise
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramsScreen(db: AppDatabase, onStartWorkout: (Long) -> Unit) {
    val scope = rememberCoroutineScope()
    val programs by db.programDao().getAll().collectAsState(initial = emptyList())
    var opened by remember { mutableStateOf<Program?>(null) }
    var showNameDialog by remember { mutableStateOf(false) }

    val current = opened
    if (current == null) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showNameDialog = true }) {
                    Icon(Icons.Default.Add, "Добавить")
                }
            }
        ) { padding ->
            if (programs.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Нет программ. Нажми +")
                }
            } else {
                LazyColumn(Modifier.padding(padding).fillMaxSize()) {
                    items(programs, key = { it.id }) { p ->
                        Card(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                                .clickable { opened = p }
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(p.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                TextButton(onClick = { onStartWorkout(p.id) }) { Text("Начать") }
                                IconButton(onClick = { scope.launch { db.programDao().delete(p) } }) {
                                    Icon(Icons.Default.Delete, "Удалить")
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showNameDialog) {
            var name by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = { Text("Новая программа") },
                text = { OutlinedTextField(name, { name = it }, label = { Text("Название") }, singleLine = true) },
                confirmButton = {
                    TextButton(enabled = name.isNotBlank(), onClick = {
                        scope.launch { db.programDao().insert(Program(name = name.trim())) }
                        showNameDialog = false
                    }) { Text("Создать") }
                },
                dismissButton = { TextButton(onClick = { showNameDialog = false }) { Text("Отмена") } }
            )
        }
    } else {
        val inProgram by db.programDao().getExercisesForProgram(current.id).collectAsState(initial = emptyList())
        val allExercises by db.exerciseDao().getAll().collectAsState(initial = emptyList())
        var showPicker by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(current.name) },
                    navigationIcon = {
                        IconButton(onClick = { opened = null }) { Icon(Icons.Default.ArrowBack, "Назад") }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showPicker = true }) { Icon(Icons.Default.Add, "Добавить") }
            }
        ) { padding ->
            LazyColumn(Modifier.padding(padding).fillMaxSize()) {
                items(inProgram, key = { it.id }) { ex ->
                    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(ex.name, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                scope.launch { db.programDao().removeExercise(current.id, ex.id) }
                            }) { Icon(Icons.Default.Delete, "Убрать") }
                        }
                    }
                }
            }
        }

        if (showPicker) {
            val available = allExercises.filter { ex -> inProgram.none { it.id == ex.id } }
            AlertDialog(
                onDismissRequest = { showPicker = false },
                title = { Text("Добавить упражнение") },
                text = {
                    if (available.isEmpty()) Text("Все упражнения уже добавлены (или база пуста)")
                    else LazyColumn {
                        items(available, key = { it.id }) { ex ->
                            Text(
                                ex.name,
                                Modifier.fillMaxWidth().clickable {
                                    scope.launch {
                                        db.programDao().addExercise(
                                            ProgramExercise(
                                                programId = current.id,
                                                exerciseId = ex.id,
                                                orderIndex = inProgram.size
                                            )
                                        )
                                    }
                                    showPicker = false
                                }.padding(vertical = 12.dp)
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showPicker = false }) { Text("Закрыть") } }
            )
        }
    }
}
