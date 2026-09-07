package com.jnvst.guru.ui.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnvst.guru.R
import com.jnvst.guru.domain.util.Resource
import com.jnvst.guru.ui.theme.BrandEmerald
import com.jnvst.guru.ui.theme.BrandIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeResultScreen(
    viewModel: PracticeViewModel,
    onReviewClick: () -> Unit,
    onReattemptClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val resultResource by viewModel.submissionResult.collectAsState()
    val latestAttemptResource by viewModel.latestAttempt.collectAsState()

    // Determine which result to show
    val score: Int?
    val correct: Int?
    val wrong: Int?
    val unanswered: Int?
    val isLoading: Boolean

    if (resultResource is Resource.Success && resultResource?.data != null) {
        val data = resultResource!!.data!!
        score = data.score
        correct = data.correctCount
        wrong = data.wrongCount
        unanswered = data.unansweredCount
        isLoading = false
    } else if (latestAttemptResource is Resource.Success && latestAttemptResource?.data != null) {
        val data = latestAttemptResource!!.data!!
        score = data.score
        correct = data.correctCount
        wrong = data.wrongCount
        unanswered = data.unansweredCount
        isLoading = false
    } else {
        score = null
        correct = null
        wrong = null
        unanswered = null
        isLoading = resultResource is Resource.Loading || latestAttemptResource is Resource.Loading
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_test_completed), fontWeight = FontWeight.Black) },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (score != null) {
                Text(
                    text = stringResource(R.string.test_completed_msg),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = BrandIndigo
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.label_your_score),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Black,
                            color = BrandIndigo
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultStat(stringResource(R.string.label_correct), correct.toString(), BrandEmerald)
                            ResultStat(stringResource(R.string.label_incorrect), wrong.toString(), Color.Red)
                            ResultStat(stringResource(R.string.label_unattempted), unanswered.toString(), Color.Gray)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onReviewClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Review Answers", fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = onReattemptClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Re-attempt", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = onHomeClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.nav_home), fontWeight = FontWeight.Bold)
                }
            } else {
                Text("Result not found")
            }
        }
    }
}

@Composable
fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
