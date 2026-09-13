package com.jnvst.guru.ui.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.jnvst.guru.R
import com.jnvst.guru.domain.model.Question
import com.jnvst.guru.domain.model.Topic
import com.jnvst.guru.domain.util.Resource
import com.jnvst.guru.ui.theme.BrandEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSessionScreen(
    viewModel: PracticeViewModel,
    onBackClick: () -> Unit,
    onFinish: () -> Unit
) {
    val questionsResource by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val submissionResult by viewModel.submissionResult.collectAsState()
    val isReviewMode by viewModel.isReviewMode.collectAsState()
    val currentTopic by viewModel.currentTopic.collectAsState()

    LaunchedEffect(submissionResult) {
        if (submissionResult is Resource.Success) {
            onFinish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_practice), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            if (questionsResource is Resource.Success && questionsResource.data != null) {
                val questions = questionsResource.data!!
                if (questions.isNotEmpty()) {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentIndex > 0) {
                                OutlinedButton(
                                    onClick = { viewModel.previousQuestion() },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.btn_previous))
                                }
                            } else {
                                Spacer(modifier = Modifier.width(100.dp)) // Spacer to keep Next on the right
                            }

                            if (currentIndex < questions.size - 1) {
                                Button(
                                    onClick = { viewModel.nextQuestion() },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(stringResource(R.string.btn_next))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        if (isReviewMode) onFinish() else viewModel.submitAttempt() 
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    enabled = submissionResult !is Resource.Loading
                                ) {
                                    if (submissionResult is Resource.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(if (isReviewMode) "Finish" else stringResource(R.string.btn_submit))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (questionsResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(R.string.msg_error_questions),
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is Resource.Success -> {
                    val questions = questionsResource.data!!
                    if (questions.isEmpty()) {
                        Text(
                            text = stringResource(R.string.msg_no_questions),
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        val currentQuestion = questions[currentIndex]
                        QuestionContent(
                            question = currentQuestion,
                            index = currentIndex,
                            totalCount = questions.size,
                            isReviewMode = isReviewMode,
                            currentTopic = currentTopic,
                            onOptionSelected = { viewModel.selectOption(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: Question,
    index: Int,
    totalCount: Int,
    isReviewMode: Boolean,
    currentTopic: Topic?,
    onOptionSelected: (Int) -> Unit
) {
    val topicName = currentTopic?.nameOverride ?: (currentTopic?.nameResId?.let { stringResource(it) } ?: "")
    val topicDescription = currentTopic?.descriptionOverride ?: (currentTopic?.descriptionResId?.let { stringResource(it) } ?: "")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_question_counter, index + 1, totalCount),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (topicName.isNotEmpty()) {
                    Text(
                        text = topicName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            if (topicDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = topicDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (question.questionText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 28.sp
                )
            }
        }

        if (!question.questionImageUrl.isNullOrEmpty()) {
            item {
                AsyncImage(
                    model = question.questionImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }
        }

        if (question.topicCode == "ODD_ONE_OUT" && !question.optionImageUrls.isNullOrEmpty()) {
            // 2x2 Grid for ODD_ONE_OUT
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MatOptionCard(0, question, isReviewMode, onOptionSelected, Modifier.weight(1f))
                        MatOptionCard(1, question, isReviewMode, onOptionSelected, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MatOptionCard(2, question, isReviewMode, onOptionSelected, Modifier.weight(1f))
                        MatOptionCard(3, question, isReviewMode, onOptionSelected, Modifier.weight(1f))
                    }
                }
            }
        } else {
            // Default List Layout
            val hasImageOptions = !question.optionImageUrls.isNullOrEmpty()
            val optionsCount = if (hasImageOptions) question.optionImageUrls!!.size else question.options.size

            items(optionsCount) { optionIndex ->
                val label = ('A' + optionIndex).toString()
                val optionText = if (hasImageOptions) null else question.options[optionIndex]
                val optionImageUrl = if (hasImageOptions) question.optionImageUrls!![optionIndex] else null
                
                val isSelected = question.selectedOptionIndex == optionIndex
                val isCorrect = question.correctOptionIndex == optionIndex
                
                val optionColor = when {
                    isReviewMode && isCorrect -> BrandEmerald
                    isReviewMode && isSelected && !isCorrect -> Color.Red
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> Color.LightGray.copy(alpha = 0.5f)
                }
                
                val bgColor = when {
                    isReviewMode && isCorrect -> BrandEmerald.copy(alpha = 0.1f)
                    isReviewMode && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.1f)
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Surface(
                    onClick = { if (!isReviewMode) onOptionSelected(optionIndex) },
                    shape = RoundedCornerShape(16.dp),
                    color = bgColor,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected || (isReviewMode && isCorrect)) 2.dp else 1.dp,
                        color = optionColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = if (isSelected || (isReviewMode && isCorrect)) optionColor else Color.LightGray.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || (isReviewMode && isCorrect)) Color.White else Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        if (optionImageUrl != null) {
                            AsyncImage(
                                model = optionImageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(80.dp)
                                    .weight(1f),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                        } else if (optionText != null) {
                            Text(text = optionText, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        }
                        
                        if (isReviewMode && isCorrect) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = BrandEmerald)
                        } else if (isReviewMode && isSelected && !isCorrect) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatOptionCard(
    optionIndex: Int,
    question: Question,
    isReviewMode: Boolean,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = ('A' + optionIndex).toString()
    val optionImageUrl = question.optionImageUrls?.getOrNull(optionIndex) ?: ""
    
    val isSelected = question.selectedOptionIndex == optionIndex
    val isCorrect = question.correctOptionIndex == optionIndex
    
    val optionColor = when {
        isReviewMode && isCorrect -> BrandEmerald
        isReviewMode && isSelected && !isCorrect -> Color.Red
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.LightGray.copy(alpha = 0.5f)
    }
    
    val bgColor = when {
        isReviewMode && isCorrect -> BrandEmerald.copy(alpha = 0.1f)
        isReviewMode && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.1f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    Surface(
        onClick = { if (!isReviewMode) onOptionSelected(optionIndex) },
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected || (isReviewMode && isCorrect)) 2.dp else 1.dp,
            color = optionColor
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = if (isSelected || (isReviewMode && isCorrect)) optionColor else Color.LightGray.copy(alpha = 0.2f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected || (isReviewMode && isCorrect)) Color.White else Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            AsyncImage(
                model = optionImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            
            if (isReviewMode && (isCorrect || (isSelected && !isCorrect))) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) BrandEmerald else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
