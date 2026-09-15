package com.paw.key.presentation.ui.detail

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.paw.key.presentation.ui.community.model.FilterCategoryUiModel
import com.paw.key.presentation.ui.community.model.PostsFilterUiModel
import com.paw.key.presentation.ui.community.model.SelectionType
import com.paw.key.presentation.ui.detail.model.DetailViewType
import com.paw.key.presentation.ui.detail.model.PostsDetailUiModel
import com.paw.key.presentation.ui.detail.model.ReviewUiModel
import com.paw.key.presentation.ui.detail.model.WalkImageUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class DetailState(
    val viewType: DetailViewType = DetailViewType.DETAIL,
    val postDetail: PostsDetailUiModel = PostsDetailUiModel(),
    val reviewDetail: ReviewUiModel = ReviewUiModel()
)

@Immutable
data class DetailEditState(
    val postId: Int = -1,
    val routeId: Int = -1,
    val routeImageId: Int = -1,

    // 기존 게시글 데이터로 초기화
    val title: String = "",
    val content: String = "",
    val isPublic: Boolean = false,

    // 이미지 - 기존 이미지 URL + 새로 추가한 URI
    val existingImageUrls: PersistentList<WalkImageUiModel> = persistentListOf(),
    val newImageUris: PersistentList<Uri> = persistentListOf(),

    // 필터
    val filterUiModel: PostsFilterUiModel = PostsFilterUiModel(),
    val selectedOptionIds: PersistentMap<Int, PersistentList<Int>> = persistentMapOf(),

    // 완료 여부
    val isComplete: Boolean = false,

    val locationText: String = "",
    val dateTimeText: String = "",
    val metaTagTexts: ImmutableList<String> = persistentListOf()
) {
    fun getSelectedOptionIds(category: FilterCategoryUiModel): PersistentList<Int> {
        return selectedOptionIds[category.id] ?: persistentListOf()
    }

    fun getUpdatedOptionIds(
        optionId: Int,
        category: FilterCategoryUiModel
    ): PersistentMap<Int, PersistentList<Int>> {
        return if (category.selectionType == SelectionType.SINGLE) {
            val categoryOptionIds = category.options.map { it.id }
            val clearedMap = selectedOptionIds.builder().apply {
                categoryOptionIds.forEach { remove(it) }
            }.build()
            clearedMap.put(category.id, persistentListOf(optionId))
        } else {
            val current = selectedOptionIds[category.id] ?: persistentListOf()
            val updated = if (current.contains(optionId)) {
                current.remove(optionId)
            } else {
                current.add(optionId)
            }
            if (updated.isEmpty()) {
                selectedOptionIds.remove(category.id)
            } else {
                selectedOptionIds.put(category.id, updated)
            }
        }
    }
}

sealed interface DetailSideEffect {
    data object navigateToCommunity: DetailSideEffect
}