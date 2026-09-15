package com.paw.key.presentation.ui.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paw.key.core.designsystem.component.UrlImage
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.noRippleClickable
import com.paw.key.presentation.ui.detail.model.WalkImageUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun DetailImageHolder(
    imageUrls: ImmutableList<WalkImageUiModel>,
    modifier: Modifier = Modifier,
    onClickImage: (Int) -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val imageWidth = maxWidth * 0.317f

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(imageUrls.size) {
                UrlImage(
                    url = imageUrls[it].imageUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(imageWidth)
                        .aspectRatio(1f)
                        .noRippleClickable(
                            onClick = {
                                onClickImage(it)
                            }
                        )
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Preview
@Composable
private fun DetailImageHolderPreview() {
    PawKeyTheme {
        DetailImageHolder(
            imageUrls = persistentListOf()
        )
    }
}