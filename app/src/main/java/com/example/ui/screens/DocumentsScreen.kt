package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudyDocument
import com.example.model.SubjectType
import com.example.ui.theme.*

@Composable
fun DocumentsScreen(
    documents: List<StudyDocument>,
    currentSubject: SubjectType,
    selectedDocument: StudyDocument?,
    onSelectDocument: (StudyDocument?) -> Unit,
    onOpenAddDocument: () -> Unit,
    onAskAboutDocument: (StudyDocument) -> Unit,
    onGenerateFlashcardsFromDoc: (StudyDocument) -> Unit,
    onGenerateQuizFromDoc: (StudyDocument) -> Unit,
    onPlayPdfGame: (StudyDocument) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterSubject by remember { mutableStateOf<SubjectType?>(null) }

    val filteredDocs = remember(documents, searchQuery, filterSubject) {
        documents.filter { doc ->
            (filterSubject == null || doc.subjectType == filterSubject) &&
                    (searchQuery.isBlank() || doc.title.contains(searchQuery, ignoreCase = true) || doc.subtitle.contains(searchQuery, ignoreCase = true))
        }
    }

    if (selectedDocument != null) {
        DocumentDetailView(
            document = selectedDocument,
            onBack = { onSelectDocument(null) },
            onAskAboutDocument = { onAskAboutDocument(selectedDocument) },
            onGenerateFlashcards = { onGenerateFlashcardsFromDoc(selectedDocument) },
            onGenerateQuiz = { onGenerateQuizFromDoc(selectedDocument) },
            onPlayPdfGame = { onPlayPdfGame(selectedDocument) }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("documents_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Title and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Course Materials & Docs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "AI-powered document analysis and key extraction",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = onOpenAddDocument,
                    colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_doc_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search textbook chapters, slides, notes...", fontSize = 13.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrightstepPrimary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                    focusedContainerColor = Color(0xFF111827),
                    unfocusedContainerColor = Color(0xFF111827),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            // Subject Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterSubject == null,
                        onClick = { filterSubject = null },
                        label = { Text("All Subjects", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrightstepPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterSubject == null,
                            borderColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
                items(SubjectType.values(), key = { it.name }) { subject ->
                    FilterChip(
                        selected = filterSubject == subject,
                        onClick = { filterSubject = if (filterSubject == subject) null else subject },
                        label = { Text("${subject.emoji} ${subject.shortName}", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrightstepPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterSubject == subject,
                            borderColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
            }

            // Documents List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredDocs, key = { it.id }) { doc ->
                    DocumentCardItem(
                        document = doc,
                        onClick = { onSelectDocument(doc) }
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentCardItem(
    document: StudyDocument,
    onClick: () -> Unit
) {
    val (formatColor, formatIcon) = when (document.format) {
        "PDF" -> Color(0xFF3B82F6) to Icons.Default.PictureAsPdf
        "Slides" -> Color(0xFF10B981) to Icons.Default.Slideshow
        "Lecture" -> Color(0xFF8B5CF6) to Icons.Default.VideoLibrary
        else -> Color(0xFFF59E0B) to Icons.Default.Description
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("doc_card_${document.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Format Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(formatColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = formatIcon,
                    contentDescription = document.format,
                    tint = formatColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${document.subjectType.emoji} ${document.title}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF3F4F6)
                    )
                }
                Text(
                    text = document.subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${document.pageCount} pgs",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = document.uploadDate,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DocumentDetailView(
    document: StudyDocument,
    onBack: () -> Unit,
    onAskAboutDocument: () -> Unit,
    onGenerateFlashcards: () -> Unit,
    onGenerateQuiz: () -> Unit,
    onPlayPdfGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
            .testTag("document_detail_view")
    ) {
        // Top Back Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF1E293B), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrightstepPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = document.format,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrightstepPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "${document.pageCount} Pages",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "${document.subjectType.emoji} ${document.title}",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = document.subtitle,
            fontSize = 13.sp,
            color = BrightstepPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // गेमीफाइड लर्निंग हीरो बटन (Gamified Learning via PDF)
        Button(
            onClick = onPlayPdfGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("play_pdf_game_button")
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🎮 पीडीएफ से गेम खेलें (Play AI Quiz Battle)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick AI Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAskAboutDocument,
                colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ask AI Tutor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onGenerateFlashcards,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(BrightstepAccent, Color.White.copy(alpha = 0.2f)))
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Style, contentDescription = null, tint = BrightstepAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Flashcards", fontSize = 11.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Executive Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightstepAmber, modifier = Modifier.size(18.dp))
                            Text("Executive Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text(
                            text = document.summary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // Key Takeaways
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Core Takeaways", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        document.keyTakeaways.forEachIndexed { index, takeaway ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("${index + 1}.", fontWeight = FontWeight.Bold, color = BrightstepPrimary, fontSize = 13.sp)
                                Text(takeaway, color = Color(0xFFE2E8F0), fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            // Equations if present
            if (document.formulaOrEquations.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Key Formulas & Laws", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            document.formulaOrEquations.forEach { formula ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = formula,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = Color(0xFF7DD3FC),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Full Content Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Document Excerpt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = document.content,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
