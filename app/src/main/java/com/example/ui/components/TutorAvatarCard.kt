package com.example.ui.components

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.TeachingStyle
import com.example.model.TutorStatus
import com.example.ui.theme.BrightstepEmerald
import com.example.ui.theme.BrightstepPrimary
import com.example.ui.theme.BrightstepAccent

@Composable
fun TutorAvatarCard(
    status: TutorStatus,
    onToggleAudio: () -> Unit,
    onSelectStyle: (TeachingStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showStyleMenu by remember { mutableStateOf(false) }

    // Floating idle animation optimized with graphicsLayer to avoid recomposition
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tutor_avatar_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827).copy(alpha = 0.92f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color(0xFF3B82F6).copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 320.dp else 220.dp)
        ) {
            // Robot Image with content crop (optimized via graphicsLayer translation)
            Image(
                painter = painterResource(id = R.drawable.robot_tutor_hero),
                contentDescription = "Brightstep 3D Robot AI Tutor",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = floatOffset * density }
                    .clip(RoundedCornerShape(24.dp))
            )

            // Subtle Cyber Scrim Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x770B0F19),
                                Color.Transparent,
                                Color(0xEE0B0F19)
                            )
                        )
                    )
            )

            // Top-Left: "AI TUTOR ONLINE" glowing pill
            Surface(
                modifier = Modifier
                    .padding(top = 12.dp, start = 12.dp)
                    .align(Alignment.TopStart),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.55f),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BrightstepEmerald.copy(alpha = 0.6f), Color.White.copy(alpha = 0.2f))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .shadow(4.dp, CircleShape, spotColor = BrightstepEmerald)
                            .background(BrightstepEmerald, CircleShape)
                    )
                    Text(
                        text = if (status.isThinking) "AI TUTOR THINKING..." else "AI TUTOR ONLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (status.isThinking) Color(0xFFF59E0B) else Color(0xFFE5E7EB),
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Top-Right: Quick Controls (Sound, Fullscreen, Style)
            Row(
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onToggleAudio,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (status.audioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Audio",
                        tint = if (status.audioEnabled) BrightstepPrimary else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { showStyleMenu = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Teaching Persona Style",
                        tint = BrightstepAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Expand View",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showStyleMenu,
                    onDismissRequest = { showStyleMenu = false },
                    containerColor = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "Teaching Persona Style",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    TeachingStyle.values().forEach { style ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = style.label,
                                        fontWeight = if (status.style == style) FontWeight.Bold else FontWeight.Normal,
                                        color = if (status.style == style) BrightstepPrimary else Color.White
                                    )
                                    Text(
                                        text = style.description,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                onSelectStyle(style)
                                showStyleMenu = false
                            },
                            leadingIcon = {
                                if (status.style == style) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrightstepPrimary)
                                }
                            }
                        )
                    }
                }
            }

            // Bottom Overlay: Status Mood & Prompt Guidance
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "Style: ${status.style.label}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrightstepPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = status.currentMood,
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "Touch avatar to interact",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
