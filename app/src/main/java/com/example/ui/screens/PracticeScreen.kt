package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.SubjectSelectorRow
import com.example.ui.theme.*

enum class PracticeTab {
    FLASHCARDS, QUIZ, HISTORY
}

@Composable
fun PracticeScreen(
    currentSubject: SubjectType,
    flashcards: List<Flashcard>,
    quizzes: List<QuizQuestion>,
    quizHistory: List<com.example.data.local.QuizAttemptEntity> = emptyList(),
    onSelectSubject: (SubjectType) -> Unit,
    onUpdateFlashcardMastery: (String, MasteryStatus) -> Unit,
    onQuizCompleted: (score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(PracticeTab.FLASHCARDS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("practice_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Practice & Mastery",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Active recall & Room-persisted quiz history",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Tab Switcher Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedTab == PracticeTab.FLASHCARDS) BrightstepPrimary else Color.Transparent,
                        modifier = Modifier.clickable { selectedTab = PracticeTab.FLASHCARDS }
                    ) {
                        Text(
                            text = "Cards",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == PracticeTab.FLASHCARDS) Color.White else Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedTab == PracticeTab.QUIZ) BrightstepPrimary else Color.Transparent,
                        modifier = Modifier.clickable { selectedTab = PracticeTab.QUIZ }
                    ) {
                        Text(
                            text = "Quiz",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == PracticeTab.QUIZ) Color.White else Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedTab == PracticeTab.HISTORY) BrightstepPrimary else Color.Transparent,
                        modifier = Modifier.clickable { selectedTab = PracticeTab.HISTORY }
                    ) {
                        Text(
                            text = "History",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == PracticeTab.HISTORY) Color.White else Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Subject Selector
        SubjectSelectorRow(
            selectedSubject = currentSubject,
            onSelectSubject = onSelectSubject
        )

        when (selectedTab) {
            PracticeTab.FLASHCARDS -> {
                FlashcardDeckView(
                    flashcards = flashcards,
                    onUpdateMastery = onUpdateFlashcardMastery
                )
            }
            PracticeTab.QUIZ -> {
                QuizRunnerView(
                    quizzes = quizzes,
                    onQuizCompleted = onQuizCompleted
                )
            }
            PracticeTab.HISTORY -> {
                QuizHistoryView(quizHistory = quizHistory)
            }
        }
    }
}

@Composable
fun QuizHistoryView(
    quizHistory: List<com.example.data.local.QuizAttemptEntity>
) {
    if (quizHistory.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                Text("No quiz attempts recorded in local Room database yet.", color = Color.Gray, fontSize = 13.sp)
                Text("Complete a diagnostic quiz to track your history!", color = BrightstepPrimary, fontSize = 12.sp)
            }
        }
    } else {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ROOM DATABASE LOG (${quizHistory.size} Attempts)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BrightstepEmerald.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "SQLite Local Persistence",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightstepEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            items(quizHistory, key = { it.id }) { attempt ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (attempt.percentage >= 70) BrightstepEmerald.copy(alpha = 0.2f)
                                        else BrightstepAmber.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${attempt.percentage}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (attempt.percentage >= 70) BrightstepEmerald else BrightstepAmber
                                )
                            }

                            Column {
                                Text(
                                    text = "${attempt.subjectTypeName} Diagnostic",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Score: ${attempt.score}/${attempt.totalQuestions} • +${attempt.xpEarned} XP",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Text(
                            text = attempt.formattedDate,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FlashcardDeckView(
    flashcards: List<Flashcard>,
    onUpdateMastery: (String, MasteryStatus) -> Unit
) {
    if (flashcards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No flashcards yet for this subject. Ask the tutor to generate some!", color = Color.Gray, fontSize = 14.sp)
        }
        return
    }

    var currentIndex by remember(flashcards) { mutableStateOf(0) }
    var isFlipped by remember(currentIndex) { mutableStateOf(false) }

    val currentCard = flashcards.getOrNull(currentIndex) ?: flashcards[0]

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(220),
        label = "card_flip"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Card ${currentIndex + 1} of ${flashcards.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF94A3B8)
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (currentCard.mastery) {
                    MasteryStatus.MASTERED -> BrightstepEmerald.copy(alpha = 0.2f)
                    MasteryStatus.REVIEWING -> BrightstepAmber.copy(alpha = 0.2f)
                    MasteryStatus.NEW -> BrightstepPrimary.copy(alpha = 0.2f)
                }
            ) {
                Text(
                    text = currentCard.mastery.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (currentCard.mastery) {
                        MasteryStatus.MASTERED -> BrightstepEmerald
                        MasteryStatus.REVIEWING -> BrightstepAmber
                        MasteryStatus.NEW -> BrightstepPrimary
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Flippable 3D Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 14.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { isFlipped = !isFlipped }
                .testTag("interactive_flashcard"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFlipped) Color(0xFF1E293B) else Color(0xFF111827)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(
                        if (isFlipped) BrightstepAccent.copy(alpha = 0.6f) else BrightstepPrimary.copy(alpha = 0.6f),
                        Color.Transparent
                    )
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // Front of card
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "QUESTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightstepPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentCard.front,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                        if (currentCard.hint != null) {
                            Text(
                                text = "Hint: ${currentCard.hint}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = "Tap to flip and reveal answer",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    // Back of card (rotated 180 so text is readable)
                    Column(
                        modifier = Modifier.graphicsLayer { rotationY = 180f },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "ANSWER & EXPLANATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightstepAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentCard.back,
                            fontSize = 16.sp,
                            color = Color(0xFFF1F5F9),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Text(
                            text = "Tap to flip back",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Action Buttons: Needs Review / Mastered / Next / Prev
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    onUpdateMastery(currentCard.id, MasteryStatus.REVIEWING)
                    if (currentIndex < flashcards.size - 1) currentIndex++
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(BrightstepAmber, Color.Transparent))
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = BrightstepAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Review Again", fontSize = 12.sp, color = BrightstepAmber)
            }

            Button(
                onClick = {
                    onUpdateMastery(currentCard.id, MasteryStatus.MASTERED)
                    if (currentIndex < flashcards.size - 1) currentIndex++
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrightstepEmerald)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mastered", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Previous / Next Nav
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous")
            }

            TextButton(
                onClick = { if (currentIndex < flashcards.size - 1) currentIndex++ },
                enabled = currentIndex < flashcards.size - 1
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun QuizRunnerView(
    quizzes: List<QuizQuestion>,
    onQuizCompleted: (score: Int, total: Int) -> Unit
) {
    if (quizzes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No diagnostic questions found for this subject.", color = Color.Gray)
        }
        return
    }

    var questionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val currentQuiz = quizzes[questionIndex]

    if (isFinished) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(BrightstepEmerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = BrightstepEmerald, modifier = Modifier.size(36.dp))
                }
                Text("Diagnostic Quiz Complete!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "Score: $correctCount / ${quizzes.size} (${(correctCount * 100) / quizzes.size}%)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrightstepPrimary
                )
                Text(
                    text = "+${correctCount * 25} XP earned for your Brightstep rank!",
                    fontSize = 12.sp,
                    color = BrightstepAmber
                )
                Button(
                    onClick = {
                        questionIndex = 0
                        selectedOption = null
                        isSubmitted = false
                        correctCount = 0
                        isFinished = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Restart Quiz")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${questionIndex + 1} of ${quizzes.size}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Score: $correctCount",
                    fontSize = 12.sp,
                    color = BrightstepEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = currentQuiz.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 22.sp
            )

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentQuiz.options.forEachIndexed { index, option ->
                    val isChosen = selectedOption == index
                    val isCorrect = currentQuiz.correctIndex == index

                    val containerColor = when {
                        !isSubmitted && isChosen -> BrightstepPrimary.copy(alpha = 0.25f)
                        isSubmitted && isCorrect -> BrightstepEmerald.copy(alpha = 0.25f)
                        isSubmitted && isChosen && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.25f)
                        else -> Color(0xFF1E293B)
                    }

                    val borderColor = when {
                        !isSubmitted && isChosen -> BrightstepPrimary
                        isSubmitted && isCorrect -> BrightstepEmerald
                        isSubmitted && isChosen && !isCorrect -> Color(0xFFEF4444)
                        else -> Color.White.copy(alpha = 0.08f)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isSubmitted) { selectedOption = index },
                        color = containerColor
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${('A'.code + index).toChar()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Explanation Card after submission
            if (isSubmitted) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedOption == currentQuiz.correctIndex) Color(0xFF064E3B) else Color(0xFF450A0A)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (selectedOption == currentQuiz.correctIndex) "✓ Correct!" else "✗ Incorrect",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = currentQuiz.explanation,
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit / Next Question Button
            Button(
                onClick = {
                    if (!isSubmitted) {
                        isSubmitted = true
                        if (selectedOption == currentQuiz.correctIndex) {
                            correctCount++
                        }
                    } else {
                        if (questionIndex < quizzes.size - 1) {
                            questionIndex++
                            selectedOption = null
                            isSubmitted = false
                        } else {
                            isFinished = true
                            onQuizCompleted(correctCount, quizzes.size)
                        }
                    }
                },
                enabled = selectedOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("quiz_submit_action")
            ) {
                Text(
                    text = if (!isSubmitted) "Submit Answer" else if (questionIndex < quizzes.size - 1) "Next Question" else "See Final Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
