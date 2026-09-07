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
import com.jnvst.guru.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onSubjectClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
            },
            actions = {
                TextButton(onClick = { /* Filter */ }) {
                    Text(stringResource(R.string.time_filter_week), fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            when (selectedTab) {
                0 -> { // Overview
                    item { PerformanceBanner() }
                    item { MetricsGrid() }
                    item { PerformanceTrendSection() }
                    item { RecentActivitySection() }
                }
                1 -> { // Subjects
                    item {
                        SubjectProgressList(onSubjectClick)
                    }
                }
                else -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Content coming soon...", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceBanner() {
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
                    progress = { 0.75f },
                    modifier = Modifier.size(80.dp),
                    color = Color.White,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text("75%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
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
fun MetricsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(
                label = stringResource(R.string.questions_solved),
                value = "480",
                subValue = "+120 this week",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                color = BrandIndigo,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = stringResource(R.string.average_accuracy),
                value = "78%",
                subValue = "+8% this week",
                icon = Icons.Default.EmojiEvents,
                color = BrandEmerald,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(
                label = stringResource(R.string.study_time),
                value = "6h 30m",
                subValue = "+1h 20m this week",
                icon = Icons.Default.AccessTime,
                color = Color(0xFFFFA000),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = stringResource(R.string.day_streak_label),
                value = "12",
                subValue = "Keep it going!",
                icon = Icons.Default.LocalFireDepartment,
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.label_performance_trend), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            Text(text = stringResource(R.string.time_filter_week), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Line Chart Placeholder", color = Color.LightGray)
        }
    }
}

@Composable
fun RecentActivitySection() {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.label_recent_activity), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            TextButton(onClick = {}) { Text(stringResource(R.string.btn_view_all)) }
        }
        
        RecentActivityItem("Mock Test 1", "Today, 10:30 AM", "82%", BrandEmerald)
        RecentActivityItem("Arithmetic: Fractions", "Yesterday", "65%", Color(0xFFFFA000))
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
fun SubjectProgressList(onSubjectClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = stringResource(R.string.label_subject_wise_progress), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        
        SubjectProgressCard("Mental Ability", 0.82f, "120 / 150 Qs", BrandIndigo, onClick = { onSubjectClick("mental_ability") })
        SubjectProgressCard("Arithmetic", 0.76f, "180 / 230 Qs", Color(0xFFFFA000), onClick = { onSubjectClick("arithmetic") })
        SubjectProgressCard("Language", 0.75f, "180 / 240 Qs", BrandEmerald, onClick = { onSubjectClick("language") })
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
                Icon(Icons.Default.Person, contentDescription = null, tint = color, modifier = Modifier.padding(10.dp))
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
