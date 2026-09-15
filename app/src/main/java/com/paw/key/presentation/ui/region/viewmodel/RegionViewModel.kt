package com.paw.key.presentation.ui.region.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.core.util.UiState
import com.paw.key.core.util.flattenCoordinatesToLatLng
import com.paw.key.core.util.handleError
import com.paw.key.domain.repository.RegionRepository
import com.paw.key.domain.repository.home.HomeRepository
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.presentation.ui.region.model.RegionDongModel
import com.paw.key.presentation.ui.region.model.RegionGuModel
import com.paw.key.presentation.ui.region.model.RegionStep
import com.paw.key.presentation.ui.region.model.toState
import com.paw.key.presentation.ui.region.state.DrawType
import com.paw.key.presentation.ui.region.state.RegionSideEffect
import com.paw.key.presentation.ui.region.state.RegionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val regionRepository: RegionRepository,
    private val homeRepository: HomeRepository,
    private val localStorageRepository: LocalStorageRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegionState())
    val state: StateFlow<RegionState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<RegionSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        fetchRegionList()
    }

    private fun fetchRegionList() = viewModelScope.launch {
        regionRepository.getRegionList()
            .onSuccess { data ->
                _state.update { currentState ->
                    currentState.copy(
                        regionList = data.map { it.toState() }.toImmutableList(),
                    )
                }
            }
            .onFailure(Timber::e)
    }

    fun getRegionGeometry(regionId: Int?) = viewModelScope.launch {
        regionRepository.getRegionGeometry(regionId!!)
            .onSuccess { data ->
                val coordinates = data.geometry.coordinates
                val flattenedLatLng = flattenCoordinatesToLatLng(coordinates)

                if (flattenedLatLng.isEmpty() || flattenedLatLng.first().isEmpty()) {
                    _state.update {
                        it.copy(uiState = UiState.Failure("좌표 데이터가 올바르지 않습니다"))
                    }
                    return@launch
                }

                val allPoints = flattenedLatLng.flatten().toPersistentList()

                if (flattenedLatLng.size == 1) {
                    // 폴리곤이 하나일 경우
                    _state.update {
                        it.copy(
                            uiState = UiState.Success(flattenedLatLng),
                            entireCoordinates = allPoints,
                            drawType = DrawType.SINGLE,
                        )
                    }
                } else {
                    // 폴리곤이 여러 개일 경우
                    _state.update {
                        it.copy(
                            uiState = UiState.Success(flattenedLatLng),
                            entireCoordinates = allPoints,
                            drawType = DrawType.MULTIPLE,
                        )
                    }
                }
            }
            .onFailure { throwable ->
                val errorMessage = handleError(throwable)
                _state.update {
                    it.copy(
                        uiState = UiState.Failure(errorMessage)
                    )
                }
            }
    }
    fun confirmRegionOnMap() {
        _state.update { it.copy(currentStep = RegionStep.SEARCH) }
    }
    fun patchRegion() {
        Timber.e("patchRegion 1")
        viewModelScope.launch {
            homeRepository.patchRegion(_state.value.selectedDong.id)
                .onSuccess { data ->
                    Timber.e("patchRegion")
                    _sideEffect.emit(RegionSideEffect.NavigateNext)
                }
                .onFailure { throwable ->
                    Timber.e("patchRegion $throwable")
                    val errorMessage = handleError(throwable)
                    _sideEffect.emit(
                        RegionSideEffect.ShowSnackBar(errorMessage)
                    )
                }
        }
    }

    // 구/동을 선택했을 때 호출되는 함수
    fun onRegionSelected(gu: RegionGuModel, dong: RegionDongModel) {
        _state.update {
            it.copy(
                selectedGu = gu,
                selectedDong = dong,
                currentStep = RegionStep.MAP
            )
        }
        getRegionGeometry(dong.id)
    }

    fun onBackPressedToSearch() {
        _state.update { it.copy(currentStep = RegionStep.SEARCH) }
    }

    fun onBackPressed() {
        when (_state.value.currentStep) {
            RegionStep.MAP -> onBackPressedToSearch()
            RegionStep.SEARCH -> viewModelScope.launch {
                _sideEffect.emit(RegionSideEffect.NavigateUp)
            }
        }
    }
}
