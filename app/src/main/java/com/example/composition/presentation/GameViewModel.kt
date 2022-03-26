package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composition.R
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
    private val context = application

    private val generateQuestionUseCase: GenerateQuestionUseCase =
        GenerateQuestionUseCase(gameRepository)
    private val getGameSettingsUseCase: GetGameSettingsUseCase =
        GetGameSettingsUseCase(gameRepository)

    private val _question: MutableLiveData<Question> = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _formattedTime: MutableLiveData<String> = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private lateinit var timer: CountDownTimer

    private val _gameResult: MutableLiveData<GameResult> = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private lateinit var gameSettings: GameSettings

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    //    Строка "Правильных ответов %s (минимум %s)"
    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0


    fun startGame(level: Level) {
        gameSettings = getGameSettingsUseCase.invoke(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
        startTimer()
        generateQuestion()
        updateProgress()
    }

    private fun startTimer() {
        timer = object :
            CountDownTimer(gameSettings.gameTimeInSeconds.toLong() * ONE_SECOND, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTimeFromMillis(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer.start()
    }

    private fun formatTimeFromMillis(millis: Long): String {
        val seconds = millis / ONE_SECOND
        return String.format("%02d:%02d", (seconds / 60), (seconds % 60))
    }

    fun chooseAnswer(number: Int) {
        if (checkAnswer(number)) {
            countOfRightAnswers++
        }
        countOfQuestions++
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            context.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCount.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        if (countOfQuestions == 0) {
            return 0
        }
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int): Boolean {
        return number == question.value?.rightAnswer
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase.invoke(gameSettings.maxSumValue)
    }

    fun finishGame() {
        _gameResult.value = GameResult(
            winner = enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
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