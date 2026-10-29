package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.StudyDocument
import com.example.model.SubjectType

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subjectTypeName: String,
    val format: String,
    val subtitle: String,
    val pageCount: Int,
    val summary: String,
    val content: String,
    val keyTakeaways: List<String>,
    val formulaOrEquations: List<String>,
    val uploadDate: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): StudyDocument {
        val subject = try {
            SubjectType.valueOf(subjectTypeName)
        } catch (e: Exception) {
            SubjectType.MATH
        }
        return StudyDocument(
            id = id,
            title = title,
            subjectType = subject,
            format = format,
            subtitle = subtitle,
            pageCount = pageCount,
            summary = summary,
            content = content,
            keyTakeaways = keyTakeaways,
            formulaOrEquations = formulaOrEquations,
            uploadDate = uploadDate
        )
    }

    companion object {
        fun fromDomainModel(doc: StudyDocument): StudyMaterialEntity {
            return StudyMaterialEntity(
                id = doc.id,
                title = doc.title,
                subjectTypeName = doc.subjectType.name,
                format = doc.format,
                subtitle = doc.subtitle,
                pageCount = doc.pageCount,
                summary = doc.summary,
                content = doc.content,
                keyTakeaways = doc.keyTakeaways,
                formulaOrEquations = doc.formulaOrEquations,
                uploadDate = doc.uploadDate
            )
        }
    }
}

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectTypeName: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val xpEarned: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String
)

@Entity(tableName = "study_goals")
data class StudyGoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subjectTypeName: String,
    val isCompleted: Boolean,
    val xpReward: Int,
    val dueTag: String
)
