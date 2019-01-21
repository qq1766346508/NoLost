package com.example.vivic.nolost.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    companion object {
        private val TAG = SearchActivity::class.java.simpleName
    }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
    }

    private fun initView() {
        iv_search_back.setOnClickListener { v -> finish() }
        ed_search_content.requestFocus()
        inputMethodManager.showSoftInput(ed_search_content, 0)
        btn_search_option.setOnClickListener { v -> showSearchOption() }
        val ivSearch = findViewById<ImageView>(R.id.iv_search_go)
        ivSearch.setOnClickListener { v -> av_search_loading.hide() }
    }

    private fun showSearchOption() {
        Log.d(TAG, "showSearchOption: ")
        val searchOption = supportFragmentManager.findFragmentByTag("SearchOption")
        if (searchOption != null && searchOption.isAdded) {
            supportFragmentManager.beginTransaction().show(searchOption).commitAllowingStateLoss()
        } else {
            SearchOptionFragment.instance.show(supportFragmentManager, "SearchOption")
        }
    }


}
