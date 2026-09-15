package com.paw.key.presentation.ui.dbti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.domain.entity.dbti.DbtiQuestionEntity
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.usecase.GetDbtiQuestionsUseCase
import com.paw.key.domain.usecase.SubmitDbtiResultUseCase
import com.paw.key.presentation.ui.dbti.result.model.DbtiResultUiModel
import com.paw.key.presentation.ui.dbti.result.model.toUiModel
import com.paw.key.presentation.ui.dbti.test.model.TestOptionModel
import com.paw.key.presentation.ui.dbti.test.state.TestUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DbtiViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val getDbtiQuestionsUseCase: GetDbtiQuestionsUseCase,
    private val submitDbtiResultUseCase: SubmitDbtiResultUseCase
) : ViewModel() {

    // Test 관련 state
    private val _testUiState = MutableStateFlow<TestUiState>(TestUiState.Loading)
    val testUiState = _testUiState.asStateFlow()

    // Result 관련 state
    private val _resultUiModel = MutableStateFlow<DbtiResultUiModel?>(null)
    val resultUiModel = _resultUiModel.asStateFlow()

    private var questions: List<DbtiQuestionEntity> = emptyList()
    private var currentIndex = 0
    private val selectedAnswers = mutableMapOf<Int, Int>()
    init {
        resetTest()
    }
    fun loadQuestions() {
        viewModelScope.launch {
            _testUiState.value = TestUiState.Loading
            getDbtiQuestionsUseCase()
                .onSuccess { questionList ->
                    questions = questionList
                    if (questionList.isNotEmpty()) {
                        updateCurrentQuestion()
                    } else {
                        _testUiState.value = TestUiState.Error("질문을 불러올 수 없습니다.")
                    }
                }
                .onFailure {
                    _testUiState.value = TestUiState.Error(it.message ?: "알 수 없는 오류")
                }
        }
    }

    fun selectOption(optionId: Int) {
        val currentQuestion = questions.getOrNull(currentIndex) ?: return
        selectedAnswers[currentQuestion.id] = optionId
        updateCurrentQuestion()
    }

    fun nextQuestion(onNavigateToResult: () -> Unit) {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            updateCurrentQuestion()
        } else {
            submitResult(onNavigateToResult)
        }
    }

    private fun submitResult(onNavigateToResult: () -> Unit) {
        viewModelScope.launch {
            _testUiState.value = TestUiState.Loading

            val optionIds = questions.map { selectedAnswers[it.id] ?: 0 }

            submitDbtiResultUseCase(
                petId = localStorageRepository.getPetId().toLong(),
                optionIds = optionIds
            )
                .onSuccess { result ->
                    _resultUiModel.value = result.toUiModel()
                    onNavigateToResult()
                }
                .onFailure {
                    _testUiState.value = TestUiState.Error(it.message ?: "결과 제출 실패")
                }
        }
    }

    private fun updateCurrentQuestion() {
        val currentQuestion = questions.getOrNull(currentIndex) ?: return
        _testUiState.value = TestUiState.Success(
            questionNumber = currentIndex + 1,
            totalQuestions = questions.size,
            categoryName = currentQuestion.categoryName,
            questionText = currentQuestion.content,
            options = currentQuestion.options.map {
                TestOptionModel(id = it.id, text = it.content, imageUrl = it.imageUrl)
            },
            selectedOptionId = selectedAnswers[currentQuestion.id]
        )
    }

    fun resetTest() {
        questions = emptyList()
        currentIndex = 0
        selectedAnswers.clear()
        _resultUiModel.value = null
        loadQuestions()
    }
}