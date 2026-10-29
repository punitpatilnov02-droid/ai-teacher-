package com.example

import com.example.data.TutorRepository
import com.example.data.local.StudyMaterialEntity
import com.example.model.MasteryStatus
import com.example.model.StudyDocument
import com.example.model.SubjectType
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testSubjectSwitching() {
        val repo = TutorRepository()
        assertEquals(SubjectType.MATH, repo.currentSubject.value)

        repo.selectSubject(SubjectType.PHYSICS)
        assertEquals(SubjectType.PHYSICS, repo.currentSubject.value)
    }

    @Test
    fun testGoalTogglingRewardsXp() {
        val repo = TutorRepository()
        val initialXp = repo.userStats.value.totalXp
        val uncompletedGoal = repo.goals.value.first { !it.isCompleted }

        repo.toggleGoal(uncompletedGoal.id)
        val updatedGoal = repo.goals.value.first { it.id == uncompletedGoal.id }
        assertTrue(updatedGoal.isCompleted)
        assertEquals(initialXp + uncompletedGoal.xpReward, repo.userStats.value.totalXp)
    }

    @Test
    fun testFlashcardMasteryUpdate() {
        val repo = TutorRepository()
        val card = repo.flashcards.value.first()
        repo.updateFlashcardMastery(card.id, MasteryStatus.MASTERED)

        val updatedCard = repo.flashcards.value.first { it.id == card.id }
        assertEquals(MasteryStatus.MASTERED, updatedCard.mastery)
    }

    @Test
    fun testProUpgradeStatus() {
        val repo = TutorRepository()
        assertFalse(repo.userStats.value.isProUser)

        repo.upgradeToPro()
        assertTrue(repo.userStats.value.isProUser)
    }

    @Test
    fun testAddDocumentAndAIAnalysis() {
        val repo = TutorRepository()
        val initialCount = repo.documents.value.size
        val newDoc = repo.addDocument(
            title = "Quantum Mechanics Intro",
            subject = SubjectType.PHYSICS,
            format = "Notes",
            content = "Schrödinger equation describes how quantum wavefunctions evolve.",
            summary = "Introduction to wave mechanics."
        )

        assertEquals(initialCount + 1, repo.documents.value.size)
        assertEquals("Quantum Mechanics Intro", newDoc.title)
    }

    @Test
    fun testInstamojoPaymentAccountUrl() {
        assertEquals("https://imjo.in/WhRtvz", TutorRepository.INSTAMOJO_PAYMENT_URL)
    }

    @Test
    fun testQuizCompletionRecordsAttempt() {
        val repo = TutorRepository()
        assertEquals(0, repo.quizHistory.value.size)

        repo.recordQuizCompletion(score = 4, total = 5, subject = SubjectType.MATH)
        assertEquals(1, repo.quizHistory.value.size)
        val attempt = repo.quizHistory.value.first()
        assertEquals(4, attempt.score)
        assertEquals(5, attempt.totalQuestions)
        assertEquals(80, attempt.percentage)
        assertEquals("MATH", attempt.subjectTypeName)
    }

    @Test
    fun testStudyMaterialEntityConversion() {
        val doc = StudyDocument(
            id = "test_doc_1",
            title = "Linear Algebra",
            subjectType = SubjectType.MATH,
            format = "PDF",
            subtitle = "Eigenvalues & Matrices",
            pageCount = 12,
            summary = "Matrix transformations summary",
            content = "Det(A - lambda*I) = 0",
            keyTakeaways = listOf("Eigenvectors do not rotate, only scale"),
            formulaOrEquations = listOf("Av = lambda*v"),
            uploadDate = "Today"
        )

        val entity = StudyMaterialEntity.fromDomainModel(doc)
        assertEquals("test_doc_1", entity.id)
        assertEquals("MATH", entity.subjectTypeName)

        val backToDoc = entity.toDomainModel()
        assertEquals(doc.id, backToDoc.id)
        assertEquals(doc.title, backToDoc.title)
        assertEquals(doc.keyTakeaways, backToDoc.keyTakeaways)
        assertEquals(doc.formulaOrEquations, backToDoc.formulaOrEquations)
    }

    @Test
    fun testSevenDayTrialInitialStateAndAutoLockSimulation() {
        val repo = TutorRepository()
        val initialStatus = repo.trialStatus.value

        // 7-दिन का ट्रायल सक्रिय होना चाहिए और शुरुआत में लॉक नहीं होना चाहिए
        assertTrue(initialStatus.isTrialActive)
        assertFalse(initialStatus.isCompletelyLocked)
        assertTrue(initialStatus.daysRemaining in 6..7)

        // 7-दिन समाप्त होने का परीक्षण सिमुलेशन
        repo.simulateTrialExpiry()
        val lockedStatus = repo.trialStatus.value
        assertFalse(lockedStatus.isTrialActive)
        assertTrue(lockedStatus.isCompletelyLocked)
        assertEquals(0, lockedStatus.daysRemaining)

        // पुनः रीसेट करने पर ऐप अनलॉक हो जाना चाहिए
        repo.resetTrial()
        val resetStatus = repo.trialStatus.value
        assertTrue(resetStatus.isTrialActive)
        assertFalse(resetStatus.isCompletelyLocked)
    }

    @Test
    fun testPlanPurchaseUnlocksFeaturesWithExpiryDate() {
        val repo = TutorRepository()
        repo.simulateTrialExpiry()
        assertTrue(repo.trialStatus.value.isCompletelyLocked)

        val plan = com.example.data.SampleData.subscriptionPlans.first { it.durationDays == 30 }
        repo.purchasePlan(plan)

        val status = repo.trialStatus.value
        assertFalse(status.isCompletelyLocked)
        assertEquals(plan.id, status.activePlanId)
        assertNotNull(status.planExpiryTimestamp)
        assertTrue(status.planExpiryTimestamp!! > System.currentTimeMillis())
        assertTrue(repo.userStats.value.isProUser)
    }

    @Test
    fun testPdfGamifiedLearningSessionAndComboScoring() {
        val repo = TutorRepository()
        val doc = repo.documents.value.first()

        // पीडीएफ गेम सत्र प्रारंभ
        repo.startPdfGameSession(doc)
        val gameState = repo.gameSessionState.value
        val questions = repo.activeGameQuestions.value

        assertEquals(doc.title, gameState.activePdfTitle)
        assertEquals(3, gameState.livesRemaining)
        assertEquals(1, gameState.comboMultiplier)
        assertTrue(questions.isNotEmpty())

        val firstQuestion = questions[0]
        // सही उत्तर सबमिट करने पर स्कोर और कॉम्बो बढ़ना चाहिए
        repo.submitGameAnswer(firstQuestion.correctIndex)
        val updatedState = repo.gameSessionState.value
        assertTrue(updatedState.score > 0)
        assertTrue(updatedState.comboMultiplier >= 2)
        assertEquals(3, updatedState.livesRemaining)

        // गलत उत्तर सबमिट करने पर एक दिल (life) कम होना चाहिए और कॉम्बो रीसेट
        val nextQuestion = questions[updatedState.currentQuestionIndex]
        val wrongIndex = if (nextQuestion.correctIndex == 0) 1 else 0
        repo.submitGameAnswer(wrongIndex)

        val stateAfterWrong = repo.gameSessionState.value
        assertEquals(2, stateAfterWrong.livesRemaining)
        assertEquals(1, stateAfterWrong.comboMultiplier)

        // गेम सत्र से बाहर निकलें
        repo.exitPdfGameSession()
        assertTrue(repo.gameSessionState.value.activePdfTitle.isBlank())
    }

    @Test
    fun testDeleteAccountAndAllDataCompliance() {
        val repo = TutorRepository()
        repo.recordQuizCompletion(score = 5, total = 5, subject = SubjectType.MATH)
        val plan = com.example.data.SampleData.subscriptionPlans.first()
        repo.purchasePlan(plan)
        assertTrue(repo.userStats.value.isProUser)
        assertEquals(1, repo.quizHistory.value.size)

        // Store compliance data deletion
        repo.deleteAccountAndAllData()

        assertFalse(repo.userStats.value.isProUser)
        assertEquals(0, repo.quizHistory.value.size)
        assertTrue(repo.trialStatus.value.isTrialActive)
        assertFalse(repo.trialStatus.value.isCompletelyLocked)
    }

    @Test
    fun testLiveClassStreamLayoutAndLectures() {
        val destinations = com.example.ui.AppDestination.values()
        assertTrue(destinations.contains(com.example.ui.AppDestination.LIVE_CLASS))
        assertTrue(com.example.ui.screens.SampleLiveLectures.isNotEmpty())
        val lecture = com.example.ui.screens.SampleLiveLectures.first()
        assertTrue(lecture.streamUrl.startsWith("http"))
        assertTrue(lecture.viewerCount > 0)
    }
}
