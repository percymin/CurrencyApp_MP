package com.cookandroid.currencytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.cookandroid.currencytest.databinding.FragmentConverterBinding

class ConverterFragment : Fragment() {

    private var binding: FragmentConverterBinding? = null
    private var selectedBase: String = "USD"
    private var selectedTarget: String = "KRW"
    private var lastResult: Double? = null

    private val currencies = listOf(
        Triple("USD", "미국", "\uD83C\uDDFA\uD83C\uDDF8"),
        Triple("KRW", "대한민국", "\uD83C\uDDF0\uD83C\uDDF7"),
        Triple("JPY", "일본", "\uD83C\uDDEF\uD83C\uDDF5"),
        Triple("EUR", "유럽연합", "\uD83C\uDDEA\uD83C\uDDFA"),
        Triple("GBP", "영국", "\uD83C\uDDEC\uD83C\uDDE7"),
        Triple("CNY", "중국", "\uD83C\uDDE8\uD83C\uDDF3"),
        Triple("AUD", "호주", "\uD83C\uDDE6\uD83C\uDDFA"),
    )

    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "KRW" to 1340.15,
        "JPY" to 149.85,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "CNY" to 7.24,
        "AUD" to 1.52
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_BASE)?.let {
            selectedBase = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentConverterBinding.inflate(inflater, container, false)
        binding = b
        setupSpinners()
        b.inputAmount.doOnTextChanged { _, _, _, _ -> lastResult = null }
        b.btnSwap.setOnClickListener { swapCurrencies() }
        b.btnConvert.setOnClickListener { performConversion() }
        b.btnOpenAlert.setOnClickListener { (activity as? MainActivity)?.openAlertTab() }
        updateChart()
        updateResultVisibility()
        return b.root
    }

    private fun setupSpinners() {
        val labels = currencies.map { "${it.third} ${it.second} ${it.first}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, labels)
        binding?.spinnerBase?.adapter = adapter
        binding?.spinnerTarget?.adapter = adapter

        binding?.spinnerBase?.setSelection(currencies.indexOfFirst { it.first == selectedBase }.coerceAtLeast(0))
        binding?.spinnerTarget?.setSelection(currencies.indexOfFirst { it.first == selectedTarget }.coerceAtLeast(0))

        binding?.spinnerBase?.setOnItemSelectedListener { position ->
            selectedBase = currencies[position].first
            lastResult = null
            updateChart()
            updateResultVisibility()
        }
        binding?.spinnerTarget?.setOnItemSelectedListener { position ->
            selectedTarget = currencies[position].first
            lastResult = null
            updateChart()
            updateResultVisibility()
        }
    }

    private fun swapCurrencies() {
        val temp = selectedBase
        selectedBase = selectedTarget
        selectedTarget = temp
        setupSpinners()
        lastResult = null
        updateResultVisibility()
        updateChart()
    }

    private fun performConversion() {
        val amount = binding?.inputAmount?.text?.toString()?.toDoubleOrNull() ?: 0.0
        val baseRate = exchangeRates[selectedBase] ?: 1.0
        val targetRate = exchangeRates[selectedTarget] ?: 1.0
        val usdAmount = amount / baseRate
        val result = usdAmount * targetRate
        lastResult = result

        val rateText = "1 $selectedBase = ${formatNumber(targetRate / baseRate)} $selectedTarget"
        binding?.chartRateInfo?.text = rateText
        binding?.resultRateInfo?.text = rateText
        binding?.resultValue?.text = "환전 결과: ${formatNumber(result)} $selectedTarget"
        binding?.resultAiMain?.text = "AI 추세 분석: \uD83D\uDCC8 상승 추세입니다."
        binding?.resultAiSub?.text = "(최근 7일 중 5일 상승)"
        updateResultVisibility()
    }

    private fun updateChart() {
        binding?.chartTitle?.text = "7일 환율 그래프 ($selectedBase → $selectedTarget)"
        val data = generateChartData(selectedBase, selectedTarget)
        binding?.chartView?.setData(data, true, R.color.riseGreen, R.color.riseGreen)
        val low = data.minOrNull() ?: 0f
        val high = data.maxOrNull() ?: 0f
        val range = high - low
        binding?.labelLow?.text = formatNumber(low.toDouble())
        binding?.labelHigh?.text = formatNumber(high.toDouble())
        binding?.labelRange?.text = formatNumber(range.toDouble())
        binding?.chartRateInfo?.text = "1 $selectedBase = ${formatNumber((exchangeRates[selectedTarget] ?: 1.0) / (exchangeRates[selectedBase] ?: 1.0))} $selectedTarget"
    }

    private fun generateChartData(base: String, target: String): List<Float> {
        val baseRate = exchangeRates[base] ?: 1.0
        val targetRate = exchangeRates[target] ?: 1.0
        val mid = targetRate / baseRate
        val offsets = listOf(-0.8f, -0.3f, 0f, 0.2f, 0.5f, 0.7f, 0.4f)
        return offsets.map { (mid + it).toFloat() }
    }

    private fun updateResultVisibility() {
        binding?.resultCard?.visibility = if (lastResult != null) View.VISIBLE else View.GONE
    }

    fun setSelectedCurrency(code: String) {
        selectedBase = code
        if (binding != null) {
            setupSpinners()
            updateChart()
            updateResultVisibility()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun formatNumber(value: Double): String = String.format("%,.2f", value)

    private fun android.widget.Spinner.setOnItemSelectedListener(onSelected: (Int) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                onSelected(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    companion object {
        private const val ARG_BASE = "arg_base"
        fun newInstance(initialBaseCurrency: String): ConverterFragment {
            val fragment = ConverterFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_BASE, initialBaseCurrency)
            }
            return fragment
        }
    }
}
