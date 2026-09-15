package com.paw.key.presentation.ui.course.walkreview

import android.Manifest
import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewDialog
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewImageRow
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewMultipleFilter
import com.paw.key.presentation.ui.course.walkreview.component.WalkReviewSingleFilter
import com.paw.key.presentation.ui.course.walkreview.component.WalkSharedReviewHeader
import com.paw.key.presentation.ui.course.walkreview.state.WalkReviewState
import com.paw.key.presentation.ui.course.walkreview.viewmodel.WalkReviewViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun WalkReviewRoute(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit = {},
    navigateHome: () -> Unit = {},
    navigateWalkDetail: (postId: Int, routeId: Int) -> Unit = {_,_->},
    viewModel: WalkReviewViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach {
                viewModel.updateImageList(it)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val limitedUris = uris.take(3)

            limitedUris.forEach {
                viewModel.updateImageList(it)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }


    WalkReviewScreen(
        paddingValues = paddingValues,
        navigateUp = navigateUp,
        navigateHome = navigateHome,
        navigateWalkDetail = { navigateWalkDetail(state.completePostsUiModel.postId, state.completePostsUiModel.routeId) },
        state = state,
        onClickImage = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMultipleMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            } else {
                permissionLauncher.launch(imagePermission)
            }
        },
        onImageDelete = viewModel::deleteImage,
        onFilterClick = viewModel::onFilterClick,
        onTitleValueChange = viewModel::updateReviewTitle,
        onContentValueChange = viewModel::updateReviewContent,
        onClickComplete = viewModel::completeWalkReview,
        onClickSharedComplete = viewModel::completeSharedReview
    )
}

@Composable
private fun WalkReviewScreen(
    paddingValues: PaddingValues,
    state: WalkReviewState,
    navigateUp: () -> Unit = {},
    navigateHome: () -> Unit = {},
    navigateWalkDetail: () -> Unit = {},
    onClickImage: () -> Unit = {},
    onImageDelete: (Int) -> Unit = {},
    onFilterClick: (Int, FilterCategoryUiModel) -> Unit = {_, _ ->},
    onTitleValueChange: (String) -> Unit = {},
    onContentValueChange: (String) -> Unit = {},
    onClickComplete: (Boolean) -> Unit = {},
    onClickSharedComplete: () -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = PawKeyTheme.colors.background
            )
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        TopBar(
            title = "산책 기록하기",
            isBackVisible = true,
            thickness = 2,
            onBackClick = navigateUp
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (state.isShared) {
            WalkSharedReviewHeader(
                item = state.sharedReviewHeader,
            )
        } else {
            WalkReviewImageRow(
                imageList = state.walkReviewImageList,
                onClickCard = { index, _ ->
                    if (index != 0) {
                        onClickImage()
                    }
                },
                onImageDelete = onImageDelete,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            WalkReviewInfoHolder(
                icon = R.drawable.ic_walk_review_location,
                content = state.routeSummary.routeDisplay.locationText
            )

            WalkReviewInfoHolder(
                icon = R.drawable.ic_walk_review_time,
                content = state.routeSummary.routeDisplay.dateTimeText
            )

            WalkReviewInfoHolder(
                icon = R.drawable.ic_walk_review_course_info,
                content = state.routeSummary.routeDisplay.metaTagTexts.joinToString(separator = " | ")
            )

            Spacer(modifier = Modifier.height(40.dp))

            state.filterUiModel.allCategories.forEach { category ->
                val selectedIds = state.getSelectedOptionIds(category)

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
                } else { // MULTI
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

            if (!state.isShared) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "산책에 대한 후기를 작성해주세요",
                    style = PawKeyTheme.typography.subTitle,
                    color = PawKeyTheme.colors.contents
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = state.walkReviewTitle,
                    onValueChange = {
                        if (it.length <= 14) {
                            onTitleValueChange(it)
                        }
                    },
                    textStyle = PawKeyTheme.typography.bodyActive.copy(color = PawKeyTheme.colors.contents),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = PawKeyTheme.colors.defaultBright,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (state.walkReviewTitle.isEmpty()) {
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
                    value = state.walkReviewContent,
                    onValueChange = {
                        if (it.length <= 250) {
                            onContentValueChange(it)
                        }
                    },
                    textStyle = PawKeyTheme.typography.bodyActive.copy(color = PawKeyTheme.colors.contents),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 216.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = PawKeyTheme.colors.defaultBright,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (state.walkReviewContent.isEmpty()) {
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
            }

            Spacer(modifier = Modifier.height(40.dp))

           if (!state.isShared) {
               DokiBorderButton(
                   text = "산책 기록 나만보기",
                   enabled = true,
                   onClick = {
                       onClickComplete(false)
                   }
               )

               Spacer(modifier = Modifier.height(8.dp))

               DokiButton(
                   text = "산책 기록 공유하기",
                   enabled = state.walkReviewTitle.isNotEmpty() && state.walkReviewContent.isNotEmpty(),
                   onClick = {
                       onClickComplete(true)
                   }
               )
           } else {
               DokiButton(
                   text = "산책 후기 남기기",
                   enabled = true,
                   onClick = onClickSharedComplete
               )
           }
        }
    }

    if (state.isComplete) {
        WalkReviewDialog(
            navigateHome = navigateHome,
            navigateWalkDetail = navigateWalkDetail
        )
    }
}

@Preview
@Composable
private fun WalkReviewPreview() {
    PawKeyTheme {
        WalkReviewScreen(
            paddingValues = PaddingValues(),
            state = WalkReviewState(),
        )
    }
}
