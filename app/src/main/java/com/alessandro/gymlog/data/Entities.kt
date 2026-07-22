package com.alessandro.gymlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// База упражнений
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val currentWeight: Float,        // текущий рабочий вес, кг (0 если не применимо)
    val weightIncrement: Float,      // на сколько увеличивать за раз
    val sets: Int,                   // количество подходов
    val reps: Int,                   // повторений в подходе
    val durationMinutes: Int,        // время (бег/велотренажер), 0 если не применимо
    val restMinSeconds: Int,         // мин. отдых между подходами
    val restMaxSeconds: Int          // макс. отдых между подходами
)

// Программы тренировок
@Entity(tableName = "programs")
data class Program(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

// Связь программа-упражнение
@Entity(tableName = "program_exercises")
data class ProgramExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val programId: Long,
    val exerciseId: Long,
    val orderIndex: Int
)

// История весов (для прогресса в календаре)
@Entity(tableName = "weight_history")
data class WeightHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val weight: Float,
    val dateEpochDay: Long           // день (LocalDate.toEpochDay)
)

// Дни тренировок в календаре
@Entity(tableName = "workout_days")
data class WorkoutDay(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val programId: Long,
    val completed: Boolean = false
)
