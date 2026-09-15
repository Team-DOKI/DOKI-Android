package com.paw.key.presentation.ui.dbti.result.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.dbti.result.model.TraitAnalysis
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf



@Composable
fun ResultBox(
    type: String,
    name: String,
    imageUrl: String?,
    keywords: ImmutableList<String>,
    description: String,
    analysis: ImmutableList<TraitAnalysis>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PawKeyTheme.colors.background,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 타이틀
            Text(
                text = "내 강아지의 성향은",
                color = PawKeyTheme.colors.defaultDark,
                textAlign = TextAlign.Center,
                style = PawKeyTheme.typography.body14M
            )

            // 이름
            Text(
                text = name,
                color = PawKeyTheme.colors.contents,
                textAlign = TextAlign.Center,
                style = PawKeyTheme.typography.header2
            )

            // 타입
            Text(
                text = type,
                color = PawKeyTheme.colors.primary,
                textAlign = TextAlign.Center,
                style = PawKeyTheme.typography.header1
            )

            // 이미지
            if (imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(PawKeyTheme.colors.defaultButton)
                )
            }

            // 키워드
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                keywords.forEach { keyword ->
                    CharacterChip(text = "# $keyword")
                }
            }

            // 설명
            Text(
                text = description,
                color = PawKeyTheme.colors.contents,
                textAlign = TextAlign.Center,
                style = PawKeyTheme.typography.body14M,
                modifier = Modifier.fillMaxWidth()
            )

            // 분석 바들
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                analysis.forEach { trait ->
                    TraitBar(
                        leftLabel = trait.leftLabel,
                        rightLabel = trait.rightLabel,
                        dominantSide = trait.dominantSide,
                        score = trait.score
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultBoxPreview() {
    PawKeyTheme {
        ResultBox(
            type = "ISF",
            name = "온순한 방구석멍",
            imageUrl = null,
            keywords = persistentListOf("차분", "다소 활발", "탐험"),
            description = "내향적이고 온순하며 자유로운 흐름 속에서 소소한 행복을 찾습니다.",
            analysis = persistentListOf(
                TraitAnalysis(
                    leftLabel = "탐험가",
                    rightLabel = "휴식가",
                    dominantSide = "right",
                    score = 2
                ),
                TraitAnalysis(
                    leftLabel = "인싸싸",
                    rightLabel = "독고다",
                    dominantSide = "left",
                    score = 3
                ),
                TraitAnalysis(
                    leftLabel = "루틴러",
                    rightLabel = "자유러",
                    dominantSide = "right",
                    score = 2
                )
            )
        )
    }
}