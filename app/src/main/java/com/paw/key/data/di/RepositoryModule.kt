package com.paw.key.data.di

import com.paw.key.data.remote.datasource.datasourceimpl.AuthRemoteDataSourceImpl
import com.paw.key.data.remote.datasource.datasourceimpl.GoogleAuthDataSourceImpl
import com.paw.key.data.remote.datasource.datasourceimpl.KakaoAuthDataSourceImpl
import com.paw.key.data.remote.datasource.login.AuthRemoteDataSource
import com.paw.key.data.remote.datasource.login.GoogleAuthDataSource
import com.paw.key.data.remote.datasource.login.KakaoAuthDataSource
import com.paw.key.data.remote.datasource.mypage.MypageDataSource
import com.paw.key.data.remote.datasource.datasourceimpl.MypageDataSourceImpl
import com.paw.key.data.repository.mypage.MypageRepositoryImpl
import com.paw.key.data.repositoryimpl.ArchivedListRepositoryImpl
import com.paw.key.data.repositoryimpl.DBTI.DbtiRepositoryImpl
import com.paw.key.data.repositoryimpl.RegionRepositoryImpl
import com.paw.key.data.repositoryimpl.WalkSharedResultRepositoryImpl
import com.paw.key.data.repositoryimpl.home.HomeRepositoryImpl
import com.paw.key.data.repositoryimpl.home.RegionCurrentRepositoryImpl
import com.paw.key.data.repositoryimpl.image.ImageRepositoryImpl
import com.paw.key.data.repositoryimpl.localstorage.LocalStorageRepositoryImpl
import com.paw.key.data.repositoryimpl.login.AuthRepositoryImpl
import com.paw.key.data.repositoryimpl.posts.PostsRepositoryImpl
import com.paw.key.data.repositoryimpl.reviews.ReviewsRepositoryImpl
import com.paw.key.data.repositoryimpl.user.UserRepositoryImpl
import com.paw.key.data.repositoryimpl.walk.WalkRepositoryImpl
import com.paw.key.data.repositoryimpl.walkpreparation.WalkPreparationRepositoryImpl
import com.paw.key.domain.repository.ArchivedListRepository
import com.paw.key.domain.repository.DBTI.DbtiRepository
import com.paw.key.domain.repository.RegionRepository
import com.paw.key.domain.repository.WalkSharedResultRepository
import com.paw.key.domain.repository.home.HomeRepository
import com.paw.key.domain.repository.home.RegionCurrentRepository
import com.paw.key.domain.repository.image.ImageRepository
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.repository.login.AuthRepository
import com.paw.key.domain.repository.mypage.MypageRepository
import com.paw.key.domain.repository.posts.PostsRepository
import com.paw.key.domain.repository.reviews.ReviewsRepository
import com.paw.key.domain.repository.user.UserRepository
import com.paw.key.domain.repository.walk.WalkRepository
import com.paw.key.domain.repository.walkpreparation.WalkPreparationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl,
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    fun bindGoogleAuthDataSource(
        impl: GoogleAuthDataSourceImpl,
    ): GoogleAuthDataSource

    @Binds
    @Singleton
    fun bindMypageDataSource(
        impl: MypageDataSourceImpl
    ): MypageDataSource

    @Binds
    abstract fun bindKakaoAuthDataSource(
        impl: KakaoAuthDataSourceImpl
    ): KakaoAuthDataSource

    @Binds
    @Singleton
    fun bindsSharedWalkResultRepository(
        walkSharedResultRepositoryImpl: WalkSharedResultRepositoryImpl
    ): WalkSharedResultRepository

    /*Home*/
    @Binds
    @Singleton
    fun bindsRegionRepository(
        regionRepositoryImpl: RegionRepositoryImpl
    ): RegionRepository

    @Binds
    @Singleton
    fun bindsUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    fun bindArchivedListRepository(
        impl: ArchivedListRepositoryImpl
    ): ArchivedListRepository

    //게시물 리스트
    @Binds
    @Singleton
    fun bindPostsRepository(
        impl: PostsRepositoryImpl
    ) : PostsRepository

    @Binds
    @Singleton
    fun bindRegionCurrentRepository(
        impl: RegionCurrentRepositoryImpl
    ) : RegionCurrentRepository

    @Binds
    @Singleton
    fun bindLoginRepository(
        impl: AuthRepositoryImpl
    ) : AuthRepository

    @Binds
    @Singleton
    fun bindImageRepository(
        impl: ImageRepositoryImpl
    ) : ImageRepository

    @Binds
    @Singleton
    fun bindLocalStorageRepository(
        impl: LocalStorageRepositoryImpl
    ): LocalStorageRepository

    @Binds
    @Singleton
    fun bindMypageRepository(
        impl: MypageRepositoryImpl
    ): MypageRepository

    @Binds
    @Singleton
    fun bindWalkListRepository(
        impl: WalkPreparationRepositoryImpl
    ) : WalkPreparationRepository

    @Binds
    @Singleton
    fun bindWalkRepository(
        impl: WalkRepositoryImpl
    ) : WalkRepository


    //DBTI
    @Binds
    @Singleton
    fun bindDbtiRepository(
        dbtiRepositoryImpl: DbtiRepositoryImpl
    ): DbtiRepository

    @Binds
    @Singleton
    fun bindReviewRepository(
        impl: ReviewsRepositoryImpl
    ) : ReviewsRepository


}
