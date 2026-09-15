package com.paw.key.presentation.ui.dbti.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paw.key.core.designsystem.component.DokiBorderButton
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.dbti.result.component.ResultBox
import com.paw.key.presentation.ui.dbti.result.model.DbtiResultUiModel
import com.paw.key.presentation.ui.dbti.result.model.TraitAnalysis
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ResultScreen(
    resultUiModel: DbtiResultUiModel,
    onRetakeTest: () -> Unit,
    onGoHome: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PawKeyTheme.colors.defaultButton)
    ) {
        TopBar(
            title = "DBTI 결과",
            onBackClick = navigateUp,
            isBackVisible = true
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            with(resultUiModel) {
                ResultBox(
                    type = type,
                    name = name,
                    imageUrl = imageUrl,
                    keywords = keywords,
                    description = description,
                    analysis = analysis
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DokiBorderButton(
                text = "다시 테스트하기",
                onClick = onRetakeTest,
                modifier = Modifier.weight(1f),
                enabled = true
            )

            DokiButton(
                text = "홈으로 가기",
                onClick = onGoHome,
                modifier = Modifier.weight(1f),
                enabled = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenPreview() {
    PawKeyTheme {
        ResultScreen(
            resultUiModel = DbtiResultUiModel(
                type = "EPR",
                name = "탐험대장 멍멍이",
                imageUrl = null,
                keywords = persistentListOf("모험", "활발", "사교성"),
                description = "활발하고 친구들과 어울리며 모험을 좋아해요.\n집사에게 언제나 애너지를 주는 타입!",
                analysis = persistentListOf(
                    TraitAnalysis(
                        leftLabel = "휴식가",
                        rightLabel = "탐험가",
                        dominantSide = "right",
                        score = 2
                    ),
                    TraitAnalysis(
                        leftLabel = "부끄멍",
                        rightLabel = "적극멍",
                        dominantSide = "right",
                        score = 2
                    ),
                    TraitAnalysis(
                        leftLabel = "루틴러",
                        rightLabel = "자유러",
                        dominantSide = "left",
                        score = 2
                    )
                ),
            ),
            onRetakeTest = {},
            onGoHome = {},
            navigateUp = {}
        )
    }
}