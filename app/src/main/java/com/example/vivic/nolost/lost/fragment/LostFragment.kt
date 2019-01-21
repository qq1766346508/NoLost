package com.example.vivic.nolost.lost.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobQuery
import com.example.vivic.nolost.R
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.commonUtil.BindEventBus
import com.example.vivic.nolost.fragment.BaseFragment
import com.example.vivic.nolost.lost.GoodsAdapter
import com.example.vivic.nolost.lost.GoodsEvent
import kotlinx.android.synthetic.main.fragment_lost.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@BindEventBus
class LostFragment : BaseFragment() {
    companion object {
        private val TAG = LostFragment::class.java.simpleName

        fun newInstance(bundle: Bundle?): LostFragment {
            val lostFragment = LostFragment()
            lostFragment.arguments = bundle
            return lostFragment
        }
    }

    private var rootView: View? = null
    private var goodsAdapter: GoodsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_lost, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }

    private fun initview() {
        rv_lost_content.layoutManager = LinearLayoutManager(context)
        goodsAdapter = GoodsAdapter(initList(), context)
        rv_lost_content.adapter = goodsAdapter
        srl_lost_refresh.setOnRefreshListener { refreshLayout ->
            goodsAdapter!!.clearData()
            goodsAdapter!!.addData(initList())
            refreshLayout.finishRefresh(2000)
        }
        srl_lost_refresh.setOnLoadMoreListener { refreshLayout ->
            goodsAdapter!!.addData(initList())
            refreshLayout.finishLoadMore(2000)
        }
    }

    private fun initList(): List<Goods> {
        val list = ArrayList<Goods>()
        for (i in 0..9) {
            val goods = Goods()
            goods.name = i.toString()
            list.add(goods)
        }
        return list
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initGoodsList(goodsEvent: GoodsEvent) {
        val list = mutableListOf<Goods>()
        val query = BmobQuery<Goods>()
        query.setLimit(3)


    }


}
