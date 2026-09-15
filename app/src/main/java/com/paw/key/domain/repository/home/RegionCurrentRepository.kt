package com.paw.key.domain.repository.home

import com.paw.key.domain.entity.home.RegionCurrentDataEntity

interface RegionCurrentRepository {
    suspend fun regionCurrent(): Result<RegionCurrentDataEntity>
}