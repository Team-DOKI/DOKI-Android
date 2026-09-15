package com.paw.key.presentation.ui.mypage.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.paw.key.core.extension.noRippleClickable

@Composable
fun MyPageCard(
    userName: String,
    userAge: String,
    userGender: String,
    dogBreed: String,
    dogImage: String?,
    buttonTitle: String,
    onButtonClick: () -> Unit,
    navigatePetProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PawKeyTheme.colors.background,
                shape = RoundedCornerShape(16.dp)
            )
            .noRippleClickable(onClick = navigatePetProfile)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MyPageCardContent(
            userName = userName,
            userAge = userAge,
            userGender = userGender,
            dogBreed = dogBreed,
            image = dogImage,
            modifier = modifier
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = PawKeyTheme.colors.defaultButton
        )

        CardButton(
            text = buttonTitle,
            onClick = onButtonClick
        )
    }
}


@Composable
private fun MyPageCardContent(
    userName: String,
    userAge: String,
    userGender: String,
    dogBreed: String,
    image: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UrlImage(
            url = image ?: "",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            isUserIcon = true
        )

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = userName,
                style = PawKeyTheme.typography.subTitle,
                color = PawKeyTheme.colors.contents
            )

            Text(
                "$userAge / $userGender / $dogBreed",
                style = PawKeyTheme.typography.subButtonDefault,
                color = PawKeyTheme.colors.defaultDark
            )
        }
    }
}

@Composable
private fun CardButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PawKeyTheme.colors.primaryGra1,
                shape = RoundedCornerShape(8.dp)
            )
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = PawKeyTheme.colors.primary,
            style = PawKeyTheme.typography.body14Sb,
        )
    }
}


@Preview
@Composable
private fun MyPageCardPreview() {
    PawKeyTheme {
        MyPageCard(
            userName = "단지",
            userAge = "20",
            userGender = "남",
            dogBreed = "견종 이름",
            buttonTitle = "DBTI 검사하러 가기",
            dogImage = null,
            onButtonClick = {},
            navigatePetProfile = {}
        )
    }
}