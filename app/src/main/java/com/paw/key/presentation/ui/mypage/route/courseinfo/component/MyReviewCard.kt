package com.paw.key.presentation.ui.mypage.route.courseinfo.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paw.key.R
import com.paw.key.core.designsystem.component.SubChip
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.detail.component.FilterChipDivider
import com.paw.key.presentation.ui.mypage.route.courseinfo.model.CourseData
import com.paw.key.presentation.ui.mypage.route.courseinfo.util.formatDateTime

@Composable
fun MyReviewCard(
    course: CourseData,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    val maxVisibleItems = 5
    val visibleItems = if (isExpanded) course.categoryOptionSummary else course.categoryOptionSummary.take(maxVisibleItems)
    val hiddenCount = course.categoryOptionSummary.size - maxVisibleItems


    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PawKeyTheme.colors.white1,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = PawKeyTheme.colors.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = course.title,
            color = PawKeyTheme.colors.black,
            style = PawKeyTheme.typography.mainButtonActive
        )

        Spacer(modifier = Modifier.height(4.dp))

        ReviewContent(
            iconRes = R.drawable.ic_mypage_location_mark,
            content = course.location
        )
        ReviewContent(
            iconRes = R.drawable.ic_mypage_time_mark,
            content = formatDateTime(course.date)
        )

        Spacer(modifier = Modifier.height(18.dp))

        FlowRow (
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            visibleItems.forEach { item ->
                SubChip(
                    text = item,
                    isActionChip = true,
                )
            }
        }

        Spacer(modifier = Modifier.height(9.dp))

        if (!isExpanded && hiddenCount > 0) {
            FilterChipDivider(
                hiddenCount = hiddenCount,
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ReviewContent(
    @DrawableRes iconRes: Int,
    content: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Text(
            text = content,
            color = PawKeyTheme.colors.defaultDark,
            style = PawKeyTheme.typography.bodyActive
        )
    }

}

@Preview
@Composable
private fun PreviewMyReviewCard() {
    PawKeyTheme {
        MyReviewCard(
            course = CourseData()
        )
    }
}