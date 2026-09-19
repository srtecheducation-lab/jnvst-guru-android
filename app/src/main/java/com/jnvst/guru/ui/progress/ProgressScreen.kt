package com.jnvst.guru.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnvst.guru.R
import com.jnvst.guru.domain.model.ProgressResponse
import com.jnvst.guru.domain.model.RecentAttempt
import com.jnvst.guru.domain.model.SubjectProgress
import com.jnvst.guru.domain.util.Resource
import com.jnvst.guru.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onSubjectClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val progressResource by viewModel.progress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.nav_progress), fontWeight = FontWeight.Black) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 20.dp,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrandIndigo
                    )
                }
            }
        ) {
            val tabs = listOf(
                stringResource(R.string.tab_overview),
                stringResource(R.string.tab_subjects),
                stringResource(R.string.tab_topics),
                stringResource(R.string.tab_tests)
            )
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (progressResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = progressResource.message ?: "Something went wrong",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
                is Resource.Success -> {
                    val data = progressResource.data!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        when (selectedTab) {
                            0 -> { // Overview
                                item { PerformanceBanner(data.overall.accuracy) }
                                item { MetricsGrid(data.overall) }
                                item { PerformanceTrendSection() }
                                item { RecentActivitySection(data.recentAttempts) }
                            }
                            1 -> { // Subjects
                                item {
                                    SubjectProgressList(data.subjects, onSubjectClick)
                                }
                            }
                            2 -> { // Topics
                                item { TopicProgressList(data.topics) }
                            }
                            3 -> { // Tests
                                item { ComingSoonCard("Mock Tests analytics are coming soon!") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceBanner(accuracy: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandIndigo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { (accuracy / 100).toFloat() },
                    modifier = Modifier.size(80.dp),
                    color = Color.White,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text("${accuracy.toInt()}%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = stringResource(R.string.progress_banner_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.progress_banner_msg),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun MetricsGrid(summary: com.jnvst.guru.domain.model.ProgressSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(
                label = stringResource(R.string.questions_solved),
                value = summary.questions.toString(),
                subValue = "${summary.attempts} Attempts",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                color = BrandIndigo,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = stringResource(R.string.average_accuracy),
                value = "${summary.accuracy.toInt()}%",
                subValue = "Historical Avg",
                icon = Icons.Default.EmojiEvents,
                color = BrandEmerald,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(
                label = "Correct / Wrong",
                value = "${summary.correct} / ${summary.wrong}",
                subValue = "Latest Performance",
                icon = Icons.Default.CheckCircleOutline,
                color = Color(0xFFFFA000),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Unanswered",
                value = summary.unanswered.toString(),
                subValue = "Try to complete all!",
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                color = Color(0xFF9575CD),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, subValue: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.padding(6.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(text = subValue, style = MaterialTheme.typography.labelSmall, color = BrandEmerald, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PerformanceTrendSection() {
    Column {
        Text(text = stringResource(R.string.label_performance_trend), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))
        ComingSoonCard("Historical performance charts are coming soon!")
    }
}

@Composable
fun RecentActivitySection(attempts: List<RecentAttempt>) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.label_recent_activity), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
        
        if (attempts.isEmpty()) {
            Text("No recent activity found.", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
        } else {
            attempts.forEach { attempt ->
                RecentActivityItem(
                    title = attempt.displayTitle ?: "${attempt.subject} Set ${attempt.page + 1}",
                    time = attempt.submittedAt.substringBefore("T"),
                    score = "${attempt.score} / ${attempt.questionCount}",
                    scoreColor = if (attempt.correctCount > attempt.wrongCount) BrandEmerald else Color(0xFFFFA000)
                )
            }
        }
    }
}

@Composable
fun RecentActivityItem(title: String, time: String, score: String, scoreColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = BrandIndigo.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = BrandIndigo, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(text = time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Text(text = score, fontWeight = FontWeight.Black, color = scoreColor)
        }
    }
}

@Composable
fun SubjectProgressList(subjects: List<SubjectProgress>, onSubjectClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = stringResource(R.string.label_subject_wise_progress), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        
        subjects.forEach { s ->
            val color = when (s.subject.uppercase()) {
                "ARITHMETIC" -> Color(0xFFFFA000)
                "MAT" -> Color(0xFF9575CD)
                else -> BrandEmerald
            }
            val title = when (s.subject.uppercase()) {
                "ARITHMETIC" -> "Arithmetic"
                "MAT" -> "Mental Ability (MAT)"
                else -> "Language"
            }
            
            SubjectProgressCard(
                title = title,
                progress = (s.accuracy / 100).toFloat(),
                stats = "${s.correct} / ${s.questions} Correct",
                color = color,
                onClick = { onSubjectClick(s.subject.lowercase()) }
            )
        }
    }
}

@Composable
fun SubjectProgressCard(title: String, progress: Float, stats: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = color, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = title, fontWeight = FontWeight.Black)
                    Text(text = stats, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "Accuracy: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TopicProgressList(topics: List<com.jnvst.guru.domain.model.TopicProgress>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Topic-wise Accuracy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        
        if (topics.isEmpty()) {
            Text("No topic data available yet.", color = Color.Gray)
        } else {
            topics.forEach { topic ->
                TopicProgressCard(
                    name = topic.displayName ?: topic.topic ?: "Unknown",
                    progress = (topic.accuracy / 100).toFloat(),
                    stats = "${topic.correct} / ${topic.questions}",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun ComingSoonCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}
