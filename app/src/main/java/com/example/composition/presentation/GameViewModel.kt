package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.repository.GameRepository
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository: GameRepository = GameRepositoryImpl

    private val generateQuestionUseCase: GenerateQuestionUseCase =
        GenerateQuestionUseCase(gameRepository)
    private val getGameSettingsUseCase: GetGameSettingsUseCase =
        GetGameSettingsUseCase(gameRepository)

    private val _question: MutableLiveData<Question> = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _curTime: MutableLiveData<Int> = MutableLiveData<Int>()
    val curTime: LiveData<Int>
        get() = _curTime

    private lateinit var timer: CountDownTimer

    private val _isFinished: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    private val _gameResult: MutableLiveData<GameResult> = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private lateinit var gameSettings: GameSettings

    fun startGame(level: Level) {
        gameSettings = getGameSettingsUseCase.invoke(level)
        _curTime.value = gameSettings.gameTimeInSeconds
        startTimer()

        _gameResult.value = GameResult(false, 0, 1, gameSettings)

        generateQuestion()

    }

    private fun startTimer() {
        val tmpCurTime: Long = _curTime.value?.toLong() ?: 0L
        timer = object : CountDownTimer(tmpCurTime * ONE_SECOND, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _curTime.value = (millisUntilFinished / ONE_SECOND).toInt()
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer.start()
    }

    fun answerToQuestion(optionNumber: Int) {
        val countOfRightAnswers = gameResult.value?.countOfRightAnswers ?: 0
        _gameResult.value = _gameResult.value?.copy(
            countOfRightAnswers = if (isAnswerCorrect(optionNumber)) countOfRightAnswers + 1 else countOfRightAnswers,
            countOfQuestions = _gameResult.value?.countOfQuestions ?: 0 + 1
        )
        generateQuestion()
    }

    private fun isAnswerCorrect(optionNumber: Int): Boolean {
        val answer: Int = _question.value?.options?.get(
            optionNumber
        ) ?: 0
        val visibleNumber: Int = question.value?.visibleNumber ?: 0
        return question.value?.sum == answer + visibleNumber
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase.invoke(gameSettings.maxSumValue)
    }

    fun finishGame() {
        _isFinished.value = true
        _gameResult.value = _gameResult.value?.copy(
            winner = gameResult.value?.countOfRightAnswers ?: 0 >= gameSettings.minCountOfRightAnswers
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        private const val ONE_SECOND = 1000L
    }
}