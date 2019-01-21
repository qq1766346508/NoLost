package com.example.vivic.nolost.search

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.example.vivic.nolost.R

class SearchOption : DialogFragment() {
    private var rootView: View? = null

    companion object {

        private val TAG = SearchOption::class.java.simpleName

        val instance: SearchOption
            get() = SearchOption()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_search_option, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }

    private fun initview() {
        val btnCancel = rootView!!.findViewById<Button>(R.id.btn_option_cancel)
        btnCancel.setOnClickListener { v -> dismissAllowingStateLoss() }
    }


}
