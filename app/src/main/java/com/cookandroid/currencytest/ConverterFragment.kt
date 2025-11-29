package com.cookandroid.currencytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cookandroid.currencytest.databinding.FragmentConverterBinding
import com.cookandroid.currencytest.model.CurrencyCard

class ConverterFragment : Fragment() {

    private var binding: FragmentConverterBinding? = null

    // Activity와 데이터를 공유하는 ViewModel
    private val viewModel: MainViewModel by activityViewModels()

    private var selectedBase: String = "USD"
    private var selectedTarget: String = "KRW"

    // 받아온 최신 환율 정보를 저장할 리스트
    private var currencyList: List<CurrencyCard> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val b = FragmentConverterBinding.inflate(inflater, container, false)
        binding = b

        // 1. ViewModel에서 최신 환율 데이터 관찰
        viewModel.currencyList.observe(viewLifecycleOwner) { list ->
            currencyList = list
            setupSpinners() // 데이터가 들어오면 스피너(선택창) 세팅
        }

        // 2. ViewModel에서 AI 한 줄 요약 결과 관찰
        viewModel.conversionSummary.observe(viewLifecycleOwner) { summary ->
            binding?.resultAiMain?.text = "AI 조언: $summary"
        }

        // 입력값이 바뀌면 결과창 초기화 (새로 계산 필요)
        b.inputAmount.doOnTextChanged { _, _, _, _ ->
            // 실시간 계산을 원하면 여기서 performConversion() 호출 가능
            // 여기서는 버튼 누를 때만 계산하도록 함
        }

        b.btnSwap.setOnClickListener { swapCurrencies() }
        b.btnConvert.setOnClickListener { performConversion() } // 변환 버튼 클릭
        b.btnOpenAlert.setOnClickListener { (activity as? MainActivity)?.openAlertTab() }

        return b.root
    }

    private fun setupSpinners() {
        if (currencyList.isEmpty()) return

        // 리스트에 '대한민국(KRW)'이 없다면 수동으로 추가 (계산 기준용)
        val fullList = currencyList.toMutableList()
        if (fullList.none { it.code == "KRW" }) {
            fullList.add(0, CurrencyCard("대한민국", "KRW", 1.0, 0.0, 0.0, emptyList(), true))
        }

        // 스피너에 보여줄 텍스트 (예: 미국 USD, 대한민국 KRW)
        val labels = fullList.map { "${it.name} ${it.code}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, labels)

        binding?.spinnerBase?.adapter = adapter
        binding?.spinnerTarget?.adapter = adapter

        // 이전에 선택했던 통화 유지
        val baseIdx = fullList.indexOfFirst { it.code == selectedBase }.coerceAtLeast(0)
        val targetIdx = fullList.indexOfFirst { it.code == selectedTarget }.coerceAtLeast(1)

        binding?.spinnerBase?.setSelection(baseIdx)
        binding?.spinnerTarget?.setSelection(targetIdx)

        // 스피너 선택 리스너
        binding?.spinnerBase?.setOnItemSelectedListener { pos ->
            if (pos in fullList.indices) {
                selectedBase = fullList[pos].code
                updateChart()
            }
        }
        binding?.spinnerTarget?.setOnItemSelectedListener { pos ->
            if (pos in fullList.indices) {
                selectedTarget = fullList[pos].code
                updateChart()
            }
        }
    }

    private fun performConversion() {
        if (currencyList.isEmpty()) return

        val amount = binding?.inputAmount?.text?.toString()?.toDoubleOrNull() ?: 0.0

        // 선택된 통화 정보 찾기 (없으면 KRW로 가정)
        val baseItem = currencyList.find { it.code == selectedBase } ?: CurrencyCard("","KRW", 1.0,0.0,0.0, emptyList(),true)
        val targetItem = currencyList.find { it.code == selectedTarget } ?: CurrencyCard("","KRW", 1.0,0.0,0.0, emptyList(),true)

        // 1. 환율 계산 (모두 KRW 기준으로 변환 후 계산)
        // 공식: (입력금액 * 기준통화KRW환율) / 대상통화KRW환율
        val baseRate = getRatePerUnit(baseItem)
        val targetRate = getRatePerUnit(targetItem)

        val amountInKrw = amount * baseRate
        val result = amountInKrw / targetRate

        // 2. 결과 텍스트 표시
        val cleanBaseCode = selectedBase.replace(" 100", "")
        val cleanTargetCode = selectedTarget.replace(" 100", "")

        binding?.resultValue?.text = "환전 결과: ${formatNumber(result)} $cleanTargetCode"
        binding?.resultRateInfo?.text = "1 $cleanBaseCode = ${formatNumber(baseRate/targetRate)} $cleanTargetCode"

        // 3. 변동률 표시 (상승/하락 아이콘)
        val trendIcon = if (targetItem.isPositive) "📈" else "📉"
        val trendColor = if(targetItem.isPositive) requireContext().getColor(R.color.riseRed) else requireContext().getColor(R.color.fallBlue)

        binding?.resultAiSub?.text = "$trendIcon 전일 대비 ${String.format("%.2f", targetItem.changePercent)}% 변동"
        binding?.resultAiSub?.setTextColor(trendColor)

        // 4. [AI] 한 줄 요약 요청
        // 대상 통화(Target)의 변동률을 넘겨서 조언을 구함
        viewModel.fetchConversionSummary(cleanBaseCode, cleanTargetCode, targetItem.changePercent)

        // 결과 카드 보여주기
        binding?.resultCard?.visibility = View.VISIBLE
    }

    // 통화 단위당 KRW 가격 구하기 (JPY 100은 100으로 나눔)
    private fun getRatePerUnit(card: CurrencyCard): Double {
        return if (card.code == "KRW") 1.0
        else if (card.code.contains("100")) card.rate / 100.0
        else card.rate
    }

    private fun updateChart() {
        // 그래프는 '대상 통화(Target)'의 흐름을 보여줌
        val targetItem = currencyList.find { it.code == selectedTarget }
        if (targetItem != null && targetItem.data.isNotEmpty()) {
            binding?.chartTitle?.text = "${selectedTarget} 최근 7일 흐름"

            // 그래프 데이터 세팅
            val floatData = targetItem.data.map { it.toFloat() }
            binding?.chartView?.setData(floatData, targetItem.isPositive, R.color.riseRed, R.color.fallBlue)

            // 최저/최고/변동폭 텍스트 세팅
            val min = targetItem.data.minOrNull() ?: 0.0
            val max = targetItem.data.maxOrNull() ?: 0.0
            binding?.labelLow?.text = formatNumber(min)
            binding?.labelHigh?.text = formatNumber(max)
            binding?.labelRange?.text = formatNumber(max - min)
        }
    }

    private fun swapCurrencies() {
        val temp = selectedBase
        selectedBase = selectedTarget
        selectedTarget = temp
        setupSpinners() // 스피너 선택값 갱신
        binding?.resultCard?.visibility = View.GONE // 통화가 바뀌었으니 결과창 숨김
    }

    private fun formatNumber(value: Double): String = String.format("%,.2f", value)

    // 스피너 리스너 편의 함수
    private fun android.widget.Spinner.setOnItemSelectedListener(onSelected: (Int) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) { onSelected(pos) }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }
    }

    // MainActivity에서 탭 이동 시 호출되는 함수
    fun setSelectedCurrency(code: String) {
        selectedBase = code
        if (binding != null && currencyList.isNotEmpty()) {
            setupSpinners()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val ARG_BASE = "arg_base"
        fun newInstance(base: String) = ConverterFragment().apply {
            arguments = Bundle().apply { putString(ARG_BASE, base) }
        }
    }
}
