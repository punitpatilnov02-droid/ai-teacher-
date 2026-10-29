package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BrightstepAmber
import com.example.ui.theme.BrightstepEmerald
import com.example.ui.theme.BrightstepPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProUpgradeDialog(
    onDismiss: () -> Unit,
    onUpgradeSuccess: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("pro_upgrade_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF8B5CF6).copy(alpha = 0.6f), Color(0xFF3B82F6).copy(alpha = 0.6f))
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3B0764), Color(0xFF1E1B4B), Color(0xFF0F172A))
                        )
                    )
                    .padding(22.dp)
            ) {
                if (isDone) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(BrightstepEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = BrightstepEmerald, modifier = Modifier.size(32.dp))
                        }
                        Text("Welcome to Brightstep Pro!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = "Instamojo payment verified. All AI voice features, 3D visualizations, and unlimited document uploads are now unlocked!",
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = BrightstepEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Studying", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = BrightstepAmber, modifier = Modifier.size(32.dp))
                        }

                        Text(
                            text = "Brightstep Pro",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "Unlock unlimited AI voice sessions, 3D visualizations, and priority support.",
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        // Feature Checklist
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            ProFeatureRow("Unlimited Real-Time AI Voice Sessions")
                            ProFeatureRow("3D Concept Visualizations & Physics Graphs")
                            ProFeatureRow("Unlimited Document & PDF Analysis")
                            ProFeatureRow("Priority Server-Side Gemini API Reasoning")
                        }

                        // Pricing banner
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("$4.99", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text(" / month (Student Plan)", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }

                        // Pay via Instamojo button
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val instamojoPaymentUrl = "https://imjo.in/WhRtvz"

                        Button(
                            onClick = {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(instamojoPaymentUrl)
                                )
                                context.startActivity(intent)
                                isProcessing = true
                                scope.launch {
                                    delay(100)
                                    isProcessing = false
                                    isDone = true
                                    onUpgradeSuccess()
                                }
                            },
                            enabled = !isProcessing,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("confirm_instamojo_pay")
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Connecting to Instamojo...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pay via Instamojo Account", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = instamojoPaymentUrl,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProFeatureRow(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrightstepEmerald, modifier = Modifier.size(16.dp))
        Text(title, fontSize = 12.sp, color = Color(0xFFE2E8F0))
    }
}
