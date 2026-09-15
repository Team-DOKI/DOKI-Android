package com.paw.key.data.repositoryimpl.DBTI

import com.paw.key.data.remote.datasource.DBTI.DbtiDataSource
import com.paw.key.domain.entity.dbti.DbtiAnalysisEntity
import com.paw.key.domain.entity.dbti.DbtiResultEntity
import com.paw.key.domain.entity.dbti.DbtiOptionEntity
import com.paw.key.domain.entity.dbti.DbtiQuestionEntity
import com.paw.key.domain.repository.DBTI.DbtiRepository
import javax.inject.Inject

class DbtiRepositoryImpl @Inject constructor(
    private val dbtiDataSource: DbtiDataSource
) : DbtiRepository {

    override suspend fun getQuestions(): Result<List<DbtiQuestionEntity>> {
        return runCatching {
            val response = dbtiDataSource.getQuestions()
            response.data.questions.map { questionDto ->
                DbtiQuestionEntity(
                    id = questionDto.id,
                    categoryCode = questionDto.category.code,
                    categoryName = questionDto.category.name,
                    content = questionDto.content,
                    options = questionDto.options.map { optionDto ->
                        DbtiOptionEntity(
                            id = optionDto.id,
                            content = optionDto.content,
                            imageUrl = optionDto.imageUrl,
                            value = optionDto.value
                        )
                    }
                )
            }
        }
    }

    override suspend fun submitResult(
        petId: Long,
        optionIds: List<Int>
    ): Result<DbtiResultEntity> {
        return runCatching {
            val response = dbtiDataSource.submitResult(
                petId = petId,
                optionIds = optionIds
            )

            DbtiResultEntity(
                type = response.data.type,
                name = response.data.name,
                image = response.data.image,
                keyword = response.data.keyword,
                description = response.data.description,
                analysis = response.data.analysis.map { analysisDto ->
                    DbtiAnalysisEntity(
                        axis = analysisDto.axis,
                        leftLabel = analysisDto.leftLabel,
                        rightLabel = analysisDto.rightLabel,
                        dominantSide = analysisDto.dominantSide,
                        score = analysisDto.score
                    )
                }
            )
        }
    }

    override suspend fun getResult(petId: Long): Result<DbtiResultEntity> {
        return runCatching {
            val response = dbtiDataSource.getResult(
                petId = petId,
            )

            DbtiResultEntity(
                type = response.data.type,
                name = response.data.name,
                image = response.data.image,
                keyword = response.data.keyword,
                description = response.data.description,
                analysis = response.data.analysis.map { analysisDto ->
                    DbtiAnalysisEntity(
                        axis = analysisDto.axis,
                        leftLabel = analysisDto.leftLabel,
                        rightLabel = analysisDto.rightLabel,
                        dominantSide = analysisDto.dominantSide,
                        score = analysisDto.score
                    )
                }
            )
        }
    }
}