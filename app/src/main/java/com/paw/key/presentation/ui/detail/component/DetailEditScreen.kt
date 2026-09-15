package com.paw.key.presentation.ui.detail.component

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.R
import com.paw.key.core.designsystem.component.DokiBorderButton
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.component.walk.WalkReviewInfoHolder
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.community.model.FilterCategoryUiModel
import com.paw.key.presentation.ui.community.model.SelectionType
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewImageRow
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewMultipleFilter
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewSingleFilter
import com.paw.key.presentation.ui.detail.DetailEditState
import com.paw.key.presentation.ui.detail.DetailViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun DetailEditRoute(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit,
    navigateHome: () -> Unit,
    navigateWalkDetail: (postId: Int, routeId: Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val editState by viewModel.editState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initEditState()
    }

    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris ->
        uris.forEach { viewModel.addEditImage(it) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.take(3).forEach { viewModel.addEditImage(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) galleryLauncher.launch("image/*")
    }

    DetailEditScreen(
        paddingValues = paddingValues,
        editState = editState,
        navigateUp = navigateUp,
        navigateHome = navigateHome,
        navigateWalkDetail = { navigateWalkDetail(editState.postId, editState.routeId) },
        onClickImage = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMultipleMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                permissionLauncher.launch(imagePermission)
            }
        },
        onImageDelete = viewModel::deleteEditImage,
        onFilterClick = viewModel::onEditFilterClick,
        onTitleValueChange = viewModel::updateEditTitle,
        onContentValueChange = viewModel::updateEditContent,
        onClickComplete = viewModel::submitEdit
    )
}

@Composable
private fun DetailEditScreen(
    paddingValues: PaddingValues,
    editState: DetailEditState,
    navigateUp: () -> Unit,
    navigateHome: () -> Unit,
    navigateWalkDetail: () -> Unit,
    onClickImage: () -> Unit,
    onImageDelete: (Int) -> Unit,
    onFilterClick: (Int, FilterCategoryUiModel) -> Unit,
    onTitleValueChange: (String) -> Unit,
    onContentValueChange: (String) -> Unit,
    onClickComplete: (Boolean) -> Unit,
) {
    // 기존 이미지(URL) + 새 이미지(URI) 합쳐서 표시
    val combinedImageList = remember(editState.existingImageUrls, editState.newImageUris) {
        (editState.existingImageUrls.map { it.imageUrl } +
                editState.newImageUris.map { it.toString() })
            .toPersistentList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PawKeyTheme.colors.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        TopBar(
            title = "산책 기록 수정하기",
            isBackVisible = true,
            thickness = 2,
            onBackClick = navigateUp
        )

        Spacer(modifier = Modifier.height(14.dp))

        WalkReviewImageRow(
            imageList = combinedImageList.map { it.toUri() }.toPersistentList(),
            onClickCard = { index, _ ->
                if (index != 0) onClickImage()
            },
            onImageDelete = onImageDelete,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // routeSummary는 기존 postDetail에서 가져옴
            WalkReviewInfoHolder(
                icon = R.drawable.ic_walk_review_location,
                content = editState.locationText
            )
            WalkReviewInfoHolder(
                icon = R.drawable.ic_walk_review_time,
                content = editState.dateTimeText
            )
            WalkReviewInfoHolder(
                icon = R.drawable.ic_walk_review_course_info,
                content = editState.metaTagTexts.joinToString(" | ")
            )

            Spacer(modifier = Modifier.height(40.dp))

            editState.filterUiModel.allCategories.forEach { category ->
                val selectedIds = editState.getSelectedOptionIds(category)

                if (category.selectionType == SelectionType.SINGLE) {
                    WalkReviewSingleFilter(
                        title = category.name,
                        filterList = category.options.map { it.text }.toImmutableList(),
                        selectedItem = category.options
                            .firstOrNull { selectedIds.contains(it.id) }?.text.orEmpty(),
                        onItemSelected = { selectedText ->
                            val optionId = category.options.first { it.text == selectedText }.id
                            onFilterClick(optionId, category)
                        }
                    )
                } else {
                    WalkReviewMultipleFilter(
                        title = category.name,
                        filterList = category.options.map { it.text }.toImmutableList(),
                        selectedItems = selectedIds
                            .mapNotNull { id -> category.options.firstOrNull { it.id == id }?.text }
                            .toPersistentList(),
                        onItemClick = { selectedText ->
                            val optionId = category.options.first { it.text == selectedText }.id
                            onFilterClick(optionId, category)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "산책에 대한 후기를 작성해주세요",
                style = PawKeyTheme.typography.subTitle,
                color = PawKeyTheme.colors.contents
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = editState.title,
                onValueChange = onTitleValueChange,
                textStyle = PawKeyTheme.typography.bodyActive.copy(color = PawKeyTheme.colors.contents),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = PawKeyTheme.colors.defaultBright, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (editState.title.isEmpty()) {
                            Text(
                                text = "후기 제목을 14글자 이내로 입력해주세요",
                                style = PawKeyTheme.typography.bodyDefault,
                                color = PawKeyTheme.colors.defaultMiddle
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            BasicTextField(
                value = editState.content,
                onValueChange = onContentValueChange,
                textStyle = PawKeyTheme.typography.bodyActive.copy(color = PawKeyTheme.colors.contents),
                modifier = Modifier.fillMaxWidth().heightIn(min = 216.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = PawKeyTheme.colors.defaultBright, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (editState.content.isEmpty()) {
                            Text(
                                text = "산책에 대한 내용을 250자 이내로 작성해주세요",
                                style = PawKeyTheme.typography.bodyDefault,
                                color = PawKeyTheme.colors.defaultMiddle
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            DokiBorderButton(
                text = "산책 기록 나만보기",
                enabled = true,
                onClick = { onClickComplete(false) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DokiButton(
                text = "산책 기록 공유하기",
                enabled = editState.title.isNotEmpty() && editState.content.isNotEmpty(),
                onClick = { onClickComplete(true) }
            )
        }
    }
}

@Preview
@Composable
private fun DetailEditScreenPreview() {
    PawKeyTheme {
        DetailEditScreen(
            paddingValues = PaddingValues(),
            editState = DetailEditState(),
            navigateUp = {},
            navigateHome = {},
            navigateWalkDetail = {},
            onClickImage = {},
            onImageDelete = {},
            onFilterClick = { _, _ -> },
            onTitleValueChange = {},
            onContentValueChange = {},
            onClickComplete = {}
        )
    }
}