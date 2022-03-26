package com.example.composition.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

    private val gameViewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() {
            return _binding ?: throw RuntimeException("Binding can not be null")
        }

    private val tvOptions: MutableList<TextView> by lazy {
        mutableListOf(
            binding.tvOption1,
            binding.tvOption2,
            binding.tvOption3,
            binding.tvOption4,
            binding.tvOption5,
            binding.tvOption6,
        )
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
            for ((index, option) in it.options.withIndex()) {
                tvOptions[index].text = option.toString()
            }

            binding.tvSum.text = it.sum.toString()
            binding.tvVisibleNumber.text = it.visibleNumber.toString()
        }

        gameViewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswerProgress.text = it
        }

        gameViewModel.enoughCount.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvAnswerProgress.setTextColor(getColorByState(it))
            } else {
                binding.tvAnswerProgress.setTextColor(getColorByState(it))
            }

        }
        gameViewModel.enoughPercent.observe(viewLifecycleOwner) {
            if (it) {
                binding.pbAnswerProgress.progressTintList =
                    ColorStateList.valueOf(getColorByState(it))
            } else {
                binding.pbAnswerProgress.progressTintList =
                    ColorStateList.valueOf(getColorByState(it))
            }
        }

        gameViewModel.minPercent.observe(viewLifecycleOwner) {
            binding.pbAnswerProgress.secondaryProgress = it
        }

        gameViewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.pbAnswerProgress.setProgress(it, true)
        }

        gameViewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
    }

    private fun getColorByState(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun initOptionsButtons() {
        for (tvOption: TextView in tvOptions) {
            tvOption.setOnClickListener {
                gameViewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
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