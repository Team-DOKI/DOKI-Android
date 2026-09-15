package com.paw.key.domain.entity.dbti

data class DbtiResultEntity(
    val type: String,
    val name: String,
    val image: String?,
    val keyword: List<String>,
    val description: String,
    val analysis: List<DbtiAnalysisEntity>
)

data class DbtiAnalysisEntity(
    val axis: String,
    val leftLabel: String,
    val rightLabel: String,
    val dominantSide: String,
    val score: Int
)