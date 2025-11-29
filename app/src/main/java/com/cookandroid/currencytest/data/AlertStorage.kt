package com.cookandroid.currencytest.data

import android.content.Context
import com.cookandroid.currencytest.model.Alert
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AlertStorage {
    private const val PREF_NAME = "fx_alerts_pref"
    private const val KEY_ALERTS = "alerts_json"
    private val gson = Gson()

    fun load(context: Context): MutableList<Alert> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_ALERTS, null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<List<Alert>>() {}.type
            val list: List<Alert> = gson.fromJson(json, type)
            list.toMutableList()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun save(context: Context, alerts: List<Alert>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ALERTS, gson.toJson(alerts)).apply()
    }
}
