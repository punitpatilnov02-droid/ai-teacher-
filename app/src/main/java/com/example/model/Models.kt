package com.example.model

/**
 * ब्राइटस्टेप कोर डेटा मॉडल्स (Brightstep Core Data Models)
 * सभी मॉडल्स हिंदी में प्रलेखित हैं ताकि विकास को आसानी से समझा जा सके।
 */

enum class SubjectType(
    val title: String,
    val hindiTitle: String,
    val emoji: String,
    val color: Long,
    val shortName: String,
    val tagline: String
) {
    MATH("Mathematics", "गणित", "📐", 0xFF3B82F6, "गणित", "कैलकुलस, रैखिक बीजगणित और प्रमेय"),
    PHYSICS("Physics", "भौतिक विज्ञान", "🔭", 0xFF8B5CF6, "भौतिकी", "यांत्रिकी, क्वांटम भौतिकी और विद्युत चुंबकत्व"),
    CHEMISTRY("Chemistry", "रसायन विज्ञान", "⚗️", 0xFF10B981, "रसायन", "कार्बनिक अभिक्रियाएं और आणविक बंधन"),
    BIOLOGY("Biology", "जीव विज्ञान", "🌿", 0xFF059669, "जीव विज्ञान", "आनुवंशिकी, कोशिकीय श्वसन और डीएनए"),
    HISTORY("History", "इतिहास", "🏛️", 0xFFF59E0B, "इतिहास", "सभ्यताएं, क्रांतियां और विश्व इतिहास"),
    ENGLISH("English", "अंग्रेज़ी साहित्य", "📖", 0xFFEC4899, "अंग्रेज़ी", "साहित्य विश्लेषण, व्याकरण और निबंध"),
    CODING("Programming", "प्रोग्रामिंग", "💻", 0xFF06B6D4, "कोडिंग", "एल्गोरिदम, डेटा स्ट्रक्चर और सिस्टम डिज़ाइन")
}

data class StepBreakdown(
    val stepNumber: Int,
    val title: String,
    val explanation: String,
    val formulaOrSnippet: String? = null
)

enum class Sender {
    AI, USER
}

data class ChatMessage(
    val id: String,
    val sender: Sender,
    val text: String,
    val timestamp: String,
    val steps: List<StepBreakdown> = emptyList(),
    val formulaOrCode: String? = null,
    val quickReplies: List<String> = emptyList(),
    val isVoice: Boolean = false,
    val attachedDocTitle: String? = null
)

data class StudyDocument(
    val id: String,
    val title: String,
    val subjectType: SubjectType,
    val format: String, // e.g. "PDF", "Notes", "Slides", "Lecture"
    val subtitle: String,
    val pageCount: Int,
    val summary: String,
    val content: String,
    val keyTakeaways: List<String>,
    val formulaOrEquations: List<String> = emptyList(),
    val uploadDate: String = "आज",
    val isGamifiedReady: Boolean = true
)

data class QuizQuestion(
    val id: String,
    val subjectType: SubjectType,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String? = null
)

enum class MasteryStatus(val hindiLabel: String) {
    NEW("नया"),
    REVIEWING("अभ्यास जारी"),
    MASTERED("कंठस्थ / महारत")
}

data class Flashcard(
    val id: String,
    val subjectType: SubjectType,
    val front: String,
    val back: String,
    val hint: String? = null,
    val mastery: MasteryStatus = MasteryStatus.NEW
)

data class StudyGoal(
    val id: String,
    val title: String,
    val subjectType: SubjectType,
    val isCompleted: Boolean,
    val xpReward: Int,
    val dueTag: String
)

enum class TeachingStyle(val label: String, val hindiLabel: String, val description: String) {
    STEP_BY_STEP("Step-by-Step", "चरण-दर-चरण गुरु", "जटिल सिद्धांतों को क्रमवार स्पष्ट रूप से समझाता है"),
    SOCRATIC("Socratic Tutor", "सुकरात पद्धति", "प्रश्नों के माध्यम से सोचने और उत्तर खोजने में मार्गदर्शन"),
    FRIENDLY_COACH("Friendly Coach", "मित्रवत कोच", "रोचक उदाहरणों और प्रेरणा के साथ सिखाता है"),
    EXAM_PREP("Exam Master", "परीक्षा विशेषज्ञ", "शॉर्टकट, महत्वपूर्ण सूत्र और परीक्षा नियमों पर केंद्रित")
}

data class TutorStatus(
    val isOnline: Boolean = true,
    val isListening: Boolean = false,
    val isThinking: Boolean = false,
    val currentMood: String = "मार्गदर्शन के लिए तैयार",
    val style: TeachingStyle = TeachingStyle.STEP_BY_STEP,
    val audioEnabled: Boolean = true
)

data class UserStats(
    val streakDays: Int = 7,
    val totalMinutesStudied: Int = 345,
    val questionsAnswered: Int = 84,
    val totalXp: Int = 1250,
    val level: Int = 4,
    val rankTitle: String = "विद्वान शिक्षार्थी (Scholar)",
    val isProUser: Boolean = false
)

data class SubscriptionTier(
    val id: String,
    val title: String,
    val priceDisplay: String,
    val periodDisplay: String,
    val durationDays: Int,
    val isPopular: Boolean,
    val features: List<String>,
    val buttonText: String = "Instamojo से खरीदें",
    val accentColor: Long,
    val allows3DVideo: Boolean = false,
    val allowsUnlimitedPdfGames: Boolean = false
)

data class UserProfile(
    val name: String = "राहुल शर्मा",
    val email: String = "rahul.student@brightstep.in",
    val isLoggedIn: Boolean = true,
    val memberSince: String = "अक्टूबर 2026",
    val activePlan: String = "मुफ़्त 7-दिन का ट्रायल"
)

// =================== नए फीचर्स के लिए मॉडल्स ===================

/**
 * 7-दिन का मुफ़्त ट्रायल और ऑटो-लॉक स्थिति
 */
data class TrialStatus(
    val signUpTimestamp: Long,
    val trialDurationMs: Long = 7 * 24 * 60 * 60 * 1000L, // 7 दिन
    val isTrialActive: Boolean = true,
    val daysRemaining: Int = 7,
    val hoursRemaining: Int = 0,
    val activePlanId: String? = null,
    val planExpiryTimestamp: Long? = null,
    val isCompletelyLocked: Boolean = false
)

/**
 * पीडीएफ से निर्मित गेमीफाइड प्रश्न
 */
data class GameQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val sourcePdfTitle: String,
    val pointValue: Int = 100
)

/**
 * इंटरएक्टिव 3D ट्यूटर गेम सत्र
 */
data class GameSessionState(
    val activePdfTitle: String = "",
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 5,
    val score: Int = 0,
    val comboMultiplier: Int = 1,
    val livesRemaining: Int = 3,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val tutorReaction: String = "चलो खेलते हैं और सीखते हैं! 🎮",
    val streak: Int = 0
)
