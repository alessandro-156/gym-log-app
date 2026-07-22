package com.alessandro.gymlog.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAll(): Flow<List<Exercise>>

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

    @Query("SELECT * FROM weight_history")
    suspend fun getAllOnce(): List<WeightHistory>
}

@Dao
interface WorkoutDayDao {
    @Query("SELECT * FROM workout_days")
    fun getAll(): Flow<List<WorkoutDay>>

    @Insert
    suspend fun insert(day: WorkoutDay): Long

    @Update
    suspend fun update(day: WorkoutDay)

    @Delete
    suspend fun delete(day: WorkoutDay)
}
