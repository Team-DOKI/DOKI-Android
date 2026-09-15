package com.paw.key.domain.repository.home

import com.paw.key.domain.entity.home.HomeInfoEntity
import com.paw.key.domain.entity.home.HomeRouteEntity
import com.paw.key.domain.entity.home.HomeWeatherEntity
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun patchRegion(regionId: Int): Result<Unit>

    suspend fun getHomeInfo(): Result<HomeInfoEntity>

    fun getHomeWeather(): Flow<Result<HomeWeatherEntity>>

    suspend fun getHomeRecommended(): Result<HomeRouteEntity>
}
