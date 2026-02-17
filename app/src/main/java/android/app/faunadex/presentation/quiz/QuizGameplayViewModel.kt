package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.model.QuizAttempt
import android.app.faunadex.domain.model.UserAnswer
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.domain.usecase.GetQuizQuestionsUseCase
import android.app.faunadex.domain.usecase.SubmitQuizUseCase
import android.app.faunadex.domain.repository.QuizRepository
import android.app.faunadex.utils.QuizMusicPlayer
import android.app.faunadex.utils.SoundEffectPlayer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class QuizGameplayUiState(
    val quiz: Quiz? = null,
    val questions: List<ShuffledQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isRevealed: Boolean = false,
    val timeRemaining: Int = 30,
    val userAnswers: Map<String, UserAnswer> = emptyMap(),
    val attemptId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isQuizCompleted: Boolean = false,
    val isMuted: Boolean = false,
    val canProceedToNext: Boolean = false,
    val countdown: Int? = null
) {
    val currentQuestion: ShuffledQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val correctAnswers: Int
        get() = userAnswers.values.count { it.isCorrect }

    val wrongAnswers: Int
        get() = userAnswers.values.count { !it.isCorrect }

    val progress: Float
        get() = if (questions.isNotEmpty()) {
            currentQuestionIndex.toFloat() / questions.size
        } else 0f
}

@HiltViewModel
class QuizGameplayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getQuizQuestionsUseCase: GetQuizQuestionsUseCase,
    private val quizRepository: QuizRepository,
    private val submitQuizUseCase: SubmitQuizUseCase,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val quizId: String = savedStateHandle.get<String>("quizId") ?: ""

    private val _uiState = MutableStateFlow(QuizGameplayUiState())
    val uiState: StateFlow<QuizGameplayUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var countdownJob: Job? = null
    private var questionStartTime: Long = 0
    private var quizStartTime: Long = 0
    private var musicStarted: Boolean = false

    private val musicPlayer: QuizMusicPlayer = QuizMusicPlayer(context, R.raw.quiz_background_music)
    private val soundEffectPlayer: SoundEffectPlayer = SoundEffectPlayer(context)

    init {
        loadQuizAndQuestions()
    }

    private fun loadQuizAndQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val quizResult = quizRepository.getQuizById(quizId)
            val quiz = quizResult.getOrNull()

            if (quiz == null) {
                _uiState.value = _uiState.value.copy(
                    error = context.getString(R.string.error_quiz_not_found),
                    isLoading = false
                )
                return@launch
            }

            val questionsResult = getQuizQuestionsUseCase(quizId)

            questionsResult.fold(
                onSuccess = { questions ->
                    android.util.Log.d("QuizGameplay", "Loaded ${questions.size} questions for quiz $quizId")
                    questions.forEach { q ->
                        android.util.Log.d("QuizGameplay", "  - Question: ${q.id} - ${q.questionTextEn.take(50)}")
                    }

                    val user = getCurrentUserUseCase()
                    if (user != null) {
                        val attemptResult = quizRepository.startQuizAttempt(user.uid, quizId)

                        attemptResult.fold(
                            onSuccess = { attempt ->
                                val shuffledQuestions = shuffleQuestions(questions)

                                _uiState.value = _uiState.value.copy(
                                    quiz = quiz,
                                    questions = shuffledQuestions,
                                    attemptId = attempt.id,
                                    timeRemaining = quiz.timeLimitSeconds,
                                    isLoading = false,
                                    countdown = 3
                                )

                                startCountdown()
                            },
                            onFailure = { exception ->
                                android.util.Log.e("QuizGameplay", "Error starting quiz attempt: ${exception.message}")
                                _uiState.value = _uiState.value.copy(
                                    error = exception.message ?: context.getString(R.string.error_failed_start_quiz),
                                    isLoading = false
                                )
                            }
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = context.getString(R.string.error_user_not_logged_in),
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    android.util.Log.e("QuizGameplay", "Error loading questions: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: context.getString(R.string.error_failed_load_questions),
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun shuffleQuestions(questions: List<Question>): List<ShuffledQuestion> {
        return questions.shuffled().map { question ->
            val optionsCount = question.optionsEn.size
            val indices = (0 until optionsCount).toList().shuffled()

            val newCorrectIndex = indices.indexOf(question.correctAnswerIndex)

            val shuffledOptionsEn = indices.map { question.optionsEn[it] }
            val shuffledOptionsId = indices.map { question.optionsId[it] }

            val shuffledQuestion = question.copy(
                optionsEn = shuffledOptionsEn,
                optionsId = shuffledOptionsId
            )

            ShuffledQuestion(
                originalQuestion = shuffledQuestion,
                shuffledCorrectAnswerIndex = newCorrectIndex
            )
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var count = 3
            while (count > 0) {
                _uiState.value = _uiState.value.copy(countdown = count)
                delay(1000)
                count--
            }

            _uiState.value = _uiState.value.copy(countdown = null)

            quizStartTime = System.currentTimeMillis()
            questionStartTime = System.currentTimeMillis()
            startTimer()

            if (!_uiState.value.isMuted) {
                musicPlayer.play()
                musicStarted = true
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && !_uiState.value.isRevealed) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    timeRemaining = _uiState.value.timeRemaining - 1
                )
            }

            if (_uiState.value.timeRemaining <= 0 && !_uiState.value.isRevealed) {
                confirmAnswer(isTimeout = true)
            }
        }
    }

    fun selectAnswer(index: Int) {
        if (!_uiState.value.isRevealed) {
            soundEffectPlayer.play(R.raw.select_sound_effect)
            _uiState.value = _uiState.value.copy(selectedAnswerIndex = index)
        }
    }

    fun confirmAnswer(isTimeout: Boolean = false) {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion ?: return
        val selectedIndex = state.selectedAnswerIndex ?: -1

        val timeTaken = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()

        val isCorrect = if (isTimeout) {
            false
        } else {
            selectedIndex == currentQuestion.shuffledCorrectAnswerIndex
        }

        if (isCorrect) {
            soundEffectPlayer.play(R.raw.correct_sound_effect)
        } else {
            soundEffectPlayer.play(R.raw.wrong_sound_effect)
        }

        val userAnswer = UserAnswer(
            questionId = currentQuestion.originalQuestion.id,
            selectedAnswerIndex = selectedIndex,
            isCorrect = isCorrect,
            timeTakenSeconds = timeTaken
        )

        val updatedAnswers = state.userAnswers + (currentQuestion.originalQuestion.id to userAnswer)

        _uiState.value = state.copy(
            isRevealed = true,
            userAnswers = updatedAnswers,
            canProceedToNext = false
        )

        timerJob?.cancel()

        viewModelScope.launch {
            delay(1500)
            _uiState.value = _uiState.value.copy(canProceedToNext = true)
        }
    }

    fun nextQuestion() {
        val state = _uiState.value

        if (state.currentQuestionIndex < state.questions.size - 1) {
            _uiState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedAnswerIndex = null,
                isRevealed = false,
                timeRemaining = state.quiz?.timeLimitSeconds ?: 30,
                canProceedToNext = false
            )

            questionStartTime = System.currentTimeMillis()
            startTimer()
        } else {
            submitQuiz()
        }
    }

    fun pauseMusic() {
        musicPlayer.pause()
    }

    fun resumeMusic() {
        if (musicStarted && !_uiState.value.isQuizCompleted && !_uiState.value.isMuted) {
            musicPlayer.play()
        }
    }

    fun toggleMute() {
        val newMutedState = !_uiState.value.isMuted
        _uiState.value = _uiState.value.copy(isMuted = newMutedState)

        if (newMutedState) {
            musicPlayer.pause()
        } else {
            if (musicStarted && !_uiState.value.isQuizCompleted) {
                musicPlayer.play()
            }
        }
    }

    private fun submitQuiz() {
        viewModelScope.launch {
            android.util.Log.d("QuizGameplay", "submitQuiz() called")
            val state = _uiState.value
            val user = getCurrentUserUseCase()
            val quiz = state.quiz

            if (user == null || quiz == null) {
                android.util.Log.e("QuizGameplay", "submitQuiz failed - user or quiz is null")
                return@launch
            }

            val totalTimeTaken = ((System.currentTimeMillis() - quizStartTime) / 1000).toInt()
            val correctCount = state.correctAnswers
            val wrongCount = state.wrongAnswers
            val totalQuestions = state.questions.size

            android.util.Log.d("QuizGameplay", "Quiz submission data:")
            android.util.Log.d("QuizGameplay", "  - correctCount: $correctCount")
            android.util.Log.d("QuizGameplay", "  - wrongCount: $wrongCount")
            android.util.Log.d("QuizGameplay", "  - totalQuestions: $totalQuestions")

            val score = submitQuizUseCase.calculateScore(correctCount, totalQuestions)
            val xpEarned = submitQuizUseCase.calculateXpEarned(score, quiz.xpReward)
            val completionPercentage = ((correctCount.toDouble() / totalQuestions) * 100).toInt()

            val attempt = QuizAttempt(
                id = state.attemptId,
                userId = user.uid,
                quizId = quiz.id,
                score = score,
                totalQuestions = totalQuestions,
                correctAnswers = correctCount,
                wrongAnswers = wrongCount,
                completionPercentage = completionPercentage,
                xpEarned = xpEarned,
                timeTakenSeconds = totalTimeTaken,
                answers = state.userAnswers,
                isCompleted = true
            )

            android.util.Log.d("QuizGameplay", "Attempting to submit: $attempt")

            val result = submitQuizUseCase(attempt, xpEarned)

            result.fold(
                onSuccess = {
                    android.util.Log.d("QuizGameplay", "Quiz submitted successfully!")

                    // Stop music when quiz is completed
                    musicPlayer.stop()

                    _uiState.value = state.copy(isQuizCompleted = true)
                },
                onFailure = { exception ->
                    android.util.Log.e("QuizGameplay", "Failed to submit quiz: ${exception.message}")
                    _uiState.value = state.copy(
                        error = exception.message ?: context.getString(R.string.error_failed_submit_quiz)
                    )
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        countdownJob?.cancel()

        musicPlayer.release()
        soundEffectPlayer.release()
    }
}
