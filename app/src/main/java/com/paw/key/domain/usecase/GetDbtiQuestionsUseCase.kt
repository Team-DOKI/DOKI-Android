package com.paw.key.domain.usecase

import com.paw.key.domain.entity.dbti.DbtiQuestionEntity
import com.paw.key.domain.repository.DBTI.DbtiRepository
import javax.inject.Inject

class GetDbtiQuestionsUseCase @Inject constructor(
    private val repository: DbtiRepository
) {
    suspend operator fun invoke(): Result<List<DbtiQuestionEntity>> {
        return repository.getQuestions()
    }
}