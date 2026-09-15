package com.paw.key.data.dto.request.posts

import com.paw.key.domain.entity.posts.CategoryOptionEntity
import com.paw.key.domain.entity.posts.PostsInfoEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostsEditRequestDto(
    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("public")
    val public: Boolean,

    @SerialName("walkImageIds")
    val walkImageIds: List<Int>,

    @SerialName("selectedOptionsForCategories")
    val selectedOptionsForCategories: List<CategoryEditOptionDto>,
)

fun PostsInfoEntity.toEditDto() = PostsEditRequestDto(
    title = title,
    description = description,
    public = isPublic,
    walkImageIds = walkImageIds,
    selectedOptionsForCategories = selectedOptionsForCategories.map { it.toEditDto() },
)

@Serializable
data class CategoryEditOptionDto(
    @SerialName("categoryId")
    val categoryId: Int,

    @SerialName("selectedOptionIds")
    val selectedOptionIds: List<Int>
)

fun CategoryOptionEntity.toEditDto() = CategoryEditOptionDto(
    categoryId = categoryId,
    selectedOptionIds = selectedOptionIds
)

