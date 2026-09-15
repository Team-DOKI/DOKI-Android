package com.paw.key.domain.entity.dbti

data class DbtiQuestionEntity(
    val id: Int,
    val categoryCode: String,
    val categoryName: String,
    val content: String,
    val options: List<DbtiOptionEntity>
)

data class DbtiOptionEntity(
    val id: Int,
    val content: String,
    val imageUrl: String?,
    val value: String
)