package com.paw.key.data.repositoryimpl.posts


import com.paw.key.core.util.suspendRunCatching
import com.paw.key.data.dto.request.posts.toDto
import com.paw.key.data.dto.request.posts.toEditDto
import com.paw.key.data.remote.datasource.posts.PostsDataSource
import com.paw.key.domain.entity.posts.FilterSelectedItemEntity
import com.paw.key.domain.entity.posts.LikeEntity
import com.paw.key.domain.entity.posts.PostRouteSummaryEntity
import com.paw.key.domain.entity.posts.PostsCategoryEntity
import com.paw.key.domain.entity.posts.PostsDetailEntity
import com.paw.key.domain.entity.posts.PostsEntity
import com.paw.key.domain.entity.posts.PostsFilterEntity
import com.paw.key.domain.entity.posts.PostsInfoEntity
import com.paw.key.domain.entity.posts.PostsResultEntity
import com.paw.key.domain.entity.posts.PostsTop3Entity
import com.paw.key.domain.repository.posts.PostsRepository
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val dataSource: PostsDataSource,
) : PostsRepository {
    override suspend fun postPosts(
        postsInfo: PostsInfoEntity
    ): Result<PostsResultEntity> = suspendRunCatching{
        dataSource.postPosts(postsInfo.toDto()).data.toEntity()
    }

    override suspend fun editPosts(
        postId: Int,
        postsInfo: PostsInfoEntity
    ) : Result<PostsResultEntity> = suspendRunCatching{
        dataSource.editPosts(postId, postsInfo.toEditDto()).data.toEntity()
    }

    override suspend fun deletePosts(postId: Int) : Result<Unit> = suspendRunCatching{
        dataSource.deletePosts(postId)
    }

    override suspend fun getPostsDetail(postId: Int): Result<PostsDetailEntity> = suspendRunCatching {
        dataSource.getPostsDetail(postId).data.toEntity()
    }

    override suspend fun getPostsFilter(
        sortBy: String,
        cursor: String?,
        size: Int,
        postsFilter: FilterSelectedItemEntity
    ): Result<PostsEntity> = suspendRunCatching {
        dataSource.getPostsFilter(
            sortBy = sortBy,
            cursor = cursor,
            size = size,
            request = postsFilter.toDto()
        ).data.toEntity()
    }

    override suspend fun getCategoriesFilter(): Result<PostsFilterEntity> = suspendRunCatching {
        dataSource.getCategoriesFilter().data.toEntity()
    }

    override suspend fun postLike(postId: Int): Result<LikeEntity> = suspendRunCatching {
        dataSource.postLike(postId).data.toEntity()
    }

    override suspend fun getTop3Reviews(routeId: Int): Result<PostsTop3Entity> = suspendRunCatching {
        dataSource.getTop3Reviews(routeId).data.toEntity()
    }

    override suspend fun getCategories(): Result<PostsCategoryEntity> = suspendRunCatching {
        dataSource.getCategories().data.toEntity()
    }

    override suspend fun getPostSummary(routeId: Int): Result<PostRouteSummaryEntity> = suspendRunCatching{
        dataSource.getRouteSummary(routeId).data.toEntity()
    }
}
