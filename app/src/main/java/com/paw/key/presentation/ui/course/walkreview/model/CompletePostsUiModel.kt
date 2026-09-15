package com.paw.key.presentation.ui.course.walkreview.model

import com.paw.key.domain.entity.posts.PostsResultEntity

data class CompletePostsUiModel(
    val postId: Int = -1,
    val routeId: Int = -1,
)

fun PostsResultEntity.toUiModel() = CompletePostsUiModel(
    postId = postId,
    routeId = routeId,
)