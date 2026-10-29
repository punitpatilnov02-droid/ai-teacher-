package com.example.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.annotation.OptIn
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.R
import com.example.ui.theme.BrightstepAccent
import com.example.ui.theme.BrightstepEmerald
import com.example.ui.theme.BrightstepPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Live Lecture Model (लाइव लेक्चर डेटा मॉडल)
 */
data class LiveLecture(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val tutorName: String,
    val subject: String,
    val viewerCount: Int,
    val streamUrl: String,
    val isLiveNow: Boolean = true
)

val SampleLiveLectures = listOf(
    LiveLecture(
        id = "stream_1",
        titleHindi = "गणित और कलन: सीमाएं और अवकलन",
        titleEnglish = "Calculus: Limits, Derivatives & Real-World Modeling",
        tutorName = "AI डॉ. सारा शर्मा (Mathematics)",
        subject = "Mathematics",
        viewerCount = 342,
        streamUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    ),
    LiveLecture(
        id = "stream_2",
        titleHindi = "भौतिकी: क्वांटम यांत्रिकी और तरंग सिद्धांत",
        titleEnglish = "Quantum Physics: Wave-Particle Duality & Superposition",
        tutorName = "प्रो. आर्यन वर्मा (Physics)",
        subject = "Physics",
        viewerCount = 512,
        streamUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    ),
    LiveLecture(
        id = "stream_3",
        titleHindi = "आर्टिफिशियल इंटेलिजेंस और न्यूरल नेटवर्क",
        titleEnglish = "Neural Networks: Backpropagation & Transformers",
        tutorName = "AI ट्यूटर प्रो (Computer Science)",
        subject = "Computer Science",
        viewerCount = 684,
        streamUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    ),
    LiveLecture(
        id = "stream_4",
        titleHindi = "जैव अणु और आनुवंशिकी मास्टरक्लास",
        titleEnglish = "Molecular Biology: DNA Transcription & Protein Folding",
        tutorName = "डॉ. अनन्या राय (Biology)",
        subject = "Biology",
        viewerCount = 289,
        streamUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    )
)

data class LiveChatComment(
    val id: String,
    val author: String,
    val text: String,
    val isTutor: Boolean = false,
    val timeAgo: String = "अभी (Just now)"
)

/**
 * LiveClassScreen: Integrates the androidx.media3.ui.PlayerView layout with
 * btnStartStream and progressBar, providing an interactive live masterclass learning experience.
 */
@OptIn(UnstableApi::class)
@Composable
fun LiveClassScreen(
    onNavigateToPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var selectedLecture by remember { mutableStateOf(SampleLiveLectures[0]) }
    var chatMessage by remember { mutableStateOf("") }
    val liveChat = remember {
        mutableStateListOf(
            LiveChatComment("1", "राहुल (Rahul)", "नमस्ते मैम, क्या आप अवकलन का चेन नियम दोबारा समझा सकती हैं?", false, "2 मिनट पहले"),
            LiveChatComment("2", "AI ट्यूटर (Tutor)", "ज़रूर राहुल! चेन नियम f(g(x))' = f'(g(x)) * g'(x) होता है।", true, "1 मिनट पहले"),
            LiveChatComment("3", "प्रिया (Priya)", "The interactive graph on the screen is super clear!", false, "30 सेकंड पहले")
        )
    }

    // Build single instance of ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    // References to the inflated views from live_class_stream.xml
    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }
    var btnStartStreamRef by remember { mutableStateOf<Button?>(null) }
    var progressBarRef by remember { mutableStateOf<ProgressBar?>(null) }
    var isStreamActive by remember { mutableStateOf(false) }

    // Helper function to load and start streaming a lecture
    fun startStreamingLecture(lecture: LiveLecture) {
        selectedLecture = lecture
        val mediaItem = MediaItem.fromUri(lecture.streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        isStreamActive = true
    }

    // Keep views in sync when player state changes
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        progressBarRef?.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        progressBarRef?.visibility = View.GONE
                        btnStartStreamRef?.text = if (exoPlayer.isPlaying) "Pause Live Class" else "Resume Live Class"
                    }
                    Player.STATE_ENDED -> {
                        progressBarRef?.visibility = View.GONE
                        btnStartStreamRef?.text = "Restart Live Class"
                    }
                    Player.STATE_IDLE -> {
                        progressBarRef?.visibility = View.GONE
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    progressBarRef?.visibility = View.GONE
                    btnStartStreamRef?.text = "Pause Live Class"
                    isStreamActive = true
                } else {
                    btnStartStreamRef?.text = if (exoPlayer.mediaItemCount > 0) "Resume Live Class" else "Start Live Class"
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Handle Android lifecycle (pause on background, resume on foreground)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Ready to resume when active
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Banner / Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E1B4B),
                                Color(0xFF0F172A),
                                Color(0xFF090D16)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                            Text(
                                text = "लाइव क्लासरूम (Live Classroom)",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "AI ट्यूटर मास्टरक्लास और इंटरएक्टिव वीडियो लेक्चर",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Sensors,
                                contentDescription = "Live",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${selectedLecture.viewerCount} लाइव",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Exact PlayerView + Button + ProgressBar layout container from XML
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2937))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Inflate the exact XML requested: live_class_stream.xml
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .testTag("live_class_player_view"),
                        factory = { ctx ->
                            val view = LayoutInflater.from(ctx).inflate(R.layout.live_class_stream, null, false)
                            val pView = view.findViewById<PlayerView>(R.id.playerView)
                            val btnStart = view.findViewById<Button>(R.id.btnStartStream)
                            val pBar = view.findViewById<ProgressBar>(R.id.progressBar)

                            // Attach ExoPlayer to PlayerView
                            pView.player = exoPlayer

                            // Store references for state updates
                            playerViewRef = pView
                            btnStartStreamRef = btnStart
                            progressBarRef = pBar

                            // Configure Start Live Class Button onClick
                            btnStart.setOnClickListener {
                                if (exoPlayer.isPlaying) {
                                    // Pause current stream
                                    exoPlayer.pause()
                                    btnStart.text = "Resume Live Class"
                                } else if (exoPlayer.playbackState == Player.STATE_ENDED) {
                                    // Seek to start and replay
                                    exoPlayer.seekTo(0)
                                    exoPlayer.play()
                                    btnStart.text = "Pause Live Class"
                                } else if (exoPlayer.mediaItemCount > 0) {
                                    // Resume playback
                                    exoPlayer.play()
                                    btnStart.text = "Pause Live Class"
                                } else {
                                    // Start fresh stream
                                    pBar.visibility = View.VISIBLE
                                    btnStart.text = "Connecting Stream..."
                                    val mediaItem = MediaItem.fromUri(selectedLecture.streamUrl)
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
                                    exoPlayer.playWhenReady = true
                                    isStreamActive = true
                                }
                            }

                            view
                        },
                        update = { _ ->
                            // Update binding if needed
                        }
                    )

                    // Stream Info bar below the button & progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedLecture.titleHindi,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedLecture.titleEnglish,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "प्रशिक्षक: ${selectedLecture.tutorName}",
                                color = BrightstepAccent,
                                fontSize = 11.sp
                            )
                        }

                        // Stream Quality Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrightstepPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrightstepPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "1080p HD",
                                color = BrightstepPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Lecture Channels / Playlist Selector
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "अन्य लाइव मास्टरक्लास चुनें (Select Live Stream)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "विभिन्न विषयों के शीर्ष लाइव सत्रों में शामिल हों",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(SampleLiveLectures, key = { it.id }) { lecture ->
                        val isSelected = lecture.id == selectedLecture.id
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable {
                                    startStreamingLecture(lecture)
                                }
                                .testTag("lecture_card_${lecture.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF312E81) else Color(0xFF1E293B)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) BrightstepAccent else Color(0xFF334155)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSelected) Color(0xFFEF4444) else Color(0xFF475569)
                                    ) {
                                        Text(
                                            text = if (isSelected) "🔴 STREAMING" else "AVAILABLE",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${lecture.viewerCount} 👁️",
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = lecture.titleHindi,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Text(
                                    text = lecture.subject,
                                    color = BrightstepAccent,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = lecture.tutorName,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Q&A and Chat with AI Tutor during Masterclass
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2937))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Forum,
                                contentDescription = "Live Chat",
                                tint = BrightstepEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "लाइव शंका समाधान (Live Doubt Solving)",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "AI ट्यूटर सक्रिय (Active)",
                            color = BrightstepEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Comments List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        liveChat.takeLast(4).forEach { comment ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (comment.isTutor) Color(0xFF1E1B4B) else Color(0xFF1F2937),
                                border = if (comment.isTutor) androidx.compose.foundation.BorderStroke(1.dp, BrightstepPrimary.copy(alpha = 0.5f)) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = comment.author,
                                            color = if (comment.isTutor) BrightstepAccent else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = comment.timeAgo,
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = comment.text,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Send comment input field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = chatMessage,
                            onValueChange = { chatMessage = it },
                            placeholder = {
                                Text(
                                    "लाइव प्रश्न पूछें (Ask live doubt)...",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("live_chat_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrightstepPrimary,
                                unfocusedBorderColor = Color(0xFF374151),
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (chatMessage.isNotBlank()) {
                                    val userText = chatMessage.trim()
                                    chatMessage = ""
                                    liveChat.add(
                                        LiveChatComment(
                                            id = System.currentTimeMillis().toString(),
                                            author = "आप (You)",
                                            text = userText,
                                            isTutor = false,
                                            timeAgo = "अभी"
                                        )
                                    )

                                    // Fast AI Tutor live doubt response
                                    scope.launch {
                                        delay(80)
                                        liveChat.add(
                                            LiveChatComment(
                                                id = (System.currentTimeMillis() + 1).toString(),
                                                author = "AI ट्यूटर (Tutor)",
                                                text = "बहुत बढ़िया सवाल! '${userText.take(20)}...' के लिए मुख्य सूत्र और चरण व्याख्यान स्क्रीन पर प्रदर्शित किए जा रहे हैं।",
                                                isTutor = true,
                                                timeAgo = "अभी"
                                            )
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrightstepPrimary)
                                .testTag("btn_send_live_comment")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Question",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Pro Masterclass Access Info & Plans Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1B4B)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrightstepPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = "Premium",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "अनलिमिटेड 4K लाइव क्लासेस (Pro Access)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Instamojo पेमेंट के साथ सभी विषय लाइव स्ट्रीम अनलॉक करें",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onNavigateToPlans,
                        colors = ButtonDefaults.buttonColors(containerColor = BrightstepPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("btn_upgrade_from_live")
                    ) {
                        Text("प्लान देखें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
