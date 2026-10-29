package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.SubjectSelectorRow
import com.example.ui.components.TutorAvatarCard
import com.example.ui.theme.BrightstepAccent
import com.example.ui.theme.BrightstepPrimary
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    currentSubject: SubjectType,
    messages: List<ChatMessage>,
    tutorStatus: TutorStatus,
    selectedDocument: StudyDocument?,
    isRecordingVoice: Boolean,
    onSelectSubject: (SubjectType) -> Unit,
    onSendMessage: (String) -> Unit,
    onToggleAudio: () -> Unit,
    onSelectStyle: (TeachingStyle) -> Unit,
    onToggleVoice: () -> Unit,
    onClearSelectedDocument: () -> Unit,
    onOpenDocuments: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_screen")
    ) {
        // Sticky Header: Tutor Avatar Card & Subject Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TutorAvatarCard(
                status = tutorStatus,
                onToggleAudio = onToggleAudio,
                onSelectStyle = onSelectStyle
            )

            SubjectSelectorRow(
                selectedSubject = currentSubject,
                onSelectSubject = onSelectSubject
            )
        }

        // Active Attached Document indicator if selected
        if (selectedDocument != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(BrightstepPrimary.copy(alpha = 0.5f), BrightstepAccent.copy(alpha = 0.5f))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BrightstepPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Analyzing: ${selectedDocument.title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                    IconButton(
                        onClick = onClearSelectedDocument,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Detach Document",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    onQuickReply = { reply ->
                        onSendMessage(reply)
                    }
                )
            }

            if (tutorStatus.isThinking) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Bottom Chat Input Bar
        ChatInputBar(
            inputText = inputText,
            onInputChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    onSendMessage(inputText)
                    inputText = ""
                }
            },
            isRecordingVoice = isRecordingVoice,
            onToggleVoice = onToggleVoice,
            onOpenDocuments = onOpenDocuments
        )
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onQuickReply: (String) -> Unit
) {
    val isUser = message.sender == Sender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.88f else 0.95f),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) {
                // AI Tutor Avatar Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BrightstepPrimary, BrightstepAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Tutor",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f, fill = false),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    ),
                    color = if (isUser) BrightstepPrimary else Color(0xFF1E293B),
                    border = if (!isUser) {
                        CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
                    } else null,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (message.attachedDocTitle != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Attachment, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                                Text(
                                    text = message.attachedDocTitle,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = if (isUser) Color.White else Color(0xFFF1F5F9)
                        )

                        // Formula or Code Box
                        if (message.formulaOrCode != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(
                                        listOf(BrightstepPrimary.copy(alpha = 0.4f), Color.Transparent)
                                    )
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = message.formulaOrCode,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Functions,
                                        contentDescription = "Formula",
                                        tint = Color(0xFF38BDF8).copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Structured Steps Breakdown
                        if (message.steps.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                message.steps.forEach { step ->
                                    StepItemRow(step)
                                }
                            }
                        }
                    }
                }

                // Quick Replies Chips
                if (message.quickReplies.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        items(message.quickReplies, key = { it }) { reply ->
                            SuggestionChip(
                                onClick = { onQuickReply(reply) },
                                label = {
                                    Text(
                                        text = reply,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF93C5FD)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = Color.White.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StepItemRow(step: StepBreakdown) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.6f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(BrightstepAccent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.stepNumber}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrightstepAccent
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2E8F0)
                )
                Text(
                    text = step.explanation,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF94A3B8)
                )
                if (step.formulaOrSnippet != null) {
                    Text(
                        text = step.formulaOrSnippet,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dots"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 40.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E293B),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "AI Tutor is writing steps",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .graphicsLayer { alpha = dotAlpha }
                            .background(BrightstepPrimary)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isRecordingVoice: Boolean,
    onToggleVoice: () -> Unit,
    onOpenDocuments: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0B0F19),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Attach document button
            IconButton(
                onClick = onOpenDocuments,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E293B), CircleShape)
                    .testTag("attach_doc_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach Document",
                    tint = BrightstepPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Input field
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = {
                    Text(
                        text = if (isRecordingVoice) "Listening to voice question..." else "Ask your AI tutor a question...",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_text_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrightstepPrimary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color(0xFF111827),
                    unfocusedContainerColor = Color(0xFF111827),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 3
            )

            // Voice input button with animated pulse
            IconButton(
                onClick = onToggleVoice,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isRecordingVoice) Color(0xFFEF4444).copy(alpha = 0.25f) else Color(0xFF1E293B),
                        CircleShape
                    )
                    .testTag("voice_input_button")
            ) {
                Icon(
                    imageVector = if (isRecordingVoice) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isRecordingVoice) Color(0xFFEF4444) else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Send Button
            IconButton(
                onClick = onSend,
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (inputText.isNotBlank()) BrightstepPrimary else Color(0xFF1E293B),
                        CircleShape
                    )
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = if (inputText.isNotBlank()) Color.White else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
