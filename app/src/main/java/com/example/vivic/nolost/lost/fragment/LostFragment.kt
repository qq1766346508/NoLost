package com.example.vivic.nolost.lost.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobQuery
import com.example.vivic.nolost.R
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.fragment.BaseFragment
import com.example.vivic.nolost.lost.GoodsAdapter
import kotlinx.android.synthetic.main.fragment_lost.*

class LostFragment : BaseFragment() {
    companion object {
        private val TAG = LostFragment::class.java.simpleName
        private val QUERY_LIMIT = 10
        private val GOODS_UPDATE_COUNT = 1

        fun newInstance(bundle: Bundle?): LostFragment {
            val lostFragment = LostFragment()
            lostFragment.arguments = bundle
            return lostFragment
        }
    }

    private var rootView: View? = null
    private var goodsAdapter: GoodsAdapter? = null
    private var lostHelper: LostHelper? = null
    private var querySkip = 0 //步长，忽略前querySkip条数据

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_lost, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lostHelper = LostHelper()
        initview()
        initObserver()
    }


    private fun initview() {
        rv_lost_content.layoutManager = LinearLayoutManager(context)
        goodsAdapter = GoodsAdapter(context)
        rv_lost_content.adapter = goodsAdapter
        srl_lost_refresh.setOnRefreshListener { refreshLayout ->
            goodsAdapter?.clearData()
            querySkip = 0
            loadGoods()
            refreshLayout.finishRefresh(2000)
        }
        srl_lost_refresh.setOnLoadMoreListener { refreshLayout ->
            loadGoods()
            refreshLayout.finishLoadMore(2000)
        }
    }

    private fun initObserver() {
        lostHelper?.totalGoodList?.observe(this, android.arch.lifecycle.Observer {
            if (it?.size != 0) {
                goodsAdapter?.addData(it)
                querySkip += QUERY_LIMIT
            }
        })
        loadGoods()
    }


    fun loadGoods() {
        val query = BmobQuery<Goods>().apply {
            this.setLimit(QUERY_LIMIT)
            this.order("-createdAt")
            this.setSkip(querySkip)
        }
        compositeDisposable.add(lostHelper?.getGoodList(query)!!)
    }

//另一种查询方法
//    var query = BmobQuery<Goods>("Goods").apply {
//        this.setLimit(QUERY_LIMIT)
//        this.order("-createdAt")
//    }
//    DataRepository.queryDataByTable(query, object : IBmobCallback<JSONArray> {
//        override fun success(result: JSONArray?) {
//            Log.d(TAG, result.toString())
//            val goodsList = Gson().fromJson<List<Goods>>(result.toString(), object : TypeToken<List<Goods>>() {}.type)
//            for (it in goodsList) {
//                Log.d(TAG, "item:$it")
//            }
//        }
//
//        override fun error(throwable: Throwable?) {
//
//        }
//
//    })
}

