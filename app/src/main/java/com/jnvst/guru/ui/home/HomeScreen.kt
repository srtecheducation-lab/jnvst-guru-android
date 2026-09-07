package com.jnvst.guru.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jnvst.guru.R
import com.jnvst.guru.ui.components.*

@Composable
fun HomeScreen(
    onSubjectWisePracticeClick: () -> Unit,
    onTopicWisePracticeClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AppHeader(
            streakCount = uiState.streakCount,
            notificationCount = uiState.notificationCount
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
        ) {
            item {
                SelectorBar(
                    currentClass = stringResource(R.string.class_6_label),
                    currentLanguage = stringResource(R.string.language_english)
                )
            }

            item {
                HeroBanner()
            }

            item {
                ContinuePracticeCard(
                    state = uiState.continuePractice,
                    onContinueClick = { /* TODO */ }
                )
            }

            item {
                ProgressSummarySection(state = uiState.progressSummary)
            }

            item {
                MainActionsList(
                    onSubjectClick = onSubjectWisePracticeClick,
                    onTopicClick = onTopicWisePracticeClick,
                    onPyqClick = { /* TODO */ },
                    onMockClick = { /* TODO */ }
                )
            }
        }
    }
}
