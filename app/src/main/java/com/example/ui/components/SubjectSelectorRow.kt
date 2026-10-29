package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubjectType
import com.example.ui.theme.BrightstepPrimary

@Composable
fun SubjectSelectorRow(
    selectedSubject: SubjectType,
    onSelectSubject: (SubjectType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("subject_selector_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(SubjectType.values(), key = { it.name }) { subject ->
            val isSelected = subject == selectedSubject

            val backgroundBrush = if (isSelected) {
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3B82F6),
                        Color(0xFF8B5CF6)
                    )
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1E293B).copy(alpha = 0.8f),
                        Color(0xFF0F172A).copy(alpha = 0.8f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundBrush)
                    .then(
                        if (isSelected) {
                            Modifier
                                .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = BrightstepPrimary)
                                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        } else {
                            Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        }
                    )
                    .clickable { onSelectSubject(subject) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
                    .testTag("subject_chip_${subject.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = subject.emoji, fontSize = 16.sp)
                    Text(
                        text = subject.title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
