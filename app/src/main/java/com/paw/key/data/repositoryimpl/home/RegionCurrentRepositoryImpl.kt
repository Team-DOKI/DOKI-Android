package com.paw.key.data.repositoryimpl.home

import com.paw.key.data.remote.datasource.home.RegionCurrentDataSource
import com.paw.key.domain.entity.home.RegionCurrentDataEntity
import com.paw.key.domain.repository.home.RegionCurrentRepository
import javax.inject.Inject

class RegionCurrentRepositoryImpl @Inject constructor(
    private val dataSource: RegionCurrentDataSource,
) : RegionCurrentRepository {

    override suspend fun regionCurrent(): Result<RegionCurrentDataEntity> {
        return runCatching {
            val response = dataSource.regionCurrent()
            if (response.code == "S000") {
                response.data.toEntity()
            } else {
                throw Exception(response.message)
            }
        }
    }
}