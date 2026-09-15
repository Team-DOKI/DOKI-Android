package com.paw.key.data.remote.datasource.DBTI

import com.paw.key.data.dto.request.dbti.DbtiResultRequest
import com.paw.key.data.dto.response.dbti.DbtiQuestionsResponse
import com.paw.key.data.dto.response.dbti.DbtiResultResponse
import com.paw.key.data.service.DBTI.DbtiService
import javax.inject.Inject

class DbtiDataSource @Inject constructor(
    private val dbtiService: DbtiService
) {
    suspend fun getQuestions(): DbtiQuestionsResponse {
        return dbtiService.getQuestions()
    }

    suspend fun submitResult(
        petId: Long,
        optionIds: List<Int>
    ): DbtiResultResponse {
        return dbtiService.submitResult(
            petId = petId,
            request = DbtiResultRequest(optionIds = optionIds)
        )
    }

    suspend fun getResult(
        petId: Long,
    ): DbtiResultResponse {
        return dbtiService.getResult(
            petId = petId,
        )
    }
}