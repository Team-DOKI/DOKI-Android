package com.paw.key.presentation.ui.course.walkreview.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.paw.key.domain.entity.image.ImageDomainType
import com.paw.key.domain.entity.image.ImagePresignedEntity
import com.paw.key.domain.entity.posts.CategoryOptionEntity
import com.paw.key.domain.entity.posts.PostsInfoEntity
import com.paw.key.domain.repository.image.ImageRepository
import com.paw.key.domain.repository.posts.PostsRepository
import com.paw.key.domain.repository.reviews.ReviewsRepository
import com.paw.key.presentation.ui.community.model.FilterCategoryUiModel
import com.paw.key.presentation.ui.community.model.toUiModel
import com.paw.key.presentation.ui.course.walkreview.model.SelectedReviewSetUiModel
import com.paw.key.presentation.ui.course.walkreview.model.toEntity
import com.paw.key.presentation.ui.course.walkreview.model.toUiModel
import com.paw.key.presentation.ui.course.walkreview.navigation.WalkReview
import com.paw.key.presentation.ui.course.walkreview.state.WalkReviewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WalkReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postsRepository: PostsRepository,
    private val imageRepository: ImageRepository,
    private val reviewsRepository: ReviewsRepository,
) : ViewModel() {
    private val routeId = savedStateHandle.toRoute<WalkReview>().routeId
    private val routeImageId = savedStateHandle.toRoute<WalkReview>().routeImageId

    private val isShared = savedStateHandle.toRoute<WalkReview>().isShared
    private val postId = savedStateHandle.toRoute<WalkReview>().postId
    private val userId = savedStateHandle.toRoute<WalkReview>().userId

    private val _state = MutableStateFlow(
        WalkReviewState(
            isShared = this.isShared
        )
    )
    val state = _state.asStateFlow()

    init {
        fetchPostsFilter()

        if (routeId != null) {
            fetchRouteSummary(routeId)
        }

        if (isShared) {
            fetchReviews()
        }
    }

    private fun fetchPostsFilter() {
        viewModelScope.launch {
            postsRepository.getCategoriesFilter()
                .onSuccess { data ->
                    _state.update { it.copy(filterUiModel = data.toUiModel()) }
                }
                .onFailure(Timber::e)
        }
    }

    fun fetchRouteSummary(routeId: Int) {
        viewModelScope.launch {
            postsRepository.getPostSummary(routeId)
                .onSuccess { result ->
                    _state.update { currentState ->
                        currentState.copy(
                            routeSummary = result.toUiModel()
                        )
                    }
                }
                .onFailure(Timber::e)
        }
    }

    fun updateImageList(uri : Uri) {
        _state.update {
            it.copy(
                walkReviewImageList = it.walkReviewImageList.add(uri)
            )
        }
    }

    fun deleteImage(index : Int) {
        _state.update {
            it.copy(
                walkReviewImageList = it.walkReviewImageList.removeAt(index)
            )
        }
    }

    fun updateReviewTitle(title: String) {
        val limitedTitle = title.take(14)

        _state.update {
            it.copy(walkReviewTitle = limitedTitle)
        }
    }

    fun updateReviewContent(content : String) {
        val limitedContent = content.take(250)

        _state.update {
            it.copy(
                walkReviewContent = limitedContent
            )
        }
    }
    fun completeWalkReview(isPublic: Boolean) {
        val currentState = _state.value
        val currentRouteId = routeId ?: return

        viewModelScope.launch {
            try {
                val uploadedWalkImageIds = mutableListOf<Int>()

                currentState.walkReviewImageList.forEach { uri ->
                    val localUriString = uri.toString()

                    val presignedResult = imageRepository.presignedImage(
                        ImagePresignedEntity(
                            domain = ImageDomainType.WALK,
                            contentType = "image/webp"
                        )
                    ).getOrElse { throw Exception("Presigned URL 발급 실패") }

                    imageRepository.uploadS3(
                        presignedUrl = presignedResult.uploadUrl,
                        uriString = localUriString
                    ).getOrElse { throw Exception("S3 업로드 실패") }

                    val registerParam = "${presignedResult.imageUrl}#${localUriString}"
                    val registerResult = imageRepository.registerImage(
                        uriString = registerParam,
                        domainType = ImageDomainType.WALK
                    ).getOrElse { throw Exception("서버에 이미지 정보 등록 실패") }

                    uploadedWalkImageIds.add(registerResult.imageId)
                }

                val categoryOptions = currentState.selectedOptionIds.map { (categoryId, optionIds) ->
                    CategoryOptionEntity(
                        categoryId = categoryId,
                        selectedOptionIds = optionIds
                    )
                }

                val postsInfo = PostsInfoEntity(
                    title = currentState.walkReviewTitle,
                    description = currentState.walkReviewContent,
                    isPublic = isPublic,
                    routeId = currentRouteId,
                    routeImageId = routeImageId ?: 3,
                    walkImageIds = uploadedWalkImageIds,
                    selectedOptionsForCategories = categoryOptions,
                    imageUrls = uploadedWalkImageIds
                )

                postsRepository.postPosts(postsInfo).onSuccess { result ->
                    Timber.d("게시물 등록 성공")
                    _state.update {
                        it.copy(
                            isComplete = true,
                            completePostsUiModel = result.toUiModel()
                        )
                    }
                }.onFailure { e ->
                    Timber.e(e, "게시물 등록 실패 (API 오류)")
                }

            } catch (e: Exception) {
                Timber.e(e, "이미지 업로드 및 게시물 등록 전체 프로세스 중 에러 발생")
            }
        }
    }

    fun onFilterClick(optionId: Int, category: FilterCategoryUiModel) {
        _state.update { currentState ->
            val updatedOptionIdsMap = currentState.getUpdatedOptionIds(optionId, category)

            if (currentState.isShared) {
                val updatedSelectedSets = updatedOptionIdsMap.map { (catId, optIds) ->
                    SelectedReviewSetUiModel(
                        reviewCategoryId = catId,
                        selectedReviewOptionIds = optIds
                    )
                }

                currentState.copy(
                    selectedOptionIds = updatedOptionIdsMap, // UI용 유지
                    sharedReviewHeader = currentState.sharedReviewHeader.copy(
                        selectedReviewSets = updatedSelectedSets // 전송용 동기화
                    )
                )
            } else {
                // 공유 상태가 아니라면 Map만 업데이트
                currentState.copy(
                    selectedOptionIds = updatedOptionIdsMap
                )
            }
        }
    }


    fun fetchReviews() {
        viewModelScope.launch {
            reviewsRepository.getReviewHeader(postId = postId ?: -1)
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            sharedReviewHeader = result.toUiModel(
                                currentRouteId = routeId ?: -1,
                            )
                        )
                    }
                }
                .onFailure(Timber::e)
        }
    }

    fun completeSharedReview() {
        val currentState = _state.value
        viewModelScope.launch {
            try {
                reviewsRepository.postReview(currentState.sharedReviewHeader.toEntity(), userId ?: -1)
                    .onSuccess {
                        Timber.d("공유 리뷰 등록 성공")
                        _state.update {
                            it.copy(isComplete = true)
                        }
                    }.onFailure { e ->
                        Timber.e(e, "공유 리뷰 등록 실패 (API 오류)")
                    }

            } catch (e: Exception) {
                Timber.e(e, "공유 리뷰 등록 전체 프로세스 중 에러 발생")
            }
        }
    }
}