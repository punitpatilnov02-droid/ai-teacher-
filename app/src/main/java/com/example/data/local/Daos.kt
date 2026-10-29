package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyMaterialDao {
    @Query("SELECT * FROM study_materials ORDER BY createdAt DESC")
    fun getAllMaterials(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE subjectTypeName = :subjectName ORDER BY createdAt DESC")
    fun getMaterialsBySubject(subjectName: String): Flow<List<StudyMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: StudyMaterialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<StudyMaterialEntity>)

    @Query("DELETE FROM study_materials WHERE id = :id")
    suspend fun deleteMaterialById(id: String)

    @Query("SELECT COUNT(*) FROM study_materials")
    suspend fun getMaterialCount(): Int
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE subjectTypeName = :subjectName ORDER BY timestamp DESC")
    fun getQuizAttemptsBySubject(subjectName: String): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)

    @Query("DELETE FROM quiz_attempts")
    suspend fun clearAllAttempts()

    @Query("SELECT COUNT(*) FROM quiz_attempts")
    suspend fun getAttemptCount(): Int
}

@Dao
interface StudyGoalDao {
    @Query("SELECT * FROM study_goals")
    fun getAllGoals(): Flow<List<StudyGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<StudyGoalEntity>)

    @Update
    suspend fun updateGoal(goal: StudyGoalEntity)

    @Query("UPDATE study_goals SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateGoalCompletion(id: String, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM study_goals")
    suspend fun getGoalCount(): Int
}
