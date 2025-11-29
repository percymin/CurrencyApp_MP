package com.cookandroid.currencytest


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cookandroid.currencytest.databinding.FragmentHomeBinding
import com.cookandroid.currencytest.ui.HomeCurrencyAdapter

class HomeFragment : Fragment() {

    interface OnCurrencySelectedListener {
        fun onCurrencySelected(code: String)
    }

    private var binding: FragmentHomeBinding? = null
    private var listener: OnCurrencySelectedListener? = null
    private lateinit var adapter: HomeCurrencyAdapter
    private lateinit var prefs: SharedPreferences

    // Activity와 데이터를 공유하는 ViewModel (API로 받은 데이터가 여기에 있음)
    private val viewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnCurrencySelectedListener
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentHomeBinding.inflate(inflater, container, false)
        binding = b

        // 1. 어댑터 초기화
        adapter = HomeCurrencyAdapter(mutableListOf()) { code ->
            listener?.onCurrencySelected(code)
        }

        // 2. 리사이클러뷰 설정 (2열 격자)
        b.currencyRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        b.currencyRecycler.adapter = adapter

        // 3. 데이터 관찰 (API 데이터가 들어오거나 갱신되면 실행됨)
        viewModel.currencyList.observe(viewLifecycleOwner) { _ ->
            // 데이터가 갱신되어도 현재 검색어에 맞춰 필터링 유지
            applyFilter()
        }

        // 4. [검색 기능 수정] 텍스트가 입력될 때마다 실시간으로 필터링
        b.inputSearch.doOnTextChanged { _, _, _, _ ->
            applyFilter()
        }

        // 5. 다크 모드 토글
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        b.switchDarkMode.isChecked = isDarkMode
        b.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
        }

        // 검색 버튼 클릭 시에도 필터링 (엔터키 대용)
        b.btnSearch.setOnClickListener {
            applyFilter()
        }

        return b.root
    }

    // 검색 필터링 로직
    private fun applyFilter() {
        // null 체크
        val currentBinding = binding ?: return

        val query = currentBinding.inputSearch.text.toString().trim()

        // ViewModel에 저장된 전체 리스트 가져오기
        val currentList = viewModel.currencyList.value ?: emptyList()

        // 검색어가 없으면 전체 리스트, 있으면 이름이나 코드로 필터링
        val filtered = if (query.isEmpty()) {
            currentList
        } else {
            currentList.filter {
                it.code.contains(query, true) || it.name.contains(query, true)
            }
        }

        // 어댑터에 데이터 반영
        adapter.submitList(filtered)
    }

    private fun setDarkMode(enable: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enable).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (enable) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val PREF_NAME = "currency_prefs"
        private const val KEY_DARK_MODE = "key_dark_mode"
    }
}
