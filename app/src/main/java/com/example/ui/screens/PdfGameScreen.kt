package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameQuestion
import com.example.model.GameSessionState
import com.example.ui.theme.*

/**
 * पीडीएफ आधारित गेमीफाइड लर्निंग स्क्रीन (Gamified Learning via PDF Screen)
 * छात्र की पीडीएफ से प्रश्न बनाकर 3D ट्यूटर के साथ गेम खेला जाता है।
 */
@Composable
fun PdfGameScreen(
    gameState: GameSessionState,
    questions: List<GameQuestion>,
    onSubmitAnswer: (Int) -> Unit,
    onRestartGame: () -> Unit,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (questions.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "कोई गेम प्रश्न उपलब्ध नहीं है।",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onExitGame) {
                    Text("लाइब्रेरी पर वापस जाएं")
                }
            }
        }
        return
    }

    val currentQuestion = questions.getOrNull(gameState.currentQuestionIndex) ?: questions.last()

    // 3D अवतार का फ्लोटिंग एनीमेशन
    val infiniteTransition = rememberInfiniteTransition(label = "tutor_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(16.dp)
            .testTag("pdf_game_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // शीर्ष बार: वापस जाएं, स्कोर, लाइव्स (दिल)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onExitGame,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF1E293B), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
            }

            // स्कोर और कॉम्बो पिल
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(BrightstepAmber, Color.Transparent))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = BrightstepAmber, modifier = Modifier.size(16.dp))
                        Text(
                            text = "${gameState.score} अंक",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = BrightstepAmber
                        )
                    }
                }

                if (gameState.comboMultiplier > 1) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDC2626)
                    ) {
                        Text(
                            text = "${gameState.comboMultiplier}x कॉम्बो! 🔥",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // लाइफ दिल (Hearts remaining)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(3) { index ->
                    Text(
                        text = if (index < gameState.livesRemaining) "❤️" else "🖤",
                        fontSize = 16.sp
                    )
                }
            }
        }

        // गेम ओवर या विक्ट्री कार्ड
        if (gameState.isGameOver || gameState.isVictory) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (gameState.isVictory) Color(0xFF064E3B) else Color(0xFF450A0A)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(
                            if (gameState.isVictory) BrightstepEmerald else Color(0xFFEF4444),
                            Color.Transparent
                        )
                    )
                )
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
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (gameState.isVictory) "👑" else "💀",
                            fontSize = 32.sp
                        )
                    }

                    Text(
                        text = if (gameState.isVictory) "अद्भुत जीत! आपने गेम जीत लिया!" else "गेम ओवर! दिल ख़त्म हो गए!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = gameState.tutorReaction,
                        fontSize = 14.sp,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "अंतिम स्कोर: ${gameState.score} अंक",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrightstepAmber
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onRestartGame,
                            colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("दोबारा खेलें", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onExitGame,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("बाहर जाएं", color = Color.White)
                        }
                    }
                }
            }
        } else {
            // 3D रोबोट अवतार और डायलॉग बबल
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // डायलॉग बबल
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(BrightstepPrimary, BrightstepAccent))
                    ),
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🤖 ट्यूटर:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightstepPrimary
                        )
                        Text(
                            text = gameState.tutorReaction,
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                    }
                }

                // 3D रोबोट अवतार विथ फ्लोटिंग (graphicsLayer द्वारा त्वरित एवं बिना लैग के)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer { translationY = floatOffset * density }
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(BrightstepPrimary.copy(alpha = 0.35f), Color.Transparent))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.robot_tutor_icon),
                        contentDescription = "3D Robot Tutor",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // प्रश्न और ऑप्शन्स कार्ड
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "प्रश्न ${gameState.currentQuestionIndex + 1} / ${questions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrightstepPrimary
                        )
                        Text(
                            text = "+${currentQuestion.pointValue * gameState.comboMultiplier} अंक",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightstepAmber
                        )
                    }

                    Text(
                        text = currentQuestion.question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    // 4 ऑप्शन्स
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        currentQuestion.options.forEachIndexed { index, option ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .clickable { onSubmitAnswer(index) }
                                    .testTag("game_option_$index"),
                                color = Color(0xFF1E293B)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(BrightstepPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${('A'.code + index).toChar()}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrightstepPrimary
                                        )
                                    }
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // बॉटम प्रोग्रेस
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "स्रोत: ${gameState.activePdfTitle} • पीडीएफ आधारित इंटरएक्टिव सत्र",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
