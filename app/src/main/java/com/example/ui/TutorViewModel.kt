package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.data.TutorRepository
import com.example.data.local.QuizAttemptEntity
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val title: String, val iconName: String) {
    CHAT("AI Tutor", "robot"),
    LIVE_CLASS("Live Class", "video"),
    DOCUMENTS("Library", "books"),
    PRACTICE("Practice", "cards"),
    PLANS("Plans", "credit_card"),
    PROGRESS("Progress", "chart"),
    ACCOUNT("Account", "user")
}

class TutorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: TutorRepository = TutorRepository(context = application)

    val currentSubject: StateFlow<SubjectType> = repository.currentSubject
    val tutorStatus: StateFlow<TutorStatus> = repository.tutorStatus
    val userStats: StateFlow<UserStats> = repository.userStats
    val documents: StateFlow<List<StudyDocument>> = repository.documents
    val goals: StateFlow<List<StudyGoal>> = repository.goals
    val flashcards: StateFlow<List<Flashcard>> = repository.flashcards
    val quizzes: StateFlow<List<QuizQuestion>> = repository.quizzes
    val quizHistory: StateFlow<List<QuizAttemptEntity>> = repository.quizHistory
    val trialStatus: StateFlow<TrialStatus> = repository.trialStatus
    val activeGameQuestions: StateFlow<List<GameQuestion>> = repository.activeGameQuestions
    val gameSessionState: StateFlow<GameSessionState> = repository.gameSessionState
    val subscriptionPlans: List<SubscriptionTier> = SampleData.subscriptionPlans
    val instamojoPaymentUrl: String = TutorRepository.INSTAMOJO_PAYMENT_URL

    private val _currentDestination = MutableStateFlow(AppDestination.CHAT)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _selectedDocument = MutableStateFlow<StudyDocument?>(null)
    val selectedDocument: StateFlow<StudyDocument?> = _selectedDocument.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _showProDialog = MutableStateFlow(false)
    val showProDialog: StateFlow<Boolean> = _showProDialog.asStateFlow()

    private val _showAddDocDialog = MutableStateFlow(false)
    val showAddDocDialog: StateFlow<Boolean> = _showAddDocDialog.asStateFlow()

    private val _showPrivacyDialog = MutableStateFlow(false)
    val showPrivacyDialog: StateFlow<Boolean> = _showPrivacyDialog.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Filter messages for currently selected subject
    val currentMessages: StateFlow<List<ChatMessage>> = combine(
        repository.currentSubject,
        repository.messages
    ) { subject, messageMap ->
        messageMap[subject] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter flashcards for currently selected subject
    val currentSubjectFlashcards: StateFlow<List<Flashcard>> = combine(
        repository.currentSubject,
        repository.flashcards
    ) { subject, cards ->
        cards.filter { it.subjectType == subject }.ifEmpty { cards }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter quizzes for currently selected subject
    val currentSubjectQuizzes: StateFlow<List<QuizQuestion>> = combine(
        repository.currentSubject,
        repository.quizzes
    ) { subject, allQuizzes ->
        allQuizzes.filter { it.subjectType == subject }.ifEmpty { allQuizzes }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDestination(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun selectSubject(subject: SubjectType) {
        repository.selectSubject(subject)
    }

    fun setTeachingStyle(style: TeachingStyle) {
        repository.setTeachingStyle(style)
    }

    fun toggleAudio() {
        repository.toggleAudio()
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(text, _selectedDocument.value)
        }
    }

    fun toggleGoal(goalId: String) {
        repository.toggleGoal(goalId)
    }

    fun selectDocument(document: StudyDocument?) {
        _selectedDocument.value = document
    }

    fun updateFlashcardMastery(cardId: String, status: MasteryStatus) {
        repository.updateFlashcardMastery(cardId, status)
    }

    fun onQuizCompleted(score: Int, total: Int) {
        repository.recordQuizCompletion(score, total, currentSubject.value)
    }

    fun setShowProDialog(show: Boolean) {
        _showProDialog.value = show
    }

    fun setShowAddDocDialog(show: Boolean) {
        _showAddDocDialog.value = show
    }

    fun setShowPrivacyDialog(show: Boolean) {
        _showPrivacyDialog.value = show
    }

    fun addNewDocument(title: String, format: String, content: String, summary: String) {
        val doc = repository.addDocument(
            title = title,
            subject = currentSubject.value,
            format = format,
            content = content,
            summary = summary
        )
        _selectedDocument.value = doc
        _showAddDocDialog.value = false
    }

    fun upgradeToPro(planTitle: String = "Yearly Series") {
        repository.upgradeToPro()
        _userProfile.value = _userProfile.value.copy(activePlan = planTitle)
        _showProDialog.value = false
    }

    fun registerAccount(name: String, email: String) {
        _userProfile.value = UserProfile(
            name = name,
            email = email,
            isLoggedIn = true,
            memberSince = "Today",
            activePlan = _userProfile.value.activePlan
        )
        setDestination(AppDestination.CHAT)
    }

    fun deleteAccountAndData() {
        repository.deleteAccountAndAllData()
        _userProfile.value = UserProfile(
            name = "Student",
            email = "student@brightstep.ai",
            isLoggedIn = false,
            memberSince = "Today",
            activePlan = "7-Day Free Trial"
        )
        setDestination(AppDestination.CHAT)
    }

    fun toggleVoiceRecording() {
        val willRecord = !_isRecordingVoice.value
        _isRecordingVoice.value = willRecord
        if (!willRecord) {
            sendMessage("Can you visualize and explain the core equation in this section?")
        }
    }

    // ==================== गेमीफाइड लर्निंग (PDF Quiz Game) ====================

    /**
     * पीडीएफ से इंटरैक्टिव गेमिंग सत्र प्रारंभ करें
     */
    fun startPdfGame(doc: StudyDocument) {
        _selectedDocument.value = doc
        repository.startPdfGameSession(doc)
    }

    /**
     * गेम में उत्तर सबमिट करें
     */
    fun submitGameAnswer(selectedOptionIndex: Int) {
        repository.submitGameAnswer(selectedOptionIndex)
    }

    /**
     * गेम को पुनः प्रारंभ करें
     */
    fun restartPdfGame() {
        val currentDoc = _selectedDocument.value ?: documents.value.firstOrNull()
        if (currentDoc != null) {
            repository.startPdfGameSession(currentDoc)
        }
    }

    /**
     * पीडीएफ गेम सत्र से बाहर निकलें
     */
    fun exitPdfGame() {
        repository.exitPdfGameSession()
    }

    // ==================== 7-दिन का ट्रायल एवं सदस्यता प्रबंधन ====================

    /**
     * 7-दिन का ट्रायल तुरंत समाप्त करने का परीक्षण सिमुलेशन
     */
    fun simulateTrialExpiry() {
        repository.simulateTrialExpiry()
    }

    /**
     * परीक्षण हेतु 7-दिन का ट्रायल रीसेट करें
     */
    fun resetTrial() {
        repository.resetTrial()
    }

    /**
     * सदस्यता प्लान खरीदें और फीचर्स अनलॉक करें (समाप्ति तिथि सहित)
     */
    fun purchaseSubscriptionPlan(plan: SubscriptionTier) {
        repository.purchasePlan(plan)
        _userProfile.value = _userProfile.value.copy(activePlan = plan.title)
        _showProDialog.value = false
    }

    fun refreshTrialStatus() {
        repository.refreshTrialAndSubscriptionState()
    }
}
