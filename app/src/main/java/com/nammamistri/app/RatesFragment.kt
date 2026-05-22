package com.nammamistri.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nammamistri.app.databinding.FragmentRatesBinding

class RatesFragment : Fragment() {
    private var _b: FragmentRatesBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRatesBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        val r = Storage.load(requireContext(), "rates", Rates())
        b.etBrick.setText(r.brick.toString())
        b.etCement.setText(r.cementBag.toString())
        b.etSand.setText(r.sandCft.toString())

        b.btnSave.setOnClickListener {
            val nr = Rates(
                brick = b.etBrick.text.toString().toDoubleOrNull() ?: r.brick,
                cementBag = b.etCement.text.toString().toDoubleOrNull() ?: r.cementBag,
                sandCft = b.etSand.text.toString().toDoubleOrNull() ?: r.sandCft
            )
            Storage.save(requireContext(), "rates", nr)
            Toast.makeText(requireContext(), "✓ Saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
