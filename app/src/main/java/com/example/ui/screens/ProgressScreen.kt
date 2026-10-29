package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudyGoal
import com.example.model.UserStats
import com.example.ui.theme.*

@Composable
fun ProgressScreen(
    userStats: UserStats,
    goals: List<StudyGoal>,
    onToggleGoal: (String) -> Unit,
    onOpenProDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("progress_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Study Progress & Goals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Keep up your momentum with daily milestones",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Room SQLite Database Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(BrightstepEmerald.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrightstepEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Room Database",
                            tint = BrightstepEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Room Database Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrightstepEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "SQLite Local",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrightstepEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Study materials, custom notes, and quiz history are saved locally on this device.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Stats Overview 4-Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Streak",
                    value = "${userStats.streakDays} Days",
                    icon = Icons.Default.LocalFireDepartment,
                    color = BrightstepAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Time",
                    value = "${userStats.totalMinutesStudied}m",
                    icon = Icons.Default.Timer,
                    color = BrightstepPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Solved",
                    value = "${userStats.questionsAnswered} Qs",
                    icon = Icons.Default.Quiz,
                    color = BrightstepEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Rank",
                    value = "Lvl ${userStats.level}",
                    icon = Icons.Default.WorkspacePremium,
                    color = BrightstepAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Brightstep Pro Banner (matching mockup with purple/indigo gradient & Instamojo button)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pro_upgrade_banner"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF8B5CF6).copy(alpha = 0.5f), Color(0xFF3B82F6).copy(alpha = 0.5f))
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF3B0764),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF172554)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = if (userStats.isProUser) "Brightstep Pro Active" else "Brightstep Pro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = if (userStats.isProUser)
                                "You have unlimited AI tutor voice sessions, 3D visualizations, and zero document page limits."
                            else
                                "Unlock unlimited AI voice sessions, 3D visualizations, and priority support.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        if (!userStats.isProUser) {
                            Button(
                                onClick = onOpenProDialog,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFD97706))
                                    .testTag("pay_via_instamojo_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.Black)
                                    Text(
                                        text = "Pay via Instamojo",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Text(
                                text = "Secure API Integration Ready • Student Discount Available",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrightstepEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "★ Unlimited Pro Plan Enabled",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrightstepEmerald,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Goals Section (matching the mockup)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TODAY'S GOALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${goals.count { it.isCompleted }} of ${goals.size} done",
                            fontSize = 11.sp,
                            color = BrightstepPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    goals.forEach { goal ->
                        GoalItemRow(
                            goal = goal,
                            onToggle = { onToggleGoal(goal.id) }
                        )
                    }
                }
            }
        }

        // Subject Mastery Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Subject Mastery Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    MasteryProgressItem("Mathematics (Calculus)", 0.75f, BrightstepPrimary)
                    MasteryProgressItem("Physics (Mechanics)", 0.60f, BrightstepAccent)
                    MasteryProgressItem("Chemistry (Reactions)", 0.45f, BrightstepEmerald)
                    MasteryProgressItem("Programming (Algorithms)", 0.85f, Color(0xFF06B6D4))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent))
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, fontSize = 11.sp, color = Color(0xFF94A3B8))
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun GoalItemRow(
    goal: StudyGoal,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Checkbox(
            checked = goal.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = BrightstepPrimary,
                uncheckedColor = Color(0xFF475569),
                checkmarkColor = Color.White
            )
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = goal.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (goal.isCompleted) Color(0xFF64748B) else Color(0xFFE2E8F0),
                textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else null
            )
            Text(
                text = "${goal.subjectType.emoji} +${goal.xpReward} XP",
                fontSize = 11.sp,
                color = if (goal.isCompleted) Color(0xFF475569) else BrightstepPrimary
            )
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (goal.isCompleted) BrightstepEmerald.copy(alpha = 0.15f) else Color(0xFF1E293B)
        ) {
            Text(
                text = if (goal.isCompleted) "Done" else goal.dueTag,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (goal.isCompleted) BrightstepEmerald else Color.Gray,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun MasteryProgressItem(
    label: String,
    progress: Float,
    barColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
            Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = Color(0xFF1E293B)
        )
    }
}
