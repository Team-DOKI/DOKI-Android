package com.paw.key.data.remote.datasource.posts

import com.paw.key.data.dto.request.posts.PostsDataRequestDto
import com.paw.key.data.dto.request.posts.PostsEditRequestDto
import com.paw.key.data.dto.request.posts.PostsFilterRequestDto
import com.paw.key.data.service.posts.PostsService
import javax.inject.Inject

class PostsDataSource @Inject constructor(
    private val service: PostsService
) {
    suspend fun postPosts(request: PostsDataRequestDto) = service.postPosts(request)

    suspend fun editPosts(postId: Int, request: PostsEditRequestDto) = service.patchPosts(postId, request)

    suspend fun deletePosts(postId: Int) = service.deletePosts(postId)

    suspend fun getCategories() = service.getCategories()

    suspend fun getPostsDetail(postId: Int) = service.getPostsDetail(postId)

    suspend fun getTop3Reviews(routeId: Int) = service.getTop3Reviews(routeId)

    suspend fun getPostsFilter(
        sortBy: String = "latest",
        cursor: String? = null,
        size: Int = 10,
        request: PostsFilterRequestDto
    ) = service.getPostsFilter(
        sortBy = sortBy,
        cursor = cursor,
        size = size,
        request = request
    )

    suspend fun getCategoriesFilter() = service.getCategoriesFilter()

    suspend fun postLike(postId: Int) = service.postLike(postId)

    suspend fun getRouteSummary(routeId: Int) = service.getRouteSummary(routeId)
}
