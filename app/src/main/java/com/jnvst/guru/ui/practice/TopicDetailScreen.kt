package com.jnvst.guru.ui.practice

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.jnvst.guru.ui.theme.BrandEmerald
import com.jnvst.guru.ui.theme.BrandIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topicId: String,
    viewModel: PracticeViewModel,
    onBackClick: () -> Unit
) {
    // For now, let's find the topic from the loaded list
    val topics by viewModel.topics.collectAsState()
    val topic = topics.find { it.id == topicId }
    var selectedMode by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topic?.let { stringResource(it.nameResId) } ?: "", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Topic Info Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = BrandEmerald.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BrandEmerald.copy(alpha = 0.1f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = BrandEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = topic?.let { stringResource(it.nameResId) } ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Find the relationship between words or figures.", // Placeholder desc
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricItem(stringResource(R.string.label_questions), (topic?.questionCount ?: 0).toString(), BrandEmerald)
                        MetricItem(stringResource(R.string.label_duration), stringResource(R.string.mins_format, topic?.durationMinutes ?: 0), Color(0xFFFFA000))
                        MetricItem(stringResource(R.string.label_difficulty), stringResource(topic?.difficultyResId ?: R.string.difficulty_medium), Color(0xFFF44336))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.title_choose_mode),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ModeCard(
                    title = stringResource(R.string.mode_practice_title),
                    subtitle = stringResource(R.string.mode_practice_desc),
                    icon = Icons.Default.Edit,
                    color = BrandEmerald,
                    isSelected = selectedMode == 0,
                    onClick = { selectedMode = 0 }
                )
                ModeCard(
                    title = stringResource(R.string.mode_test_title),
                    subtitle = stringResource(R.string.mode_test_desc),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    color = Color(0xFFFFA000),
                    isSelected = selectedMode == 1,
                    onClick = { selectedMode = 1 }
                )
                ModeCard(
                    title = stringResource(R.string.mode_timed_title),
                    subtitle = stringResource(R.string.mode_timed_desc),
                    icon = Icons.Default.Timer,
                    color = Color(0xFF9575CD),
                    isSelected = selectedMode == 2,
                    onClick = { selectedMode = 2 }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    // This screen is currently bypassed in the new selection flow
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
            ) {
                Text(text = stringResource(R.string.btn_start_practice), fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = color
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = color))
        }
    }
}
