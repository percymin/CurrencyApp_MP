package com.cookandroid.currencytest.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cookandroid.currencytest.R
import com.cookandroid.currencytest.data.AlertStorage
import com.cookandroid.currencytest.data.CurrencyCardRepository
import com.cookandroid.currencytest.model.Alert
import com.cookandroid.currencytest.util.RateUtils

class AlertCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val alerts = AlertStorage.load(applicationContext)
        if (alerts.isEmpty()) return Result.success()

        val rates = CurrencyCardRepository.fetchRealRates()
        if (rates.isEmpty()) return Result.retry()

        createChannel()
        alerts.forEach { alert ->
            val current = RateUtils.computeRate(rates, alert.baseCurrency, alert.targetCurrency)
            if (current == 0.0) return@forEach
            val met = if (alert.condition == "below") current <= alert.targetRate else current >= alert.targetRate
            if (met) {
                val content = "${alert.baseCurrency}→${alert.targetCurrency} ${String.format("%,.2f", current)} (목표 ${String.format("%,.2f", alert.targetRate)})"
                sendNotification("목표 환율 도달!", content)
            }
        }
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel("fx_alerts", "환율 알림", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notification = NotificationCompat.Builder(applicationContext, "fx_alerts")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify((0..9999).random(), notification)
    }
}
