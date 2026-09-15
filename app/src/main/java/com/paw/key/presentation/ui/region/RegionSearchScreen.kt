package com.paw.key.presentation.ui.region

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paw.key.R
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.PawKeyBottomSheet
import com.paw.key.core.extension.noRippleClickable
import com.paw.key.presentation.ui.region.component.RegionSearchBottomSheetContent
import com.paw.key.presentation.ui.region.model.RegionDistrictModel
import com.paw.key.presentation.ui.region.model.RegionDongModel
import com.paw.key.presentation.ui.region.model.RegionGuModel
import com.paw.key.presentation.ui.signup.component.FormField
import com.paw.key.presentation.ui.signup.component.SignUpTextField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionSearchScreen(
    regionList: ImmutableList<RegionDistrictModel>,
    selectedGu: RegionGuModel,
    selectedDong: RegionDongModel,
    onRegionSelected: (RegionGuModel, RegionDongModel) -> Unit,
    onSaveRegionClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by remember { mutableStateOf(false) }
    val isRegionSelected = selectedGu.name.isNotEmpty() && selectedDong.name.isNotEmpty()

    Column (
        modifier = Modifier
            .padding(
                top = 40.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        FormField(
            label = "선택 지역",
            content = {
                SignUpTextField(
                    value = if (selectedDong.name.isNotEmpty()) "${selectedGu.name} ${selectedDong.name}" else "",
                    onValueChange = {},
                    enabled = false,
                    placeholder = "주로 산책하는 지역을 검색해보세요",
                    suffix = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_signup_search),
                            contentDescription = "region search",
                            tint = Color.Unspecified
                        )
                    },
                    modifier = Modifier.noRippleClickable {
                        scope.launch { isSheetOpen = true }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        DokiButton(
            text = if (isRegionSelected) "저장하기" else "수정하기",
            enabled = true,
            onClick = {
                if (isRegionSelected) {
                    onSaveRegionClick()
                } else {
                    scope.launch { isSheetOpen = true }
                }
            }
        )

        Spacer(modifier = Modifier.height(34.dp))
    }

    if (isSheetOpen) {
        PawKeyBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false },
        ) {
            RegionSearchBottomSheetContent(
                regionList = regionList,
                selectedGu = selectedGu,
                selectedDong = selectedDong,
                onRegionSelected = { gu, dong ->
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            isSheetOpen = false
                            onRegionSelected(gu, dong)
                        }
                    }
                }
            )
        }
    }
}