package com.cookandroid.currencytest.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object AlertWorkScheduler {
    private const val UNIQUE_NAME = "fx_alert_worker"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<AlertCheckWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }
}
