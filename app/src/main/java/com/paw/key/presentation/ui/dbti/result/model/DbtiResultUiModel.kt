package com.paw.key.presentation.ui.dbti.result.model

import com.paw.key.domain.entity.dbti.DbtiResultEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class DbtiResultUiModel(
    val type: String = "",
    val name: String = "",
    val imageUrl: String? = "",
    val keywords: ImmutableList<String> = persistentListOf(),
    val description: String = "",
    val analysis: ImmutableList<TraitAnalysis> = persistentListOf(),
)

fun DbtiResultEntity.toUiModel() = DbtiResultUiModel(
    type = type,
    name = name,
    imageUrl = image,
    keywords = keyword.toImmutableList(),
    description = description,
    analysis = analysis.map { it.toUiModel() }.toImmutableList(),
)
