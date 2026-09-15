package com.paw.key.presentation.ui.course.walkreview.state

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.paw.key.presentation.ui.community.model.FilterCategoryUiModel
import com.paw.key.presentation.ui.community.model.PostsFilterUiModel
import com.paw.key.presentation.ui.community.model.SelectionType
import com.paw.key.presentation.ui.course.walkreview.model.CompletePostsUiModel
import com.paw.key.presentation.ui.course.walkreview.model.WalkReviewRouteSummaryUiModel
import com.paw.key.presentation.ui.course.walkreview.model.WalkSharedReviewUiModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class WalkReviewState(
    val routeSummary: WalkReviewRouteSummaryUiModel = WalkReviewRouteSummaryUiModel(),

    val walkReviewImageList: PersistentList<Uri> = persistentListOf(),

    val filterUiModel: PostsFilterUiModel = PostsFilterUiModel(), // 서버에서 받아온 필터 목록
    val selectedOptionIds: PersistentMap<Int, PersistentList<Int>> = persistentMapOf(), // 선택된 옵션 ID들 {카테고리ID: [옵션ID...]}
    val walkReviewTitle: String = "",
    val walkReviewContent: String = "",

    val isComplete: Boolean = false,
    val completePostsUiModel : CompletePostsUiModel = CompletePostsUiModel(),
    val isShared: Boolean = false,
    val sharedReviewHeader: WalkSharedReviewUiModel = WalkSharedReviewUiModel()
) {
    // 특정 카테고리에서 선택된 ID 리스트 가져오기
    fun getSelectedOptionIds(category: FilterCategoryUiModel): PersistentList<Int> {
        return selectedOptionIds[category.id] ?: persistentListOf()
    }

    // 필터 클릭 시 업데이트된 Map 반환
    fun getUpdatedOptionIds(
        optionId: Int,
        category: FilterCategoryUiModel
    ): PersistentMap<Int, PersistentList<Int>> {
        return if (category.selectionType == SelectionType.SINGLE) {
            selectedOptionIds.put(category.id, persistentListOf(optionId))
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
