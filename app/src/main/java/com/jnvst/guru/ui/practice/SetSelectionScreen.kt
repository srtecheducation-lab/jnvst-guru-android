package com.jnvst.guru.ui.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jnvst.guru.R
import com.jnvst.guru.domain.model.SetStatus
import com.jnvst.guru.domain.util.Resource
import com.jnvst.guru.ui.theme.BrandEmerald
import com.jnvst.guru.ui.theme.BrandIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetSelectionScreen(
    mode: String,
    subjectId: String,
    topicId: String?,
    difficulty: String,
    viewModel: PracticeViewModel,
    onSetSelected: (Int, Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val statusesResource by viewModel.setStatuses.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSetStatuses(mode, subjectId, topicId, difficulty)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_select_set), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (statusesResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = statusesResource.message ?: "Error",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    val sets = statusesResource.data ?: emptyList()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sets) { status ->
                            SetCard(
                                status = status,
                                onClick = { onSetSelected(status.page, status.completed) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetCard(
    status: SetStatus,
    onClick: () -> Unit
) {
    val color = if (status.completed) BrandEmerald else BrandIndigo
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = color
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.set_format, status.setNumber),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                if (status.completed) {
                    val scoreText = if (status.score != null) "Attempted • Score: ${status.score}" else "Attempted"
                    Text(
                        text = scoreText,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
