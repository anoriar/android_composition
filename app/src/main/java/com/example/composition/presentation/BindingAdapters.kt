package com.example.composition.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entity.GameResult


@BindingAdapter("smile")
fun bindSmile(iv: ImageView, winner: Boolean) {
    iv.setImageResource(if (winner) R.drawable.ic_smile else R.drawable.ic_sad)
}

@BindingAdapter("requiredScore")
fun bindRequiredAnswers(tv: TextView, count: Int) {
    tv.text = String.format(
        tv.resources.getString(R.string.required_score),
        count.toString()
    )
}


@BindingAdapter("scoreAnswers")
fun bindScoreAnswers(tv: TextView, count: Int) {
    tv.text = String.format(
        tv.resources.getString(R.string.score_answers),
        count.toString()
    )
}

@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(tv: TextView, percent: Int) {
    tv.text = String.format(
        tv.resources.getString(R.string.required_percentage),
        percent.toString()
    )
}

@BindingAdapter("scorePercentage")
fun bindScorePercentage(tv: TextView, gameResult: GameResult) {
    tv.text = String.format(
        tv.resources.getString(R.string.score_percentage),
        getPercentOfRightAnswers(gameResult.countOfRightAnswers, gameResult.countOfQuestions).toString()
    )
}

private fun getPercentOfRightAnswers(countOfRightAnswers: Int, countOfQuestions: Int): Int {
    if (countOfQuestions == 0) {
        return 0
    } else {
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}

