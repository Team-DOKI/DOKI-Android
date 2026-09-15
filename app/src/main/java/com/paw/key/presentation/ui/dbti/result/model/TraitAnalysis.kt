package com.paw.key.presentation.ui.dbti.result.model

import com.paw.key.domain.entity.dbti.DbtiAnalysisEntity

// TraitAnalysis 데이터 클래스 정의
data class TraitAnalysis(
    val leftLabel: String,
    val rightLabel: String,
    val dominantSide: String,
    val score: Int
)

fun DbtiAnalysisEntity.toUiModel() = TraitAnalysis(
    leftLabel = leftLabel,
    rightLabel = rightLabel,
    dominantSide = dominantSide,
    score = score
)