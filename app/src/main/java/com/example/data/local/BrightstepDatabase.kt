package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudyMaterialEntity::class,
        QuizAttemptEntity::class,
        StudyGoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class BrightstepDatabase : RoomDatabase() {

    abstract fun studyMaterialDao(): StudyMaterialDao
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun studyGoalDao(): StudyGoalDao

    companion object {
        @Volatile
        private var INSTANCE: BrightstepDatabase? = null

        fun getDatabase(context: Context): BrightstepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BrightstepDatabase::class.java,
                    "brightstep_study_database.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate initial materials & goals
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(database)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: BrightstepDatabase) {
            val materialEntities = SampleData.sampleDocuments.map {
                StudyMaterialEntity.fromDomainModel(it)
            }
            database.studyMaterialDao().insertMaterials(materialEntities)

            val goalEntities = SampleData.sampleGoals.map {
                StudyGoalEntity(
                    id = it.id,
                    title = it.title,
                    subjectTypeName = it.subjectType.name,
                    isCompleted = it.isCompleted,
                    xpReward = it.xpReward,
                    dueTag = it.dueTag
                )
            }
            database.studyGoalDao().insertGoals(goalEntities)

            // Initial diagnostic attempt history
            val initialAttempt = QuizAttemptEntity(
                subjectTypeName = "MATH",
                score = 3,
                totalQuestions = 4,
                percentage = 75,
                xpEarned = 75,
                formattedDate = "Earlier Today"
            )
            database.quizAttemptDao().insertQuizAttempt(initialAttempt)
        }
    }
}
