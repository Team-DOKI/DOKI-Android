package com.paw.key.domain.usecase

import com.paw.key.domain.entity.dbti.DbtiResultEntity
import com.paw.key.domain.repository.DBTI.DbtiRepository
import javax.inject.Inject

class SubmitDbtiResultUseCase @Inject constructor(
    private val repository: DbtiRepository
) {
    suspend operator fun invoke(
        petId: Long,
        optionIds: List<Int>
    ): Result<DbtiResultEntity> {
        return repository.submitResult(petId, optionIds)
    }
}