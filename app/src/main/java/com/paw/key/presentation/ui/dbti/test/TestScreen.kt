package com.paw.key.presentation.ui.dbti.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.LoadingScreen
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.dbti.component.SelectCard
import com.paw.key.presentation.ui.dbti.test.state.TestUiState
import com.paw.key.presentation.ui.dbti.viewmodel.DbtiViewModel

@Composable
fun TestScreen(
    onBackClick: () -> Unit,
    navigateToResult : () -> Unit,
    viewModel: DbtiViewModel,
) {
    val uiState by viewModel.testUiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is TestUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = PawKeyTheme.colors.background)
            ) {
                TopBar(
                    title = "프로필 설정",
                    onBackClick = onBackClick,
                    isBackVisible = true
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${state.questionNumber}/${state.totalQuestions}",
                            color = PawKeyTheme.colors.defaultMiddle,
                            style = PawKeyTheme.typography.body14M
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.categoryName,
                            color = PawKeyTheme.colors.primary,
                            style = PawKeyTheme.typography.bodyActive
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.questionText,
                            color = PawKeyTheme.colors.contents,
                            textAlign = TextAlign.Center,
                            style = PawKeyTheme.typography.header3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.options.forEach { option ->
                            SelectCard(
                                text = option.text,
                                imageUrl = option.imageUrl,
                                onClick = { viewModel.selectOption(option.id) },
                                modifier = Modifier.weight(1f),
                                isSelected = state.selectedOptionId == option.id
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    DokiButton(
                        text = "다음으로",
                        onClick = { viewModel.nextQuestion(onNavigateToResult = navigateToResult) },
                        enabled = state.selectedOptionId != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(34.dp))
                }
            }
        }
        else -> {
            LoadingScreen()
        }
    }
}

@PreviewScreenSizes
@Composable
private fun TestScreenPreview() {
    PawKeyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(
                    title = "프로필 설정",
                    onBackClick = {},
                    isBackVisible = true
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.weight(67f))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "1/9",
                            color = PawKeyTheme.colors.defaultMiddle,
                            style = PawKeyTheme.typography.body14M
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "탐험가 vs 휴식가",
                            color = PawKeyTheme.colors.primary,
                            style = PawKeyTheme.typography.bodyActive
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "산책 중 다른 강아지를 만나면\n우리 강아지는...",
                            color = PawKeyTheme.colors.contents,
                            textAlign = TextAlign.Center,
                            style = PawKeyTheme.typography.header3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.weight(48f))
                    Spacer(modifier = Modifier.weight(219.31f))
                    Spacer(modifier = Modifier.weight(1f))

                    DokiButton(
                        text = "다음으로",
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(34.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectCard(
                    text = "동네 구석구석 새 길을\n탐험해야 신나요",
                    imageUrl = null,
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    isSelected = false
                )
                SelectCard(
                    text = "익숙한 코스에서\n짧게 다녀오는게 좋아요",
                    imageUrl = null,
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    isSelected = false
                )
            }
        }
    }
}