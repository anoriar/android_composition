package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.composition.R
import com.example.composition.databinding.FragmentChooseLevelBinding
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.databinding.FragmentWelcomeBinding
import com.example.composition.domain.entity.Level


/**
 * A simple [Fragment] subclass.
 * Use the [ChooseLevelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseLevelFragment : Fragment() {

    private var _binding: FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
        get() {
            return _binding ?: throw RuntimeException("Binding can not be null")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initButtons() {
        binding.btnTestLevel.setOnClickListener {
            launchGameFragment(Level.TEST)
        }
        binding.btnEasyLevel.setOnClickListener {
            launchGameFragment(Level.EASY)
        }

        binding.btnNormalLevel.setOnClickListener {
            launchGameFragment(Level.NORMAL)
        }

        binding.btnHardLevel.setOnClickListener {
            launchGameFragment(Level.HARD)
        }
    }

    private fun launchGameFragment(level: Level) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, GameFragment.getInstance(level))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun getInstance(): ChooseLevelFragment {
            return ChooseLevelFragment()
        }
    }
}