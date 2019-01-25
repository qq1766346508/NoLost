package com.example.vivic.nolost.search

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.lost.fragment.LoadMode
import com.example.vivic.nolost.lost.fragment.LostFragment
import com.example.vivic.nolost.lost.fragment.LostViewModel
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    companion object {
        private val TAG = SearchActivity::class.java.simpleName
    }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private var lostFragment: LostFragment? = null
    private var lostViewModel: LostViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        lostViewModel = ViewModelProviders.of(this).get(LostViewModel::class.java)
        initView()
    }

    private fun initView() {
        iv_search_back.setOnClickListener { finish() }
        ed_search_content.requestFocus()
        ed_search_content.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                queryByKey()
            }
            false
        }
        inputMethodManager.showSoftInput(ed_search_content, 0)
        btn_search_option.setOnClickListener { showSearchOption() }
        iv_search_go.setOnClickListener { queryByKey() }
        lostFragment = LostFragment.newInstance(LoadMode.LOAD_MODE_SEARCH)
        supportFragmentManager?.beginTransaction()?.replace(R.id.search_container, lostFragment!!)?.commitAllowingStateLoss()
    }

    private fun showSearchOption() {
        Log.d(TAG, "showSearchOptionFragment: ")
        SearchOptionFragment.instance.show(this)
    }


    private fun queryByKey() {
        val key = ed_search_content.text.toString()
        if (!key.isEmpty()) {
            inputMethodManager.hideSoftInputFromWindow(ed_search_content!!.windowToken, 0)
            lostViewModel?.key?.value = key
        }
    }


}
