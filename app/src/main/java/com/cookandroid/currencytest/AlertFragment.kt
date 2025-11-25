package com.cookandroid.currencytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.currencytest.databinding.FragmentAlertBinding
import com.cookandroid.currencytest.model.Alert
import com.cookandroid.currencytest.ui.AlertAdapter
import java.util.Date
import java.util.UUID

class AlertFragment : Fragment() {

    private var binding: FragmentAlertBinding? = null
    private val alerts: MutableList<Alert> = mutableListOf(
        Alert(id = UUID.randomUUID().toString(), baseCurrency = "USD", targetCurrency = "KRW", targetRate = 1320.0, condition = "below", createdAt = Date(124, 10, 20)),
        Alert(id = UUID.randomUUID().toString(), baseCurrency = "EUR", targetCurrency = "KRW", targetRate = 1700.0, condition = "above", createdAt = Date(124, 10, 22))
    )
    private lateinit var adapter: AlertAdapter
    private val baseOptions = listOf("USD", "JPY", "EUR", "GBP")
    private val targetOptions = listOf("KRW", "USD", "JPY", "EUR")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentAlertBinding.inflate(inflater, container, false)
        binding = b
        adapter = AlertAdapter(alerts) { alert ->
            adapter.remove(alert)
            toggleEmpty()
            Toast.makeText(requireContext(), "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
        b.alertRecycler.layoutManager = LinearLayoutManager(requireContext())
        b.alertRecycler.adapter = adapter

        b.spinnerBase.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, baseOptions)
        b.spinnerTarget.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, targetOptions)

        b.btnSaveAlert.setOnClickListener { saveAlert() }
        b.textCurrentRate.text = "1,340.15원"
        b.textAiSuggestion.text = "최근 하락 추세 — 1320 근처에서 알림 설정 추천"
        toggleEmpty()
        return b.root
    }

    private fun saveAlert() {
        val base = baseOptions[binding?.spinnerBase?.selectedItemPosition ?: 0]
        val target = targetOptions[binding?.spinnerTarget?.selectedItemPosition ?: 0]
        val targetRate = binding?.inputTargetRate?.text?.toString()?.toDoubleOrNull()
        val condition = if (binding?.radioBelow?.isChecked == true) "below" else "above"

        if (targetRate == null) {
            Toast.makeText(requireContext(), "목표 환율을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val alert = Alert(
            id = UUID.randomUUID().toString(),
            baseCurrency = base,
            targetCurrency = target,
            targetRate = targetRate,
            condition = condition,
            createdAt = Date()
        )
        adapter.add(alert)
        binding?.inputTargetRate?.setText("")
        toggleEmpty()
        val conditionText = if (condition == "below") "이하" else "이상"
        Toast.makeText(requireContext(), "$base → $target, ${String.format("%,.0f", targetRate)} $conditionText 알림 설정이 저장되었습니다!", Toast.LENGTH_SHORT).show()
    }

    private fun toggleEmpty() {
        val isEmpty = adapter.itemCount == 0
        binding?.textEmpty?.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
