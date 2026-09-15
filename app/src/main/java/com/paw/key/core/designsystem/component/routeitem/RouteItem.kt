package com.paw.key.core.designsystem.component.routeitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paw.key.R
import com.paw.key.core.designsystem.component.DogkyFilterBadge
import com.paw.key.core.designsystem.component.UrlImage
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.designsystem.theme.PretendardBold
import com.paw.key.core.extension.noRippleClickable

@Composable
fun RouteItem(
    location: String,
    routeTitle: String,
    routeImage: String,
    routeTime: String,
    routeDate: String,
    onClickHeart: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLiked: Boolean = false
) {
    Column(
        modifier = modifier
            .noRippleClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f/4f)
                .background(
                    color = PawKeyTheme.colors.defaultBright,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
        ) {
            UrlImage(
                url = routeImage,
                contentDescription = "routeImage",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DogkyFilterBadge(
                    location = location,
                    onLocationClick = {},
                    horizontalPadding = 6,
                    verticalPadding = 5,
                    modifier = Modifier
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = if (isLiked) ImageVector.vectorResource(R.drawable.ic_heart_filled) else ImageVector.vectorResource(R.drawable.ic_heart_default),
                    contentDescription = "heart",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(8.dp)
                        .noRippleClickable(onClick = onClickHeart)
                )
            }
        }

        HomeRouteItemInfo(
            routeTitle = routeTitle,
            routeTime = routeTime,
            routeDate = routeDate
        )
    }
}

@Composable
fun HomeRouteItemInfo(
    routeTitle: String,
    routeTime: String,
    routeDate: String,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .padding(
                start = 4.dp,
                end = 4.dp
            )
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = routeTitle,
            style = PawKeyTheme.typography.bodyActive,
            fontFamily = PretendardBold,
            color = PawKeyTheme.colors.contents
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HomeIconText(
                icon = ImageVector.vectorResource(R.drawable.ic_calendar),
                text = routeDate
            )

            Spacer(modifier = Modifier.weight(1f))

            HomeIconText(
                icon = ImageVector.vectorResource(R.drawable.ic_alarm),
                text = "${routeTime}min"
            )
        }
    }
}

@Composable
fun HomeIconText(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            style = PawKeyTheme.typography.buttonSmall,
            color = PawKeyTheme.colors.defaultMiddle
        )
    }
}

@Preview
@Composable
private fun RouteItemPreview() {
    PawKeyTheme {
        RouteItem(
            routeImage = "",
            routeTime = "10",
            routeTitle = "강남구 역삼동",
            routeDate = "2025/11/06",
            location = "강남구 역삼동",
            onClickHeart = {},
            onClick = {},
        )
    }

}
