package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

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
        initOptionsButtons()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initOptionsButtons() {
        binding.tvOption1.setOnClickListener {
            launchGameFinishedFragment(
                GameResult(
                    true,
                    1,
                    2,
                    GameSettings(
                        5,
                        4,
                        2,
                        1
                    )
                )
            )
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