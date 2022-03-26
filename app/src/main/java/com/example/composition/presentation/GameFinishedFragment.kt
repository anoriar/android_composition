package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult


/**
 * A simple [Fragment] subclass.
 * Use the [GameFinishedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFinishedFragment : Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() {
            return _binding ?: throw RuntimeException("Binding can not be null")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtnRetry()
        bindViews()
    }

    private fun initBtnRetry() {
        binding.btnRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun bindViews() {
        val gameResult = args.gameResult
        val icon = if (gameResult.winner) R.drawable.ic_smile else R.drawable.ic_sad
        binding.ivSmile.setImageResource(icon)
        binding.tvRequiredScore.text = String.format(
            resources.getString(R.string.required_score),
            gameResult.gameSettings.minCountOfRightAnswers
        )
        binding.tvScoreAnswers.text = String.format(
            resources.getString(R.string.score_answers),
            gameResult.countOfRightAnswers,
        )
        binding.tvRequiredPercentage.text = String.format(
            resources.getString(R.string.required_percentage),
            gameResult.gameSettings.minPercentOfRightAnswers
        )
        binding.tvScorePercentage.text = String.format(
            resources.getString(R.string.score_percentage),
            getPercentOfRightAnswers()
        )
    }

    private fun getPercentOfRightAnswers() = with(args.gameResult) {
        if (countOfQuestions == 0) {
            0
        } else {
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun retryGame() {
        requireActivity().supportFragmentManager.popBackStack()
    }
}