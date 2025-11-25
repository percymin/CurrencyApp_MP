package com.cookandroid.currencytest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.cookandroid.currencytest.data.CurrencyCardRepository
import com.cookandroid.currencytest.databinding.FragmentHomeBinding
import com.cookandroid.currencytest.ui.HomeCurrencyAdapter

class HomeFragment : Fragment() {

    interface OnCurrencySelectedListener {
        fun onCurrencySelected(code: String)
    }

    private var binding: FragmentHomeBinding? = null
    private var listener: OnCurrencySelectedListener? = null
    private lateinit var adapter: HomeCurrencyAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnCurrencySelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentHomeBinding.inflate(inflater, container, false)
        binding = b
        adapter = HomeCurrencyAdapter(CurrencyCardRepository.cards.toMutableList()) { code ->
            listener?.onCurrencySelected(code)
        }
        b.currencyRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        b.currencyRecycler.adapter = adapter
        b.btnSearch.setOnClickListener { applyFilter() }
        return b.root
    }

    private fun applyFilter() {
        val query = binding?.inputSearch?.text?.toString()?.trim().orEmpty()
        val filtered = if (query.isEmpty()) {
            CurrencyCardRepository.cards
        } else {
            CurrencyCardRepository.cards.filter {
                it.code.contains(query, true) || it.name.contains(query, true)
            }
        }
        adapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
