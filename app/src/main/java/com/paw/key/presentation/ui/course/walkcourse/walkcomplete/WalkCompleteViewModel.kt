package com.paw.key.presentation.ui.course.walkcourse.walkcomplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.paw.key.domain.repository.walk.WalkRepository
import com.paw.key.domain.usecase.walk.GetWalkFinishResultUseCase
import com.paw.key.domain.usecase.walk.GetWalkInfoUseCase
import com.paw.key.presentation.ui.course.navigation.WalkComplete
import com.paw.key.presentation.ui.course.walkcourse.walkcomplete.model.toUiModel
import com.paw.key.presentation.ui.course.walkcourse.walkcomplete.state.WalkCompleteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WalkCompleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val walkCompleteRepository: WalkRepository,
    private val finishResultUseCase: GetWalkFinishResultUseCase,
    private val finishWalkInfoUseCase: GetWalkInfoUseCase
) : ViewModel() {
    private val routeId = savedStateHandle.toRoute<WalkComplete>().routeId
    private val routeImageId = savedStateHandle.toRoute<WalkComplete>().routeImageId

    private val _state = MutableStateFlow(WalkCompleteState())
    val state: StateFlow<WalkCompleteState> = _state.asStateFlow()

    init {
        // 좌표값
        fetchWalkComplete()
        // 유저 정보 및 산책 정보
        fetchWalkCompleteData()
    }

    private fun fetchWalkCompleteData() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    routeImageId = routeImageId
                )
            }

            launch {
                finishResultUseCase().collect { entity ->
                    Timber.e("finishResult $entity")
                    entity?.let {
                        _state.update { currentState ->
                            currentState.copy(
                                walkCompleteUserInfo = it.toUiModel()
                            )
                        }
                    }
                }
            }

            launch {
                finishWalkInfoUseCase().collect { walkFinish ->
                    Timber.e("finishWalk $walkFinish")
                    walkFinish?.let {
                        _state.update { currentState ->
                            currentState.copy(
                                walkCompleteFinishInfo = it.toUiModel()
                            )
                        }
                    }
                }
            }
        }
    }

    fun fetchWalkComplete() {
        viewModelScope.launch {
            Timber.e("fetchWalkComplete $routeId")
            walkCompleteRepository.completeWalk(routeId)
                .onSuccess { result ->
                    Timber.e("complete $result")
                    _state.update {
                        it.copy(
                            walkCompleteMapInfo = result.geometry.toUiModel(),
                        )
                    }
                }
                .onFailure {
                    Timber.e("complete $it")
                }
        }
    }

}
