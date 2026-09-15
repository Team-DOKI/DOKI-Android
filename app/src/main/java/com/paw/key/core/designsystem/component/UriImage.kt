package com.paw.key.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.paw.key.R

@Composable
fun UrlImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    isUserIcon: Boolean = false,
) {
    if (LocalInspectionMode.current) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.img_fake_red),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            error = if (isUserIcon) painterResource(R.drawable.img_default_user) else null
        )
    }
}

@Preview
@Composable
fun UrlImagePreview() {
    UrlImage(
        url = "",
        modifier = Modifier.size(100.dp),
    )
}