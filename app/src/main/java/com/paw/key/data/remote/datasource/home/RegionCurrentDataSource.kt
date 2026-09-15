package com.paw.key.data.remote.datasource.home

import com.paw.key.data.service.home.HomeRegionService
import javax.inject.Inject

class RegionCurrentDataSource @Inject constructor(
    private val service: HomeRegionService
) {
    suspend fun regionCurrent() =
        service.regionCurrent()
}

