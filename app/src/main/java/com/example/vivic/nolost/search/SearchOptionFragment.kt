package com.example.vivic.nolost.search

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.example.vivic.nolost.R

class SearchOptionFragment : DialogFragment() {
    private var rootView: View? = null

    companion object {

        val TAG = SearchOptionFragment::class.java.simpleName

        val instance: SearchOptionFragment
            get() = SearchOptionFragment()
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
        btnCancel.setOnClickListener { dismissAllowingStateLoss() }
    }


}
