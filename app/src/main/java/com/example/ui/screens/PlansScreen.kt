package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubscriptionTier
import com.example.model.UserProfile
import com.example.ui.theme.BrightstepEmerald
import com.example.ui.theme.BrightstepPrimary

@Composable
fun PlansScreen(
    plans: List<SubscriptionTier>,
    userProfile: UserProfile,
    onBuyWithInstamojo: (SubscriptionTier) -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showVerifyDialog by remember { mutableStateOf(false) }
    var paymentIdInput by remember { mutableStateOf("") }
    var selectedPlanToVerify by remember { mutableStateOf(plans.firstOrNull()) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("plans_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
        // Top Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BrightstepPrimary.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(BrightstepPrimary, Color.Transparent))
                    )
                ) {
                    Text(
                        text = "SECURE INSTAMOJO GATEWAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrightstepPrimary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "Brightstep Subscriptions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Unlock unlimited AI processing power, premium 3D visualizations, and priority support. Powered securely by Instamojo.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Active Subscription Status & Instamojo Account Banner
        item {
            val context = androidx.compose.ui.platform.LocalContext.current
            val paymentUrl = "https://imjo.in/WhRtvz"

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF3B82F6).copy(alpha = 0.5f), Color(0xFF10B981).copy(alpha = 0.5f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Subscription Plan", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = userProfile.activePlan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userProfile.activePlan.contains("Pro") || userProfile.activePlan.contains("Series")) BrightstepEmerald else Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrightstepEmerald.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(BrightstepEmerald)
                                )
                                Text(
                                    text = "VERIFIED ACCOUNT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrightstepEmerald
                                )
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.08f))

                    // Instamojo Account link box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                listOf(BrightstepPrimary.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = BrightstepPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Instamojo Payment Account",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = paymentUrl,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.SemiBold
                            )

                            Button(
                                onClick = {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(paymentUrl)
                                    )
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .testTag("open_instamojo_url_button")
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Instamojo Account Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showVerifyDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightstepEmerald),
                                border = ButtonDefaults.outlinedButtonBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(BrightstepEmerald, BrightstepPrimary))
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("verify_payment_button")
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrightstepEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("भुगतान आईडी दर्ज करें (Verify Payment / Enter ID)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // The 3 Subscription Plan Cards (Keyed for fast list rendering)
        items(plans, key = { it.id }) { plan ->
            val isCurrentPlan = userProfile.activePlan == plan.title

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plan_card_${plan.id}"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (plan.isPopular) Color(0xFF131D38) else Color(0xFF111827)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = if (plan.isPopular) {
                        Brush.verticalGradient(
                            listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                        )
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (plan.isPopular) 8.dp else 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Best Value badge on top
                    if (plan.isPopular) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 12.dp, end = 12.dp),
                            shape = CircleShape,
                            color = BrightstepPrimary
                        ) {
                            Text(
                                text = "BEST VALUE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column {
                            Text(
                                text = plan.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (plan.isPopular) BrightstepPrimary else Color(0xFFE2E8F0)
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = plan.priceDisplay,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = plan.periodDisplay,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.08f))

                        // Features List
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            plan.features.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(plan.accentColor),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = feature,
                                        fontSize = 13.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }

                        // Buy with Instamojo Button
                        Button(
                            onClick = { onBuyWithInstamojo(plan) },
                            enabled = !isCurrentPlan,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (plan.isPopular) BrightstepPrimary else Color(0xFF1E293B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .then(
                                    if (plan.isPopular) {
                                        Modifier.shadow(6.dp, RoundedCornerShape(14.dp), spotColor = BrightstepPrimary)
                                    } else Modifier
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isCurrentPlan) "Active Plan" else plan.buttonText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                if (!isCurrentPlan) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Privacy Policy Link Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TextButton(onClick = onOpenPrivacyPolicy) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Read Instamojo & AI Privacy Policy",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = "Encrypted TLS 1.3 Transaction via Instamojo Gateway",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray
                )
            }
        }
    }

    if (showVerifyDialog) {
        AlertDialog(
            onDismissRequest = { showVerifyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = BrightstepEmerald,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Instamojo भुगतान सत्यापन",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "यदि आपने https://imjo.in/WhRtvz पर भुगतान कर दिया है, तो कृपया अपनी Payment ID या Transaction Reference दर्ज करें:",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 17.sp
                    )

                    OutlinedTextField(
                        value = paymentIdInput,
                        onValueChange = { paymentIdInput = it },
                        label = { Text("Payment ID / Ref (e.g. MOJO1234...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrightstepPrimary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )

                    Text(
                        text = "खरीदा गया प्लान चुनें (Select Plan):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    plans.forEach { plan ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedPlanToVerify?.id == plan.id) BrightstepPrimary.copy(alpha = 0.25f) else Color(0xFF0F172A),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = if (selectedPlanToVerify?.id == plan.id) Brush.horizontalGradient(listOf(BrightstepPrimary, BrightstepEmerald)) else Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlanToVerify = plan }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(plan.title, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(plan.priceDisplay, fontSize = 13.sp, color = BrightstepEmerald, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val plan = selectedPlanToVerify ?: plans.first()
                        showVerifyDialog = false
                        onBuyWithInstamojo(plan)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightstepEmerald)
                ) {
                    Text("सत्यापित कर सक्रिय करें (Activate)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerifyDialog = false }) {
                    Text("रद्द करें", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp)
        )
    }
}
}
