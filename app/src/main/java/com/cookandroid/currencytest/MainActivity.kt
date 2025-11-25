package com.cookandroid.currencytest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.currencytest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), HomeFragment.OnCurrencySelectedListener {

    var selectedCurrencyCode: String = "USD"

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val converterFragment = ConverterFragment.newInstance(selectedCurrencyCode)
    private val alertFragment = AlertFragment()
    private val aiFragment = AiFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> openFragment(homeFragment)
                R.id.nav_converter -> openFragment(converterFragment)
                R.id.nav_alert -> openFragment(alertFragment)
                R.id.nav_ai -> openFragment(aiFragment)
                else -> false
            }
        }

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_home
            openFragment(homeFragment)
        }
    }

    private fun openFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

    override fun onCurrencySelected(code: String) {
        val trimmedCode = code.split(" ").firstOrNull().orEmpty().ifEmpty { "USD" }
        selectedCurrencyCode = trimmedCode
        converterFragment.setSelectedCurrency(trimmedCode)
        binding.bottomNav.selectedItemId = R.id.nav_converter
        openFragment(converterFragment)
    }

    fun openAlertTab() {
        binding.bottomNav.selectedItemId = R.id.nav_alert
        openFragment(alertFragment)
    }
}
