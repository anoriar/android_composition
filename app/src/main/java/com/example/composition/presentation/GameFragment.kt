package com.example.composition.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() {
            return _binding ?: throw RuntimeException("Binding can not be null")
        }

    private lateinit var level: Level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        gameViewModel.startGame(level)
        launchObservers()
        initOptionsButtons()

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun launchObservers() {
        gameViewModel.formattedTime.observe(viewLifecycleOwner) {
            binding.tvGameTime.text = it
        }

        gameViewModel.question.observe(viewLifecycleOwner) {

            binding.tvOption1.text = it.options[0].toString()
            binding.tvOption2.text = it.options[1].toString()
            binding.tvOption3.text = it.options[2].toString()
            binding.tvOption4.text = it.options[3].toString()
            binding.tvOption5.text = it.options[4].toString()
            binding.tvOption6.text = it.options[5].toString()

            binding.tvSum.text = it.sum.toString()
            binding.tvVisibleNumber.text = it.visibleNumber.toString()
        }

        gameViewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswerProgress.text = it
        }
        gameViewModel.isFinished.observe(viewLifecycleOwner) {
            if (it) {
                gameViewModel.gameResult.observe(viewLifecycleOwner) { gameResult ->
                    launchGameFinishedFragment(gameResult)
                }
            }
        }

        gameViewModel.enoughCount.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvAnswerProgress.setTextColor(Color.GREEN)
            } else {
                binding.tvAnswerProgress.setTextColor(Color.RED)
            }

        }
        gameViewModel.enoughPercent.observe(viewLifecycleOwner) {
            if (it) {
                binding.pbAnswerProgress.progressTintList = ColorStateList.valueOf(Color.GREEN)
            } else {
                binding.pbAnswerProgress.progressTintList = ColorStateList.valueOf(Color.RED)
            }
        }

        gameViewModel.minPercent.observe(viewLifecycleOwner) {
            binding.pbAnswerProgress.progress = it
        }

        gameViewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.pbAnswerProgress.secondaryProgress = it
        }
    }

    private fun initOptionsButtons() {
        binding.tvOption1.setOnClickListener {
            gameViewModel.chooseAnswer(binding.tvOption1.text.toString().toInt())
        }
        binding.tvOption2.setOnClickListener {
            gameViewModel.chooseAnswer(binding.tvOption2.text.toString().toInt())
        }
        binding.tvOption3.setOnClickListener {
            gameViewModel.chooseAnswer(binding.tvOption3.text.toString().toInt())
        }
        binding.tvOption4.setOnClickListener {
            gameViewModel.chooseAnswer(binding.tvOption4.text.toString().toInt())
        }
        binding.tvOption5.setOnClickListener {
            gameViewModel.chooseAnswer(binding.tvOption5.text.toString().toInt())
        }
        binding.tvOption6.setOnClickListener {
            gameViewModel.chooseAnswer(binding.tvOption6.text.toString().toInt())
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction().replace(
            R.id.main_container,
            GameFinishedFragment.getInstance(
                gameResult
            )
        )
            .addToBackStack(null).commit()
    }

    private fun parseArguments() {
        requireArguments().getParcelable<Level>(LEVEL_KEY)?.let {
            level = it
        }
    }

    companion object {
        private const val LEVEL_KEY = "level"

        const val FRAGMENT_NAME = "game_fragment"

        fun getInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LEVEL_KEY, level)
                }
            }
        }
    }

}