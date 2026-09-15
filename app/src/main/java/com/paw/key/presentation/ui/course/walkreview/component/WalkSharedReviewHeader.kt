package com.paw.key.presentation.ui.course.walkreview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paw.key.core.designsystem.component.UrlImage
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.course.walkreview.model.WalkSharedReviewUiModel

@Composable
fun WalkSharedReviewHeader(
    modifier: Modifier = Modifier,
    item : WalkSharedReviewUiModel = WalkSharedReviewUiModel(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = item.postTitle,
            style = PawKeyTheme.typography.header3,
            color = PawKeyTheme.colors.contents,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = PawKeyTheme.colors.defaultButton,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            UrlImage(
                url = item.profileImageUrl,
                modifier = Modifier
                    .size(43.dp)
                    .clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Crop,
                isUserIcon = true
            )

            Text(
                text = item.profileName,
                style = PawKeyTheme.typography.subTitle,
                color = PawKeyTheme.colors.defaultDark
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = PawKeyTheme.colors.defaultButton,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun WalkSharedReviewHeaderPreview() {
    PawKeyTheme {
        WalkSharedReviewHeader()
    }
}