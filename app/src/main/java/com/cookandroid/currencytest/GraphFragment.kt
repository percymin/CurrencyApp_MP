package com.cookandroid.currencytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.cookandroid.currencytest.data.CurrencyCardRepository
import com.cookandroid.currencytest.databinding.FragmentGraphBinding
import com.cookandroid.currencytest.ui.GraphCurrencyAdapter

class GraphFragment : Fragment() {

    private var binding: FragmentGraphBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentGraphBinding.inflate(inflater, container, false)
        binding = b

        val adapter = GraphCurrencyAdapter(CurrencyCardRepository.cards)
        b.graphRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        b.graphRecycler.adapter = adapter

        b.btnHana.setOnClickListener { setBankSelection(true) }
        b.btnShinhan.setOnClickListener { setBankSelection(false) }
        setBankSelection(true)

        return b.root
    }

    private fun setBankSelection(isHana: Boolean) {
        binding?.apply {
            if (isHana) {
                btnHana.setBackgroundColor(requireContext().getColor(R.color.primaryBlue))
                btnHana.setTextColor(requireContext().getColor(R.color.white))
                btnShinhan.setBackgroundColor(requireContext().getColor(R.color.cardBackground))
                btnShinhan.setTextColor(requireContext().getColor(R.color.textPrimary))
            } else {
                btnShinhan.setBackgroundColor(requireContext().getColor(R.color.primaryBlue))
                btnShinhan.setTextColor(requireContext().getColor(R.color.white))
                btnHana.setBackgroundColor(requireContext().getColor(R.color.cardBackground))
                btnHana.setTextColor(requireContext().getColor(R.color.textPrimary))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
