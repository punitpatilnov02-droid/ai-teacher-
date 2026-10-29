package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.SubjectType
import com.example.ui.dialogs.AddDocumentDialog
import com.example.ui.dialogs.PrivacyPolicyDialog
import com.example.ui.dialogs.ProUpgradeDialog
import com.example.ui.screens.*
import com.example.ui.theme.BrightstepAccent
import com.example.ui.theme.BrightstepEmerald
import com.example.ui.theme.BrightstepPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TutorViewModel = viewModel()
) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val currentSubject by viewModel.currentSubject.collectAsStateWithLifecycle()
    val tutorStatus by viewModel.tutorStatus.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val selectedDoc by viewModel.selectedDocument.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val flashcards by viewModel.currentSubjectFlashcards.collectAsStateWithLifecycle()
    val quizzes by viewModel.currentSubjectQuizzes.collectAsStateWithLifecycle()
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val trialStatus by viewModel.trialStatus.collectAsStateWithLifecycle()
    val gameSessionState by viewModel.gameSessionState.collectAsStateWithLifecycle()
    val activeGameQuestions by viewModel.activeGameQuestions.collectAsStateWithLifecycle()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
    val showProDialog by viewModel.showProDialog.collectAsStateWithLifecycle()
    val showAddDocDialog by viewModel.showAddDocDialog.collectAsStateWithLifecycle()
    val showPrivacyDialog by viewModel.showPrivacyDialog.collectAsStateWithLifecycle()

    // Handle back button when inside game, document detail, or secondary tabs
    BackHandler(enabled = gameSessionState.activePdfTitle.isNotBlank() || selectedDoc != null || currentDestination != AppDestination.CHAT) {
        if (gameSessionState.activePdfTitle.isNotBlank()) {
            viewModel.exitPdfGame()
        } else if (selectedDoc != null) {
            viewModel.selectDocument(null)
        } else if (currentDestination != AppDestination.CHAT) {
            viewModel.setDestination(AppDestination.CHAT)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color(0xFF090D16))) {
        val isExpandedScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Adaptive Side Navigation Rail for tablets/wide screens
            if (isExpandedScreen) {
                NavigationRail(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White,
                    header = {
                        Image(
                            painter = painterResource(id = R.drawable.ai_teach_logo),
                            contentDescription = "AI TEACH Logo",
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, BrightstepPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    },
                    modifier = Modifier.width(84.dp)
                ) {
                    AppDestination.values().forEach { destination ->
                        NavigationRailItem(
                            selected = currentDestination == destination,
                            onClick = { viewModel.setDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = getDestinationIcon(destination),
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = BrightstepPrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = BrightstepPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Privacy policy button at bottom of rail
                    IconButton(
                        onClick = { viewModel.setShowPrivacyDialog(true) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = "Privacy Policy", tint = Color.Gray)
                    }
                }
            }

            // Main Content Area
            Scaffold(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                containerColor = Color(0xFF090D16),
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (!isExpandedScreen) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ai_teach_logo),
                                        contentDescription = "AI TEACH Logo",
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, BrightstepPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "AI TEACH",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = Color.White
                                        )
                                        if (userStats.isProUser) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFF59E0B)
                                            ) {
                                                Text(
                                                    text = "PRO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = when (currentDestination) {
                                            AppDestination.CHAT -> "${currentSubject.emoji} ${currentSubject.title} Session"
                                            AppDestination.LIVE_CLASS -> "🔴 लाइव क्लासरूम (Live Masterclass)"
                                            AppDestination.DOCUMENTS -> "Course Materials Library"
                                            AppDestination.PRACTICE -> "Adaptive Recall & Quiz"
                                            AppDestination.PLANS -> "Instamojo Subscriptions"
                                            AppDestination.PROGRESS -> "Daily Goals & Streaks"
                                            AppDestination.ACCOUNT -> "Student Profile Sync"
                                        },
                                        fontSize = 11.sp,
                                        color = BrightstepPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        },
                        actions = {
                            // 7-दिन का ट्रायल स्थिति बैज (Trial Countdown Badge)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (trialStatus.isCompletelyLocked) Color(0xFFEF4444).copy(alpha = 0.2f)
                                else if (userStats.isProUser) BrightstepEmerald.copy(alpha = 0.2f)
                                else if (trialStatus.daysRemaining <= 2) Color(0xFFF59E0B).copy(alpha = 0.2f)
                                else Color(0xFF10B981).copy(alpha = 0.2f),
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clickable {
                                        // त्वरित परीक्षण टॉगल (Test Toggle between locked & active)
                                        if (trialStatus.isCompletelyLocked) viewModel.resetTrial() else viewModel.simulateTrialExpiry()
                                    }
                                    .testTag("trial_countdown_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (trialStatus.isCompletelyLocked) Icons.Default.Lock
                                        else if (userStats.isProUser) Icons.Default.Verified
                                        else Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (trialStatus.isCompletelyLocked) Color(0xFFEF4444)
                                        else if (userStats.isProUser) BrightstepEmerald
                                        else if (trialStatus.daysRemaining <= 2) Color(0xFFF59E0B)
                                        else Color(0xFF10B981),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (trialStatus.isCompletelyLocked) "ट्रायल समाप्त 🔒"
                                        else if (userStats.isProUser) "प्रो सक्रिय 👑"
                                        else "ट्रायल: ${trialStatus.daysRemaining}d ${trialStatus.hoursRemaining}h",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (trialStatus.isCompletelyLocked) Color(0xFFEF4444)
                                        else if (userStats.isProUser) BrightstepEmerald
                                        else if (trialStatus.daysRemaining <= 2) Color(0xFFF59E0B)
                                        else Color(0xFF10B981)
                                    )
                                }
                            }

                            // Quick Live Class button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (currentDestination == AppDestination.LIVE_CLASS) Color(0xFFEF4444) else Color(0xFF1E293B),
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clickable { viewModel.setDestination(AppDestination.LIVE_CLASS) }
                                    .testTag("top_live_class_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LiveTv,
                                        contentDescription = "Live Class",
                                        tint = if (currentDestination == AppDestination.LIVE_CLASS) Color.White else Color(0xFFEF4444),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Live",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentDestination == AppDestination.LIVE_CLASS) Color.White else Color(0xFFEF4444)
                                    )
                                }
                            }

                            // Quick Privacy Policy icon button
                            IconButton(onClick = { viewModel.setShowPrivacyDialog(true) }) {
                                Icon(Icons.Default.Shield, contentDescription = "Privacy Policy", tint = Color.Gray)
                            }

                            // Pro upgrade button if not pro
                            if (!userStats.isProUser) {
                                Button(
                                    onClick = { viewModel.setDestination(AppDestination.PLANS) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1E293B)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFFF59E0B).copy(alpha = 0.6f), Color.Transparent)
                                        )
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.padding(end = 8.dp).testTag("top_pro_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Plans", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BrightstepEmerald.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = BrightstepEmerald, modifier = Modifier.size(14.dp))
                                        Text("Pro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrightstepEmerald)
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0B0F19)
                        )
                    )
                },
                bottomBar = {
                    // Bottom Navigation Bar for Mobile / Compact Screens
                    if (!isExpandedScreen) {
                        NavigationBar(
                            containerColor = Color(0xFF111827),
                            contentColor = Color.White,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            AppDestination.values().forEach { destination ->
                                val isSelected = currentDestination == destination
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.setDestination(destination) },
                                    icon = {
                                        Icon(
                                            imageVector = getDestinationIcon(destination),
                                            contentDescription = destination.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = BrightstepPrimary,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = BrightstepPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (gameSessionState.activePdfTitle.isNotBlank()) {
                        // 1. गेमीफाइड पीडीएफ लर्निंग स्क्रीन (Gamified Learning via PDF Quiz)
                        PdfGameScreen(
                            gameState = gameSessionState,
                            questions = activeGameQuestions,
                            onSubmitAnswer = { viewModel.submitGameAnswer(it) },
                            onRestartGame = { viewModel.restartPdfGame() },
                            onExitGame = { viewModel.exitPdfGame() }
                        )
                    } else if (trialStatus.isCompletelyLocked && currentDestination != AppDestination.PLANS) {
                        // 2. सख्त 7-दिन का ट्रायल समाप्ति ऑटो-लॉक स्क्रीन (Strict 7-Day Free Trial Auto-Lock)
                        TrialExpiredLockScreen(
                            onOpenPlans = { viewModel.setDestination(AppDestination.PLANS) },
                            onResetTrialForTesting = { viewModel.resetTrial() }
                        )
                    } else {
                        // 3. सामान्य ऐप नेविगेशन
                        when (currentDestination) {
                            AppDestination.CHAT -> {
                                ChatScreen(
                                    currentSubject = currentSubject,
                                    messages = messages,
                                    tutorStatus = tutorStatus,
                                    selectedDocument = selectedDoc,
                                    isRecordingVoice = isRecordingVoice,
                                    onSelectSubject = { viewModel.selectSubject(it) },
                                    onSendMessage = { viewModel.sendMessage(it) },
                                    onToggleAudio = { viewModel.toggleAudio() },
                                    onSelectStyle = { viewModel.setTeachingStyle(it) },
                                    onToggleVoice = { viewModel.toggleVoiceRecording() },
                                    onClearSelectedDocument = { viewModel.selectDocument(null) },
                                    onOpenDocuments = { viewModel.setDestination(AppDestination.DOCUMENTS) }
                                )
                            }

                            AppDestination.LIVE_CLASS -> {
                                LiveClassScreen(
                                    onNavigateToPlans = { viewModel.setDestination(AppDestination.PLANS) }
                                )
                            }

                            AppDestination.DOCUMENTS -> {
                                DocumentsScreen(
                                    documents = documents,
                                    currentSubject = currentSubject,
                                    selectedDocument = selectedDoc,
                                    onSelectDocument = { viewModel.selectDocument(it) },
                                    onOpenAddDocument = { viewModel.setShowAddDocDialog(true) },
                                    onAskAboutDocument = { doc ->
                                        viewModel.selectDocument(doc)
                                        viewModel.setDestination(AppDestination.CHAT)
                                        viewModel.sendMessage("Can you summarize the core principles in ${doc.title}?")
                                    },
                                    onGenerateFlashcardsFromDoc = { doc ->
                                        viewModel.selectSubject(doc.subjectType)
                                        viewModel.setDestination(AppDestination.PRACTICE)
                                    },
                                    onGenerateQuizFromDoc = { doc ->
                                        viewModel.selectSubject(doc.subjectType)
                                        viewModel.setDestination(AppDestination.PRACTICE)
                                    },
                                    onPlayPdfGame = { doc ->
                                        viewModel.startPdfGame(doc)
                                    }
                                )
                            }

                            AppDestination.PRACTICE -> {
                                PracticeScreen(
                                    currentSubject = currentSubject,
                                    flashcards = flashcards,
                                    quizzes = quizzes,
                                    quizHistory = quizHistory,
                                    onSelectSubject = { viewModel.selectSubject(it) },
                                    onUpdateFlashcardMastery = { id, status -> viewModel.updateFlashcardMastery(id, status) },
                                    onQuizCompleted = { score, total -> viewModel.onQuizCompleted(score, total) }
                                )
                            }

                            AppDestination.PLANS -> {
                                PlansScreen(
                                    plans = viewModel.subscriptionPlans,
                                    userProfile = userProfile,
                                    onBuyWithInstamojo = { plan ->
                                        viewModel.purchaseSubscriptionPlan(plan)
                                        viewModel.setShowProDialog(true)
                                    },
                                    onOpenPrivacyPolicy = { viewModel.setShowPrivacyDialog(true) }
                                )
                            }

                            AppDestination.PROGRESS -> {
                                ProgressScreen(
                                    userStats = userStats,
                                    goals = goals,
                                    onToggleGoal = { viewModel.toggleGoal(it) },
                                    onOpenProDialog = { viewModel.setDestination(AppDestination.PLANS) }
                                )
                            }

                            AppDestination.ACCOUNT -> {
                                AccountScreen(
                                    userProfile = userProfile,
                                    onRegister = { name, email ->
                                        viewModel.registerAccount(name, email)
                                    },
                                    onOpenPrivacyPolicy = { viewModel.setShowPrivacyDialog(true) },
                                    onDeleteAccount = { viewModel.deleteAccountAndData() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Document Dialog
        if (showAddDocDialog) {
            AddDocumentDialog(
                initialSubject = currentSubject,
                onDismiss = { viewModel.setShowAddDocDialog(false) },
                onAddDocument = { title, format, content, summary ->
                    viewModel.addNewDocument(title, format, content, summary)
                }
            )
        }

        // Pro Upgrade Dialog (Instamojo)
        if (showProDialog) {
            ProUpgradeDialog(
                onDismiss = { viewModel.setShowProDialog(false) },
                onUpgradeSuccess = { viewModel.upgradeToPro() }
            )
        }

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            PrivacyPolicyDialog(
                onDismiss = { viewModel.setShowPrivacyDialog(false) }
            )
        }
    }
}

private fun getDestinationIcon(destination: AppDestination): androidx.compose.ui.graphics.vector.ImageVector {
    return when (destination) {
        AppDestination.CHAT -> Icons.Default.SmartToy
        AppDestination.LIVE_CLASS -> Icons.Default.LiveTv
        AppDestination.DOCUMENTS -> Icons.Default.AutoStories
        AppDestination.PRACTICE -> Icons.Default.FactCheck
        AppDestination.PLANS -> Icons.Default.CreditCard
        AppDestination.PROGRESS -> Icons.Default.Leaderboard
        AppDestination.ACCOUNT -> Icons.Default.Person
    }
}
