package com.paw.key.presentation.ui.detail

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
import com.paw.key.presentation.ui.community.model.FilterCategoryUiModel
import com.paw.key.presentation.ui.community.model.toUiModel
import com.paw.key.presentation.ui.detail.model.DetailViewType
import com.paw.key.presentation.ui.detail.model.toUiModel
import com.paw.key.presentation.ui.detail.navigation.Detail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postsRepository: PostsRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val postId = savedStateHandle.toRoute<Detail>().postId
    private val routeId = savedStateHandle.toRoute<Detail>().routeId

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    private val _editState = MutableStateFlow(DetailEditState())
    val editState = _editState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<DetailSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val routeIdDeferred = CompletableDeferred<Int>()

    init {
        fetchDetail(postId)
    }

    fun fetchDetail(
        postId: Int
    ) {
        viewModelScope.launch {
            postsRepository.getPostsDetail(postId = postId)
                .onSuccess { result ->
                    val uiModel = result.toUiModel()

                    _state.update { currentState ->
                        currentState.copy(
                            postDetail = uiModel,
                        )
                    }
                    routeIdDeferred.complete(uiModel.routeDisplay.routeId)
                    //fetchTopReview(routeId = uiModel.routeDisplay.routeId)
                }
                .onFailure(Timber::e)
        }
    }

    fun fetchTopReview(routeId: Int) {
        Timber.e("fetchTop $routeId")
        viewModelScope.launch {
            postsRepository.getTop3Reviews(routeId = routeId)
                .onSuccess { result ->
                    _state.update { currentState ->
                        currentState.copy(
                            reviewDetail = result.toUiModel(),
                        )
                    }
                }
                .onFailure(Timber::e)
        }
    }

    fun removePosts() {
        viewModelScope.launch {
            postsRepository.deletePosts(postId = postId)
                .onSuccess {
                    Timber.e("삭제 성공")
                    _sideEffect.emit(DetailSideEffect.navigateToCommunity)
                }
                .onFailure {
                    Timber.e(it)
                }
        }
    }

    fun initEditState() {
        val detail = _state.value.postDetail
        viewModelScope.launch {
            // 필터 로드
            postsRepository.getCategoriesFilter()
                .onSuccess { data ->
                    _editState.update { it.copy(filterUiModel = data.toUiModel()) }
                }
                .onFailure(Timber::e)

            // 기존 데이터로 초기화
            _editState.update { currentEdit ->
                currentEdit.copy(
                    postId = detail.postId,
                    routeId = detail.routeDisplay.routeId,
                    title = detail.title,
                    content = detail.description,
                    isPublic = detail.isPublic,
                    existingImageUrls = detail.walkImages.toPersistentList(),
                    locationText = detail.routeDisplay.locationText,
                    dateTimeText = detail.routeDisplay.dateTimeText,
                    metaTagTexts = detail.routeDisplay.metaTagTexts
                )
            }
        }
    }

    fun editPosts() {
        initEditState()
        _state.update {
            it.copy(
                viewType = DetailViewType.EDIT
            )
        }
    }

    fun updateEditTitle(title: String) {
        _editState.update { it.copy(title = title.take(14)) }
    }

    fun updateEditContent(content: String) {
        _editState.update { it.copy(content = content.take(250)) }
    }

    fun addEditImage(uri: Uri) {
        _editState.update { it.copy(newImageUris = it.newImageUris.add(uri)) }
    }

    fun deleteEditImage(index: Int) {
        val existingSize = _editState.value.existingImageUrls.size
        if (index < existingSize) {
            // 기존 이미지 삭제
            _editState.update {
                it.copy(existingImageUrls = it.existingImageUrls.removeAt(index))
            }
        } else {
            // 새로 추가한 이미지 삭제
            val newIndex = index - existingSize
            _editState.update { it.copy(newImageUris = it.newImageUris.removeAt(newIndex)) }
        }
    }

    fun onEditFilterClick(optionId: Int, category: FilterCategoryUiModel) {
        _editState.update { it.copy(selectedOptionIds = it.getUpdatedOptionIds(optionId, category)) }
    }

    fun submitEdit(isPublic: Boolean) {
        val currentEdit = _editState.value
        viewModelScope.launch {
            try {
                // 새 이미지 업로드
                val newImageIds = currentEdit.newImageUris.map { uri ->
                    val presignedResult = imageRepository.presignedImage(
                        ImagePresignedEntity(
                            domain = ImageDomainType.WALK,
                            contentType = "image/webp"
                        )
                    ).getOrElse { throw Exception("Presigned 실패") }

                    imageRepository.uploadS3(
                        presignedUrl = presignedResult.uploadUrl,
                        uriString = uri.toString()
                    ).getOrElse { throw Exception("S3 업로드 실패") }

                    val registerResult = imageRepository.registerImage(
                        uriString = "${presignedResult.imageUrl}#${uri}",
                        domainType = ImageDomainType.WALK
                    ).getOrElse { throw Exception("이미지 등록 실패") }

                    registerResult.imageId
                }

                // 기존 이미지 ID + 새 이미지 ID
                val existingImageIds = currentEdit.existingImageUrls.map { it.imageId }
                val allImageIds = existingImageIds + newImageIds

                val categoryOptions = currentEdit.selectedOptionIds.map { (categoryId, optionIds) ->
                    CategoryOptionEntity(categoryId = categoryId, selectedOptionIds = optionIds)
                }

                postsRepository.editPosts(
                    postId = currentEdit.postId,
                    postsInfo = PostsInfoEntity(
                        title = currentEdit.title,
                        description = currentEdit.content,
                        isPublic = isPublic,
                        routeId = currentEdit.routeId,
                        routeImageId = currentEdit.routeImageId,
                        walkImageIds = allImageIds,
                        selectedOptionsForCategories = categoryOptions,
                        imageUrls = allImageIds
                    )
                ).onSuccess {
                    _editState.update { it.copy(isComplete = true) }
                    _sideEffect.emit(DetailSideEffect.navigateToCommunity)
                }.onFailure(Timber::e)

            } catch (e: Exception) {
                Timber.e(e, "수정 실패")
            }
        }
    }
}