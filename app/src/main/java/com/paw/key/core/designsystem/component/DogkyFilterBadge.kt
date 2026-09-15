package com.paw.key.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.noRippleClickable

@Composable
fun DogkyFilterBadge(
    location: String,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 10,
    verticalPadding: Int = 9,
) {
    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(4.dp)
            )
            .background(
                color = PawKeyTheme.colors.opacity5Primary,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = PawKeyTheme.colors.primary,
                shape = RoundedCornerShape(4.dp)
            )
            .noRippleClickable(
                onClick = onLocationClick
            )
            .padding(horizontal = horizontalPadding.dp, vertical = verticalPadding.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = location,
            style = PawKeyTheme.typography.buttonSmall,
            color = PawKeyTheme.colors.primary,
        )
    }
}

@Preview
@Composable
private fun RegionBadgePreview() {
    PawKeyTheme {
        DogkyFilterBadge(
            location = "강남구 역삼동",
            onLocationClick = {},
            horizontalPadding = 6,
            verticalPadding = 5,
        )
    }
}
