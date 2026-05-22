package com.nammamistri.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nammamistri.app.databinding.FragmentCalculatorBinding
import com.nammamistri.app.R
import kotlin.math.ceil
import java.util.Locale

class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val BRICKS_PER_M3 = 500.0
        private const val M3_TO_CFT = 35.3147

        private val THICKNESS = mapOf(
            "4.5" to 0.1143,
            "9" to 0.2286,
            "13.5" to 0.3429
        )

        private val MORTAR_RATIO = mapOf(
            "4.5" to 0.20,
            "9" to 0.25,
            "13.5" to 0.30
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCalc.setOnClickListener { compute() }
    }

    private fun compute() {
        val b = binding
        val length = b.etLength.text.toString().toDoubleOrNull() ?: 0.0
        val height = b.etHeight.text.toString().toDoubleOrNull() ?: 0.0

        val key = when (b.rgThickness.checkedRadioButtonId) {
            R.id.rb9 -> "9"
            R.id.rb135 -> "13.5"
            else -> "4.5"
        }

        val t = THICKNESS[key] ?: 0.1143
        val mortarRatio = MORTAR_RATIO[key] ?: 0.20

        val wallVolume = length * height * t
        val brickVolume = wallVolume * (1.0 - mortarRatio)
        val mortarVolume = wallVolume * mortarRatio

        val bricksCount = (brickVolume * BRICKS_PER_M3).toInt()

        val cementVolume = mortarVolume / 7.0
        val sandVolume = cementVolume * 6.0

        val cementBags = ceil(cementVolume / 0.0347).toInt()
        val sandCft = sandVolume * M3_TO_CFT

        val context = requireContext()
        val rates = Storage.load(context, "rates", Rates())
        val estimatedCost = (bricksCount.toDouble() * rates.brick) +
                (cementBags.toDouble() * rates.cementBag) +
                (sandCft * rates.sandCft)

        b.results.visibility = View.VISIBLE
        b.tvBricks.text = "ಇಟ್ಟಿಗೆ:  $bricksCount"
        b.tvCement.text = "ಸಿಮೆಂಟ್:  $cementBags ಚೀಲ"
        b.tvSand.text = "ಮರಳು:  ${String.format(Locale.US, "%.1f", sandCft)} CFT"
        b.tvCost.text = "ಒಟ್ಟು ವೆಚ್ಚ:  ${Storage.inr(estimatedCost)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
