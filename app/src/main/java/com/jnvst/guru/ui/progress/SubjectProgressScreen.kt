package com.jnvst.guru.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jnvst.guru.R
import com.jnvst.guru.domain.util.Resource
import com.jnvst.guru.ui.theme.BrandIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectProgressScreen(
    subjectId: String,
    viewModel: ProgressViewModel,
    onTopicClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val progressResource by viewModel.progress.collectAsState()

    val subjectTitle = when(subjectId.uppercase()) {
        "MENTAL_ABILITY", "MAT" -> "Mental Ability"
        "ARITHMETIC" -> "Arithmetic"
        else -> "Language"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text(subjectTitle, fontWeight = FontWeight.Black) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (progressResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = progressResource.message ?: "Error",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    val topics = progressResource.data!!.topics.filter { 
                        it.subject.equals(subjectId, ignoreCase = true) || 
                        (subjectId.uppercase() == "MENTAL_ABILITY" && it.subject == "MAT")
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.label_topics_in, subjectTitle),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }

                        if (topics.isEmpty()) {
                            item {
                                Text("No topic data available for this subject.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                            }
                        } else {
                            items(topics) { topic ->
                                TopicProgressCard(
                                    name = topic.displayName ?: topic.topic ?: "Unknown",
                                    progress = (topic.accuracy / 100).toFloat(),
                                    stats = "${topic.correct} / ${topic.questions}",
                                    onClick = { onTopicClick(topic.topic ?: topic.topicId?.toString() ?: "") }
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
fun TopicProgressCard(name: String, progress: Float, stats: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrandIndigo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = stats, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = if (progress > 0.8) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }
            Spacer(Modifier.width(12.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
