package com.alessandro.gymlog.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises ORDER BY name")
    suspend fun getAllOnce(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): Exercise?

    @Insert
    suspend fun insert(exercise: Exercise): Long

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}

@Dao
interface ProgramDao {
    @Query("SELECT * FROM programs ORDER BY name")
    fun getAll(): Flow<List<Program>>

    @Query("SELECT * FROM programs ORDER BY name")
    suspend fun getAllOnce(): List<Program>

    @Insert
    suspend fun insert(program: Program): Long

    @Update
    suspend fun update(program: Program)

    @Delete
    suspend fun delete(program: Program)

    @Query("""SELECT e.* FROM exercises e
        INNER JOIN program_exercises pe ON pe.exerciseId = e.id
        WHERE pe.programId = :programId ORDER BY pe.orderIndex""")
    fun getExercisesForProgram(programId: Long): Flow<List<Exercise>>

    @Query("""SELECT e.* FROM exercises e
        INNER JOIN program_exercises pe ON pe.exerciseId = e.id
        WHERE pe.programId = :programId ORDER BY pe.orderIndex""")
    suspend fun getExercisesForProgramOnce(programId: Long): List<Exercise>

    @Insert
    suspend fun addExercise(link: ProgramExercise)

    @Query("DELETE FROM program_exercises WHERE programId = :programId AND exerciseId = :exerciseId")
    suspend fun removeExercise(programId: Long, exerciseId: Long)
}

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(entry: WeightHistory)

    @Query("SELECT * FROM weight_history WHERE exerciseId = :exerciseId ORDER BY dateEpochDay")
    fun getForExercise(exerciseId: Long): Flow<List<WeightHistory>>

    @Query("SELECT * FROM weight_history WHERE dateEpochDay BETWEEN :from AND :to ORDER BY dateEpochDay")
    suspend fun getBetween(from: Long, to: Long): List<WeightHistory>

    @Query("SELECT * FROM weight_history")
    suspend fun getAllOnce(): List<WeightHistory>
}

@Dao
interface WorkoutDayDao {
    @Query("SELECT * FROM workout_days")
    fun getAll(): Flow<List<WorkoutDay>>

    @Query("SELECT * FROM workout_days WHERE dateEpochDay BETWEEN :from AND :to")
    suspend fun getBetween(from: Long, to: Long): List<WorkoutDay>

    @Query("UPDATE workout_days SET completed = 1 WHERE programId = :programId AND dateEpochDay = :day")
    suspend fun markCompleted(programId: Long, day: Long): Int

    @Insert
    suspend fun insert(day: WorkoutDay): Long

    @Update
    suspend fun update(day: WorkoutDay)

    @Delete
    suspend fun delete(day: WorkoutDay)
}
