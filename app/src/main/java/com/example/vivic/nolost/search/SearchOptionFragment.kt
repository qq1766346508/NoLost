package com.example.vivic.nolost.search

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.bean.Goods.Companion.TYPE_ALL
import com.example.vivic.nolost.bean.Goods.Companion.TYPE_FOUND
import com.example.vivic.nolost.bean.Goods.Companion.TYPE_LOST
import com.example.vivic.nolost.fragment.BaseDialog
import com.example.vivic.nolost.lost.fragment.LostViewModel
import kotlinx.android.synthetic.main.fragment_search_option.*

class SearchOptionFragment : BaseDialog() {
    override fun getTagName(): String {
        return TAG
    }

    private var rootView: View? = null
    private var lostViewModel: LostViewModel? = null

    companion object {

        val TAG = SearchOptionFragment::class.java.simpleName
        val instance: SearchOptionFragment
            get() = SearchOptionFragment()
    }

    private val inputMethodManager: InputMethodManager by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_search_option, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lostViewModel = ViewModelProviders.of(activity!!).get(LostViewModel::class.java)
        initview()
        loadData()
    }

    private fun initview() {
        btn_option_cancel.setOnClickListener { dismissAllowingStateLoss() }
        rg_option_goodstype.check(rb_option_goods_all.id)
        et_option_goodslocation.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                queryByOption()
            }
            false
        }
        btn_option_confirm.setOnClickListener {
            queryByOption()
        }
    }

    private fun loadData() {
        lostViewModel?.lastCheck?.let {
            if (it != 0) {
                rg_option_goodstype.check(it)
            }
        }
        lostViewModel?.lastName?.let {
            et_option_goodsname.setText(it)
        }
        lostViewModel?.lastLocation?.let {
            et_option_goodslocation.setText(it)
        }
    }

    private fun queryByOption() {
        val goods = Goods().apply {
            this.type = when (rg_option_goodstype.checkedRadioButtonId) {
                rb_option_goods_all.id -> TYPE_ALL
                rb_option_goods_lost.id -> TYPE_LOST
                rb_option_goods_found.id -> TYPE_FOUND
                else -> {
                    ""
                }
            }
            this.name = et_option_goodsname.text.toString()
            this.location = et_option_goodslocation.text.toString()
        }
        lostViewModel?.optionGoods?.value = goods
        inputMethodManager.hideSoftInputFromWindow(et_option_goodslocation!!.windowToken, 0)
        lostViewModel?.lastCheck = rg_option_goodstype.checkedRadioButtonId
        lostViewModel?.lastName = et_option_goodsname.text.toString()
        lostViewModel?.lastLocation = et_option_goodslocation.text.toString()
        dismissAllowingStateLoss()
    }


}
