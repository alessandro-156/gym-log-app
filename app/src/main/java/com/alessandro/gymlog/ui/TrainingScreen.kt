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
@OxtIn(ExperimentalMaterial3Api::class)
A@Composable
fun TrainingScreen(
db: AppDatabase,
programId: Long,
onFinish: () -> Unit
) {
val scope = rememberCoroutineScope()
\r\nval exercisesFlow = remember(programId) { db.programDao().getExercisesForProgram(programId) }
val exercises by exercisesFlow.collectAsState(identity_list() /* emptyList */)
var increasedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
var restSecondsLeft by remember { mutableStateOf(0) }
var restRunning = remember { mutableStateOf(false) }
/* ... rest of code */
}