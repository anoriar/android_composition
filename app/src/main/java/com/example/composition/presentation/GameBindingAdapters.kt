package com.example.composition.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composition.R

@BindingAdapter("numberToString")
fun bindNumberToString(tv: TextView, number: Int) {
    tv.text = number.toString()
}

@BindingAdapter("answerProgressTextColor")
fun bindAnswerProgressTextColor(tv: TextView, enoughCount: Boolean) {
    val color = getColorByState(tv.context, enoughCount)
    tv.setTextColor(color)
}

private fun getColorByState(context: Context, goodState: Boolean): Int {
    val resId = if (goodState) {
        android.R.color.holo_green_light
    } else {
        android.R.color.holo_red_light
    }
    return ContextCompat.getColor(context, resId)
}


@BindingAdapter("answerProgressBarColor")
fun bindAnswerProgressBarColor(pb: ProgressBar, enoughPercent: Boolean) {
    val color = getColorByState(pb.context, enoughPercent)
    pb.progressTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("answerProgressBarProgress")
fun bindAnswerProgressBarColor(pb: ProgressBar, progress: Int) {
    pb.setProgress(progress, true)
}