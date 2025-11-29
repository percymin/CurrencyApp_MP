package com.cookandroid.currencytest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.currencytest.data.AlertStorage
import com.cookandroid.currencytest.databinding.FragmentAlertBinding
import com.cookandroid.currencytest.model.Alert
import com.cookandroid.currencytest.model.CurrencyCard
import com.cookandroid.currencytest.util.RateUtils
import com.cookandroid.currencytest.ui.AlertAdapter
import java.util.Date
import java.util.UUID

class AlertFragment : Fragment() {

    private var binding: FragmentAlertBinding? = null
    private val alerts: MutableList<Alert> by lazy {
        AlertStorage.load(requireContext()).ifEmpty {
            mutableListOf(
                Alert(id = UUID.randomUUID().toString(), baseCurrency = "USD", targetCurrency = "KRW", targetRate = 1320.0, condition = "below", createdAt = Date()),
                Alert(id = UUID.randomUUID().toString(), baseCurrency = "EUR", targetCurrency = "KRW", targetRate = 1700.0, condition = "above", createdAt = Date())
            )
        }
    }
    private lateinit var adapter: AlertAdapter
    private val baseOptions = listOf("USD", "JPY", "EUR", "GBP")
    private val targetOptions = listOf("KRW", "USD", "JPY", "EUR")
    private val viewModel: MainViewModel by activityViewModels()
    private var latestRates: List<CurrencyCard> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentAlertBinding.inflate(inflater, container, false)
        binding = b
        createNotificationChannel()
        adapter = AlertAdapter(alerts) { alert ->
            adapter.remove(alert)
            AlertStorage.save(requireContext(), alerts)
            toggleEmpty()
            Toast.makeText(requireContext(), "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
        b.alertRecycler.layoutManager = LinearLayoutManager(requireContext())
        b.alertRecycler.adapter = adapter

        b.spinnerBase.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, baseOptions)
        b.spinnerTarget.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, targetOptions)

        b.btnSaveAlert.setOnClickListener { saveAlert() }
        b.textCurrentRate.text = "데이터 불러오는 중..."
        b.textAiSuggestion.text = "최근 하락 추세 — 1320 근처에서 알림 설정 추천"

        // 환율 데이터 관찰하여 현재 환율 표시 갱신
        viewModel.currencyList.observe(viewLifecycleOwner) { list ->
            latestRates = list
            updateCurrentRateDisplay()
        }

        b.spinnerBase.setOnItemSelectedListener { updateCurrentRateDisplay() }
        b.spinnerTarget.setOnItemSelectedListener { updateCurrentRateDisplay() }
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
        AlertStorage.save(requireContext(), alerts)
        binding?.inputTargetRate?.setText("")
        toggleEmpty()
        val conditionText = if (condition == "below") "이하" else "이상"
        Toast.makeText(requireContext(), "$base → $target, ${String.format("%,.0f", targetRate)} $conditionText 알림 설정이 저장되었습니다!", Toast.LENGTH_SHORT).show()
        maybeNotify(alert)
    }

    private fun toggleEmpty() {
        val isEmpty = adapter.itemCount == 0
        binding?.textEmpty?.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun updateCurrentRateDisplay() {
        val base = baseOptions[binding?.spinnerBase?.selectedItemPosition ?: 0]
        val target = targetOptions[binding?.spinnerTarget?.selectedItemPosition ?: 0]
        val currentRate = RateUtils.computeRate(latestRates, base, target)
        val text = if (target == "KRW") {
            "현재 환율: 1 $base = ${String.format("%,.2f", currentRate)}원"
        } else {
            "현재 환율: 1 $base = ${String.format("%,.4f", currentRate)} $target"
        }
        binding?.textCurrentRate?.text = text
    }

    private fun computeRate(base: String, target: String): Double {
        if (latestRates.isEmpty()) return 0.0
        return RateUtils.computeRate(latestRates, base, target)
    }

    private fun maybeNotify(alert: Alert) {
        val meetsCondition = run {
            val current = computeRate(alert.baseCurrency, alert.targetCurrency)
            if (current == 0.0) false
            else if (alert.condition == "below") current <= alert.targetRate else current >= alert.targetRate
        }
        val title = "환율 알림 저장됨"
        val content = if (meetsCondition) {
            "${alert.baseCurrency}→${alert.targetCurrency} ${alert.targetRate} 달성! 현재 ${String.format("%,.2f", computeRate(alert.baseCurrency, alert.targetCurrency))}"
        } else {
            "${alert.baseCurrency}→${alert.targetCurrency} ${alert.targetRate} 조건으로 알림 설정 완료"
        }
        sendNotification(title, content)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("fx_alerts", "환율 알림", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            Toast.makeText(requireContext(), "알림 권한을 허용하면 알림을 받을 수 있어요.", Toast.LENGTH_SHORT).show()
            return
        }
        val notification = NotificationCompat.Builder(requireContext(), "fx_alerts")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(requireContext()).notify((0..9999).random(), notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Spinner 확장 함수: 선택 시 콜백 실행
    private fun android.widget.Spinner.setOnItemSelectedListener(onSelected: () -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                onSelected()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }
}
