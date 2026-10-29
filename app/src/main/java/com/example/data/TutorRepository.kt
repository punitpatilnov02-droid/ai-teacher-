package com.example.data

import android.content.Context
import android.util.Log
import com.example.data.api.GeminiApiClient
import com.example.data.local.BrightstepDatabase
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.StudyGoalEntity
import com.example.data.local.StudyMaterialEntity
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ब्राइटस्टेप केंद्रीय डेटा रिपॉजिटरी (TutorRepository)
 * यह क्लास 7-दिन का मुफ़्त ट्रायल, इंस्टामोजो प्लान्स, पीडीएफ गेमिंग और स्वायत्त जेमिनी एआई ब्रेन को प्रबंधित करती है।
 */
class TutorRepository(
    private val context: Context? = null,
    private val database: BrightstepDatabase? = context?.let { BrightstepDatabase.getDatabase(it) }
) {

    companion object {
        private const val TAG = "ब्राइटस्टेप_रिपॉजिटरी"
        // आधिकारिक इंस्टामोजो भुगतान खाता लिंक
        const val INSTAMOJO_PAYMENT_URL = "https://imjo.in/WhRtvz"
        const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000L
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val geminiClient = GeminiApiClient()

    // 7-दिन का ट्रायल और सदस्यता स्थिति
    private val _signUpTimestamp = MutableStateFlow(System.currentTimeMillis())
    private val _trialStatus = MutableStateFlow(
        TrialStatus(
            signUpTimestamp = System.currentTimeMillis(),
            isTrialActive = true,
            daysRemaining = 7,
            hoursRemaining = 0,
            isCompletelyLocked = false
        )
    )
    val trialStatus: StateFlow<TrialStatus> = _trialStatus.asStateFlow()

    private val _currentSubject = MutableStateFlow(SubjectType.MATH)
    val currentSubject: StateFlow<SubjectType> = _currentSubject.asStateFlow()

    private val _messages = MutableStateFlow<Map<SubjectType, List<ChatMessage>>>(
        SubjectType.values().associateWith { SampleData.initialMessages(it) }
    )
    val messages: StateFlow<Map<SubjectType, List<ChatMessage>>> = _messages.asStateFlow()

    private val _documents = MutableStateFlow(SampleData.sampleDocuments)
    val documents: StateFlow<List<StudyDocument>> = _documents.asStateFlow()

    private val _goals = MutableStateFlow(SampleData.sampleGoals)
    val goals: StateFlow<List<StudyGoal>> = _goals.asStateFlow()

    private val _flashcards = MutableStateFlow(SampleData.sampleFlashcards)
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val _quizzes = MutableStateFlow(SampleData.sampleQuizzes)
    val quizzes: StateFlow<List<QuizQuestion>> = _quizzes.asStateFlow()

    private val _quizHistory = MutableStateFlow<List<QuizAttemptEntity>>(emptyList())
    val quizHistory: StateFlow<List<QuizAttemptEntity>> = _quizHistory.asStateFlow()

    private val _tutorStatus = MutableStateFlow(TutorStatus())
    val tutorStatus: StateFlow<TutorStatus> = _tutorStatus.asStateFlow()

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    // पीडीएफ गेम सत्र स्थिति (Gamified Learning State)
    private val _activeGameQuestions = MutableStateFlow<List<GameQuestion>>(emptyList())
    val activeGameQuestions: StateFlow<List<GameQuestion>> = _activeGameQuestions.asStateFlow()

    private val _gameSessionState = MutableStateFlow(GameSessionState())
    val gameSessionState: StateFlow<GameSessionState> = _gameSessionState.asStateFlow()

    init {
        Log.i(TAG, "ब्राइटस्टेप रिपॉजिटरी प्रारंभ हो रही है... ट्रायल स्थिति की जाँच की जा रही है।")
        refreshTrialAndSubscriptionState()

        // Room डेटाबेस से लोकल डेटा सिंक्रनाइज़ करें
        database?.let { db ->
            coroutineScope.launch {
                if (db.studyMaterialDao().getMaterialCount() == 0) {
                    BrightstepDatabase.populateInitialData(db)
                }

                db.studyMaterialDao().getAllMaterials().collect { entities ->
                    if (entities.isNotEmpty()) {
                        _documents.value = entities.map { it.toDomainModel() }
                    }
                }
            }

            coroutineScope.launch {
                db.studyGoalDao().getAllGoals().collect { goalEntities ->
                    if (goalEntities.isNotEmpty()) {
                        _goals.value = goalEntities.map { entity ->
                            val subject = try {
                                SubjectType.valueOf(entity.subjectTypeName)
                            } catch (e: Exception) {
                                SubjectType.MATH
                            }
                            StudyGoal(
                                id = entity.id,
                                title = entity.title,
                                subjectType = subject,
                                isCompleted = entity.isCompleted,
                                xpReward = entity.xpReward,
                                dueTag = entity.dueTag
                            )
                        }
                    }
                }
            }

            coroutineScope.launch {
                db.quizAttemptDao().getAllQuizAttempts().collect { attempts ->
                    _quizHistory.value = attempts
                }
            }
        }
    }

    /**
     * 7-दिन का ट्रायल और प्लान समाप्ति की जाँच (Strict Expiry Checker)
     */
    fun refreshTrialAndSubscriptionState() {
        val now = System.currentTimeMillis()
        val currentStatus = _trialStatus.value

        // यदि उपयोगकर्ता के पास कोई सक्रिय भुगतान योजना है
        if (currentStatus.activePlanId != null && currentStatus.planExpiryTimestamp != null) {
            val isPlanExpired = now > currentStatus.planExpiryTimestamp
            if (isPlanExpired) {
                Log.w(TAG, "भुगतान योजना समाप्त हो चुकी है! सभी फीचर्स ऑटो-लॉक किए जा रहे हैं।")
                _trialStatus.value = currentStatus.copy(
                    isTrialActive = false,
                    isCompletelyLocked = true,
                    daysRemaining = 0,
                    hoursRemaining = 0
                )
            } else {
                val msLeft = currentStatus.planExpiryTimestamp - now
                val daysLeft = (msLeft / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                val hoursLeft = ((msLeft / (1000 * 60 * 60)) % 24).toInt().coerceAtLeast(0)
                _trialStatus.value = currentStatus.copy(
                    isTrialActive = false,
                    isCompletelyLocked = false,
                    daysRemaining = daysLeft,
                    hoursRemaining = hoursLeft
                )
            }
            return
        }

        // यदि उपयोगकर्ता 7-दिन के मुफ़्त ट्रायल पर है
        val elapsed = now - currentStatus.signUpTimestamp
        val timeLeftMs = SEVEN_DAYS_MS - elapsed

        if (timeLeftMs <= 0) {
            Log.w(TAG, "7-दिन का मुफ़्त ट्रायल समाप्त हो चुका है! ऑटो-लॉक सक्रिय।")
            _trialStatus.value = currentStatus.copy(
                isTrialActive = false,
                isCompletelyLocked = true,
                daysRemaining = 0,
                hoursRemaining = 0
            )
        } else {
            val days = (timeLeftMs / (1000 * 60 * 60 * 24)).toInt()
            val hours = ((timeLeftMs / (1000 * 60 * 60)) % 24).toInt()
            _trialStatus.value = currentStatus.copy(
                isTrialActive = true,
                isCompletelyLocked = false,
                daysRemaining = days,
                hoursRemaining = hours
            )
            Log.d(TAG, "ट्रायल सक्रिय है: $days दिन $hours घंटे शेष।")
        }
    }

    /**
     * परीक्षण हेतु ट्रायल समाप्त करने का सिम्युलेटर (Strict Lock Testing)
     */
    fun simulateTrialExpiry() {
        Log.w(TAG, "परीक्षण: 7-दिन का मुफ़्त ट्रायल समाप्त किया गया। ऐप ऑटो-लॉक मोड में है।")
        _trialStatus.update {
            it.copy(
                signUpTimestamp = System.currentTimeMillis() - SEVEN_DAYS_MS - 1000L,
                isTrialActive = false,
                activePlanId = null,
                planExpiryTimestamp = null,
                isCompletelyLocked = true,
                daysRemaining = 0,
                hoursRemaining = 0
            )
        }
    }

    /**
     * परीक्षण हेतु 7-दिन का ट्रायल रीसेट करें
     */
    fun resetTrial() {
        Log.i(TAG, "परीक्षण: 7-दिन का मुफ़्त ट्रायल पुनः रीसेट किया गया।")
        _trialStatus.value = TrialStatus(
            signUpTimestamp = System.currentTimeMillis(),
            isTrialActive = true,
            daysRemaining = 7,
            hoursRemaining = 0,
            activePlanId = null,
            planExpiryTimestamp = null,
            isCompletelyLocked = false
        )
    }

    /**
     * प्लान आधारित फीचर अनलॉकिंग और समाप्ति तिथि निर्धारण
     */
    fun purchasePlan(plan: SubscriptionTier) {
        val now = System.currentTimeMillis()
        val expiry = now + (plan.durationDays.toLong() * 24 * 60 * 60 * 1000L)

        Log.i(TAG, "प्लान खरीदा गया: ${plan.title}, समाप्ति तिथि: $expiry (${plan.durationDays} दिन बाद)")

        _trialStatus.value = _trialStatus.value.copy(
            isTrialActive = false,
            activePlanId = plan.id,
            planExpiryTimestamp = expiry,
            isCompletelyLocked = false,
            daysRemaining = plan.durationDays,
            hoursRemaining = 0
        )

        _userStats.update {
            it.copy(
                isProUser = true,
                rankTitle = "ब्राइटस्टेप प्रो (${plan.title})"
            )
        }

    }

    /**
     * खाता और सारा डेटा मिटाएं (Google Play Store Data Safety & Privacy Policy Compliance)
     */
    fun deleteAccountAndAllData() {
        Log.w(TAG, "उपयोगकर्ता खाता और सारा डेटा पूर्ण रूप से मिटाया जा रहा है।")
        _userStats.value = UserStats(
            streakDays = 0,
            totalMinutesStudied = 0,
            questionsAnswered = 0,
            totalXp = 0,
            level = 1,
            rankTitle = "शुरुआती शिक्षार्थी (Beginner)",
            isProUser = false
        )
        _quizHistory.value = emptyList()
        resetTrial()
    }

    /**
     * प्रो में अपग्रेड करें (डिफ़ॉल्ट या वार्षिक प्लान)
     */
    fun upgradeToPro(planTitle: String = "Yearly Series") {
        val plan = SampleData.subscriptionPlans.firstOrNull { it.title.contains(planTitle, ignoreCase = true) }
            ?: SampleData.subscriptionPlans.first()
        purchasePlan(plan)
    }

    // ==================== गेमीफाइड लर्निंग (PDF Upload Game) ====================

    /**
     * चयनित पीडीएफ से गेमीफाइड क्विज़ गेम सत्र प्रारंभ करें
     */
    fun startPdfGameSession(document: StudyDocument) {
        Log.i(TAG, "पीडीएफ से गेमिंग सत्र शुरू किया जा रहा है: ${document.title}")
        val questions = SampleData.generateGameQuestionsForDocument(document)
        _activeGameQuestions.value = questions

        _gameSessionState.value = GameSessionState(
            activePdfTitle = document.title,
            currentQuestionIndex = 0,
            totalQuestions = questions.size,
            score = 0,
            comboMultiplier = 1,
            livesRemaining = 3,
            isGameOver = false,
            isVictory = false,
            tutorReaction = "नमस्ते! मैं आपके इस पीडीएफ से प्रश्न पूछूँगा। क्या आप तैयार हैं? 🚀",
            streak = 0
        )
    }

    /**
     * गेम में उत्तर सबमिट करें (कॉम्बो, स्कोर, लाइफ बार और 3D ट्यूटर प्रतिक्रिया)
     */
    fun submitGameAnswer(selectedOptionIndex: Int) {
        val currentState = _gameSessionState.value
        val questions = _activeGameQuestions.value
        if (questions.isEmpty() || currentState.isGameOver || currentState.isVictory) return

        val currentQuestion = questions[currentState.currentQuestionIndex]
        val isCorrect = selectedOptionIndex == currentQuestion.correctIndex

        if (isCorrect) {
            val newMultiplier = (currentState.comboMultiplier + 1).coerceAtMost(4)
            val addedScore = currentQuestion.pointValue * currentState.comboMultiplier
            val newStreak = currentState.streak + 1
            val nextIndex = currentState.currentQuestionIndex + 1

            val reactions = listOf(
                "शाबाश! बिल्कुल सटीक उत्तर! 🔥 ${newMultiplier}x कॉम्बो!",
                "शानदार! आपने पीडीएफ का यह सिद्धांत पूरी तरह समझ लिया है! 🏆",
                "गज़ब का ज्ञान! +$addedScore अंक जुड़े! 🌟",
                "बॉस लेवल की तैयारी! लगातार $newStreak सही उत्तर! ⚡"
            )

            if (nextIndex >= questions.size) {
                // विजय!
                _gameSessionState.value = currentState.copy(
                    score = currentState.score + addedScore,
                    comboMultiplier = newMultiplier,
                    streak = newStreak,
                    isVictory = true,
                    isGameOver = false,
                    tutorReaction = "बधाई हो! आपने इस पीडीएफ गेम को जीत लिया है! 👑 कुल अंक: ${currentState.score + addedScore}"
                )
                recordQuizCompletion(questions.size, questions.size, _currentSubject.value)
            } else {
                _gameSessionState.value = currentState.copy(
                    currentQuestionIndex = nextIndex,
                    score = currentState.score + addedScore,
                    comboMultiplier = newMultiplier,
                    streak = newStreak,
                    tutorReaction = reactions.random()
                )
            }
        } else {
            // ग़लत उत्तर: -1 दिल (Life), कॉम्बो रीसेट
            val newLives = currentState.livesRemaining - 1
            val wrongReactions = listOf(
                "ओह नहीं! कॉम्बो टूट गया। सही उत्तर: ${currentQuestion.options[currentQuestion.correctIndex]} 💔",
                "सावधानी से! ट्यूटर का सुझाव: ${currentQuestion.explanation}",
                "एक दिल ख़त्म! हिम्मत मत हारो, अगला प्रश्न ध्यान से पढ़ो! 🛡️"
            )

            if (newLives <= 0) {
                _gameSessionState.value = currentState.copy(
                    livesRemaining = 0,
                    isGameOver = true,
                    isVictory = false,
                    comboMultiplier = 1,
                    tutorReaction = "गेम ओवर! आपके सभी दिल ख़त्म हो गए। दोबारा अभ्यास करके मजबूत बनें! 🔄"
                )
            } else {
                _gameSessionState.value = currentState.copy(
                    livesRemaining = newLives,
                    comboMultiplier = 1,
                    streak = 0,
                    tutorReaction = wrongReactions.random()
                )
            }
        }
    }

    /**
     * पीडीएफ गेम सत्र समाप्त करें और मुख्य लाइब्रेरी पर वापस जाएं
     */
    fun exitPdfGameSession() {
        Log.i(TAG, "पीडीएफ गेम सत्र बंद किया गया।")
        _gameSessionState.value = GameSessionState()
        _activeGameQuestions.value = emptyList()
    }

    fun selectSubject(subject: SubjectType) {
        _currentSubject.value = subject
        _tutorStatus.update { it.copy(currentMood = "अध्ययन: ${subject.hindiTitle}") }
    }

    fun setTeachingStyle(style: TeachingStyle) {
        _tutorStatus.update { it.copy(style = style) }
    }

    fun toggleAudio() {
        _tutorStatus.update { it.copy(audioEnabled = !it.audioEnabled) }
    }

    // ==================== स्वायत्त जेमिनी एआई चैट ====================

    suspend fun sendMessage(userText: String, attachedDoc: StudyDocument? = null) {
        // यदि ट्रायल समाप्त हो चुका है और ऐप लॉक है, तो चैट की अनुमति न दें
        if (_trialStatus.value.isCompletelyLocked) {
            Log.w(TAG, "चैट अवरुद्ध: 7-दिन का ट्रायल समाप्त हो चुका है।")
            return
        }

        val subject = _currentSubject.value
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = Sender.USER,
            text = userText,
            timestamp = "अभी-अभी",
            attachedDocTitle = attachedDoc?.title
        )

        _messages.update { currentMap ->
            val list = currentMap[subject] ?: emptyList()
            currentMap + (subject to (list + userMsg))
        }

        _tutorStatus.update { it.copy(isThinking = true, currentMood = "एआई ब्रेन सोच रहा है...") }

        // स्वायत्त रूप से जेमिनी लाइव एपीआई से उत्तर लाने का प्रयास करें
        val geminiReply = geminiClient.generateTutorResponse(
            prompt = userText,
            subjectName = subject.hindiTitle,
            attachedPdfText = attachedDoc?.content
        )

        val aiResponse = if (!geminiReply.isNullOrBlank()) {
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = Sender.AI,
                text = geminiReply,
                timestamp = "अभी-अभी",
                quickReplies = listOf("इसे और विस्तार से समझाएं", "इस पर एक गेम खेलें", "मुख्य सूत्र बताएं")
            )
        } else {
            // यदि एपीआई कुंजी उपलब्ध नहीं है, तो स्थानीय स्वायत्त हिंदी रिस्पॉन्डर तुरंत उत्तर देता है
            generateIntelligentResponse(userText, subject, attachedDoc, _tutorStatus.value.style)
        }

        _messages.update { currentMap ->
            val list = currentMap[subject] ?: emptyList()
            currentMap + (subject to (list + aiResponse))
        }

        _tutorStatus.update { it.copy(isThinking = false, currentMood = "उत्तर तैयार है!") }
        _userStats.update { it.copy(questionsAnswered = it.questionsAnswered + 1, totalXp = it.totalXp + 20) }
    }

    private fun generateIntelligentResponse(
        prompt: String,
        subject: SubjectType,
        doc: StudyDocument?,
        style: TeachingStyle
    ): ChatMessage {
        val lower = prompt.lowercase()

        // 1. गेम / क्विज़ अनुरोध
        if (lower.contains("गेम") || lower.contains("quiz") || lower.contains("test me") || lower.contains("खेल")) {
            return ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = Sender.AI,
                text = "चलो ${subject.hindiTitle} का एक इंटरएक्टिव गेम खेलते हैं! नीचे दिए गए 'गेम मोड शुरू करें' पर टैप करें या इस प्रश्न का उत्तर दें:",
                timestamp = "अभी-अभी",
                steps = listOf(
                    StepBreakdown(1, "गेम मोड सक्रिय", "हर सही उत्तर पर कॉम्बो गुणक (Multiplier) और XP मिलेगा।"),
                    StepBreakdown(2, "लाइफ सुरक्षा", "आपके पास 3 दिल (❤️) हैं, ग़लत उत्तर से दिल कम होगा।"),
                    StepBreakdown(3, "बॉस लेवल", "अंत में एक कठिन बॉस सवाल आएगा!")
                ),
                quickReplies = listOf("पीडीएफ गेम खेलें", "सूत्र सारांश देखें", "चरण-दर-चरण समाधान")
            )
        }

        // 2. डिफ़ॉल्ट हिंदी समाधान
        return ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = Sender.AI,
            text = "नमस्ते! ${subject.hindiTitle} में '$prompt' के लिए चरण-दर-चरण व्याख्या नीचे दी गई है:",
            timestamp = "अभी-अभी",
            steps = listOf(
                StepBreakdown(1, "पहला चरण: मूल अवधारणा", "समस्या को समझें और आवश्यक चरों की पहचान करें।"),
                StepBreakdown(2, "दूसरा चरण: सूत्र का अनुप्रयोग", "संबंधित गणितीय या वैज्ञानिक सूत्र लागू करें।"),
                StepBreakdown(3, "तीसरा चरण: परिणाम सत्यापन", "सीमाओं और इकाइयों (Units) की जाँच करें।")
            ),
            quickReplies = listOf("इस पर क्विज़ खेलें", "आसान भाषा में समझाएं", "एक उदाहरण दें")
        )
    }

    fun toggleGoal(goalId: String) {
        _goals.update { list ->
            list.map { goal ->
                if (goal.id == goalId) {
                    val newCompleted = !goal.isCompleted
                    if (newCompleted) {
                        _userStats.update { stats ->
                            stats.copy(
                                totalXp = stats.totalXp + goal.xpReward,
                                totalMinutesStudied = stats.totalMinutesStudied + 10
                            )
                        }
                    }
                    database?.let { db ->
                        coroutineScope.launch {
                            db.studyGoalDao().updateGoalCompletion(goalId, newCompleted)
                        }
                    }
                    goal.copy(isCompleted = newCompleted)
                } else goal
            }
        }
    }

    fun addDocument(
        title: String,
        subject: SubjectType,
        format: String,
        content: String,
        summary: String
    ): StudyDocument {
        val newDoc = StudyDocument(
            id = "doc_${System.currentTimeMillis()}",
            title = title,
            subjectType = subject,
            format = format,
            subtitle = "उपयोगकर्ता द्वारा अपलोड नोट्स",
            pageCount = (content.length / 400).coerceAtLeast(1),
            summary = summary,
            content = content,
            keyTakeaways = listOf(
                "एआई द्वारा विश्लेषित अध्ययन सामग्री।",
                "गेमीफाइड क्विज़ और अभ्यास के लिए तैयार।"
            ),
            uploadDate = "अभी"
        )
        _documents.update { listOf(newDoc) + it }
        _userStats.update { it.copy(totalXp = it.totalXp + 30) }

        database?.let { db ->
            coroutineScope.launch {
                db.studyMaterialDao().insertMaterial(StudyMaterialEntity.fromDomainModel(newDoc))
            }
        }

        return newDoc
    }

    fun updateFlashcardMastery(cardId: String, newMastery: MasteryStatus) {
        _flashcards.update { list ->
            list.map { card ->
                if (card.id == cardId) card.copy(mastery = newMastery) else card
            }
        }
    }

    fun recordQuizCompletion(score: Int, total: Int, subject: SubjectType = _currentSubject.value) {
        val earnedXp = score * 25
        val percentage = if (total > 0) (score * 100) / total else 0
        _userStats.update {
            it.copy(
                totalXp = it.totalXp + earnedXp,
                questionsAnswered = it.questionsAnswered + total
            )
        }

        val dateStr = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(Date())
        val attemptEntity = QuizAttemptEntity(
            subjectTypeName = subject.name,
            score = score,
            totalQuestions = total,
            percentage = percentage,
            xpEarned = earnedXp,
            formattedDate = dateStr
        )

        _quizHistory.update { listOf(attemptEntity) + it }

        database?.let { db ->
            coroutineScope.launch {
                db.quizAttemptDao().insertQuizAttempt(attemptEntity)
            }
        }
    }
}
