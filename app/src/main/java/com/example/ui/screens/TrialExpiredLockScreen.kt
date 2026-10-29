package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TutorRepository
import com.example.ui.theme.BrightstepAmber
import com.example.ui.theme.BrightstepPrimary

/**
 * सख्त 7-दिन ट्रायल समाप्ति ऑटो-लॉक स्क्रीन (Strict 7-Day Trial Expiry Lock Screen)
 * जब 7 दिन समाप्त हो जाते हैं, तो ऐप पूरी तरह से लॉक हो जाता है और केवल प्लान खरीदने का विकल्प रहता है।
 */
@Composable
fun TrialExpiredLockScreen(
    onOpenPlans: () -> Unit,
    onResetTrialForTesting: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val instamojoUrl = TutorRepository.INSTAMOJO_PAYMENT_URL

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(20.dp)
            .testTag("trial_expired_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFEF4444).copy(alpha = 0.8f), Color(0xFF8B5CF6).copy(alpha = 0.5f))
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // लॉक आइकन विथ रेड पल्स
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFFEF4444), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "App Locked",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "ट्रायल समाप्त (TRIAL EXPIRED)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFCA5A5),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Text(
                    text = "आपका 7-दिन का मुफ़्त ट्रायल समाप्त हो चुका है!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )

                Text(
                    text = "सभी एआई चैट, 3D ट्यूटर, पीडीएफ गेम और क्विज़ स्वतः लॉक कर दिए गए हैं। आगे अध्ययन जारी रखने के लिए कृपया एक सदस्यता प्लान खरीदें।",
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                // इंस्टामोजो खाता लिंक बॉक्स
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(BrightstepPrimary, Color.Transparent))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "आधिकारिक भुगतान लिंक:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = instamojoUrl,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // मुख्य खरीद बटन - Instamojo
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instamojoUrl))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = BrightstepAmber)
                        .testTag("buy_instamojo_lock_screen_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.Black)
                        Text(
                            text = "Instamojo से प्लान खरीदें (Buy Plan)",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // ऐप में प्लान विवरण देखें
                OutlinedButton(
                    onClick = onOpenPlans,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "उपलब्ध प्लान्स देखें (View Plans & Features)",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))

                // डेवलपर / टेस्टिंग रीसेट विकल्प
                TextButton(
                    onClick = onResetTrialForTesting,
                    modifier = Modifier.testTag("reset_trial_testing_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "परीक्षण के लिए 7-दिन का ट्रायल रीसेट करें (Reset for Test)",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
