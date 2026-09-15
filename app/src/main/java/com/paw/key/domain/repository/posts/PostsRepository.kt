package com.paw.key.domain.repository.posts

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

interface PostsRepository {
    suspend fun postPosts(postsInfo: PostsInfoEntity): Result<PostsResultEntity> // 게시물 등록

    suspend fun deletePosts(postId: Int) : Result<Unit>// 게시물 삭제

    suspend fun editPosts(
        postId: Int,
        postsInfo: PostsInfoEntity
    ): Result<PostsResultEntity> // 게시물 수정

    suspend fun getPostsDetail(postId: Int): Result<PostsDetailEntity> // 상세 조회

    suspend fun getPostsFilter( // route 리스트 조회
        sortBy: String = "latest", // popular
        cursor: String? = null,
        size: Int = 10,
        postsFilter : FilterSelectedItemEntity
    ): Result<PostsEntity>

    /** 커뮤니티 화면에서 필터링 카테고리 조회 */
    suspend fun getCategoriesFilter(): Result<PostsFilterEntity>

    suspend fun postLike(postId: Int): Result<LikeEntity>

    suspend fun getTop3Reviews(routeId: Int): Result<PostsTop3Entity>

    suspend fun getCategories(): Result<PostsCategoryEntity> // 사용자가 게시물 작성할 때 사용

    suspend fun getPostSummary(routeId: Int): Result<PostRouteSummaryEntity>
}
