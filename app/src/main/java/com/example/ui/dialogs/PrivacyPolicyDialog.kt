package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BrightstepPrimary

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("privacy_policy_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BrightstepPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Privacy Policy",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.08f))

                Text(
                    text = "Effective Date: October 2026",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrightstepPrimary
                )

                Text(
                    text = "Welcome to Brightstep Study Companion. This Privacy Policy describes how we collect, use, and handle your information when you use our Android application and AI tutor services.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFFCBD5E1)
                )

                // Section 1
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "1. Information We Collect",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "We collect information you provide directly to us when creating an account (Name, Email) and interactions with the AI Tutor for the purpose of improving session context and active recall accuracy.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Section 2
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "2. Payments & Instamojo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "All Plan Purchases, including Monthly Series (₹999), Yearly Series (₹9,999), and USA/International tiers ($149), are securely processed by Instamojo. We do not store your full payment card credentials on our servers.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Section 3
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "3. AI Data Processing",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Your study notes and chat interactions are processed securely to provide real-time educational step breakdowns. Data may be routed through our USA and global servers depending on your active plan tier.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Section 4 - Google Play Data Safety Compliance
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "4. User Data Deletion Rights",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "In full compliance with Google Play Store Data Safety policies, users can request or execute immediate deletion of their account profile, study notes, and quiz histories at any time using the 'Delete Account & Data' button in the Account tab or by contacting developer support.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Section 5 - Developer Contact
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "5. Developer & Grievance Contact",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "For any subscription inquiries, Instamojo invoice verification, or privacy concerns, reach out directly to:\nEmail: dp1754876@gmail.com\nInstamojo: https://imjo.in/WhRtvz",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = BrightstepPrimary
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
