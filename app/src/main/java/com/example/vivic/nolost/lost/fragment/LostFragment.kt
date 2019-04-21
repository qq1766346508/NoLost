package com.example.vivic.nolost.lost.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vivic.nolost.R
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import com.example.vivic.nolost.fragment.BaseFragment
import com.example.vivic.nolost.lost.GoodsAdapter
import com.example.vivic.nolost.lost.fragment.LostViewModel.QUERY_LIMIT
import kotlinx.android.synthetic.main.fragment_lost.*

/*
*丢失物品模块，在首页，搜索页，个人页重用
 */
class LostFragment : BaseFragment() {
    companion object {
        private val TAG = LostFragment::class.java.simpleName
        fun newInstance(loadModel: Int): LostFragment {
            val fragment = LostFragment()
            fragment.arguments = Bundle().apply {
                this.putInt(LoadMode.LOAD_MODE, loadModel)
            }
            return fragment
        }

        /**
         * 携带objectID查询某个用户的全部历史
         */
        fun newInstance(loadModel: Int, creatorObjectId: String?): LostFragment {
            val fragment = LostFragment()
            fragment.arguments = Bundle().apply {
                this.putInt(LoadMode.LOAD_MODE, loadModel)
                this.putString("creatorObjectId", creatorObjectId)
            }
            return fragment
        }
    }

    private var rootView: View? = null
    private var goodsAdapter: GoodsAdapter? = null
    private var lostViewModel: LostViewModel? = null
    private var loadModel: Int = -1
    private var creatorObjectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loadModel = it.getInt(LoadMode.LOAD_MODE)
            Log.d(TAG, "loadModel = $loadModel")
            creatorObjectId = it.getString("creatorObjectId")
            Log.d(TAG, "creatorObjectId = $creatorObjectId")

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_lost, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG,"onViewCreated")
        lostViewModel = ViewModelProviders.of(activity!!).get(LostViewModel::class.java)
        initview()
        initObserver()
    }


    private fun initview() {
        Log.i(TAG,"initview")
        rv_lost_content.layoutManager = LinearLayoutManager(context)
        goodsAdapter = GoodsAdapter(context)
        rv_lost_content.adapter = goodsAdapter
        rv_lost_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (Math.abs(dy) > 0) {
                    goodsAdapter?.hideItemMenu()
                }
            }
        })
        srl_lost_refresh.setEnableAutoLoadMore(true)
        srl_lost_refresh.setHeaderTriggerRate(0.5f)
        srl_lost_refresh.setOnRefreshListener { refreshLayout ->
            //每次下拉刷新都要清空列表，重新请求
            resetList()
            addSubscribe(when (loadModel) {
                LoadMode.LOAD_MODE_NORMAL -> lostViewModel?.loadGoods()
                LoadMode.LOAD_MODE_KEY -> lostViewModel?.loadGoodsByKey()
                LoadMode.LOAD_MODE_OPTION -> lostViewModel?.loadGoodsByOption()
                LoadMode.LOAD_MODE_HISTORY -> lostViewModel?.loadGoodsByUser(creatorObjectId)
                else -> {
                    null
                }
            })
            refreshLayout.finishRefresh(1000)
        }
        srl_lost_refresh.setOnLoadMoreListener { refreshLayout ->
            addSubscribe(when (loadModel) {
                LoadMode.LOAD_MODE_NORMAL -> lostViewModel?.loadGoods()
                LoadMode.LOAD_MODE_KEY -> lostViewModel?.loadGoodsByKey()
                LoadMode.LOAD_MODE_OPTION -> lostViewModel?.loadGoodsByOption()
                LoadMode.LOAD_MODE_HISTORY -> lostViewModel?.loadGoodsByUser(creatorObjectId)
                else -> {
                    null
                }
            })
            refreshLayout.finishLoadMore()
        }
        addSubscribe(when (loadModel) {//首次进入调用
            LoadMode.LOAD_MODE_NORMAL -> lostViewModel?.loadGoods()
            LoadMode.LOAD_MODE_HISTORY -> lostViewModel?.loadGoodsByUser(creatorObjectId)
            else -> {
                null
            }
        })
    }

    private fun initObserver() {
        lostViewModel?.totalGoodList?.observe(this, android.arch.lifecycle.Observer {
            Log.i(TAG,"goods list size = ${it?.size}")
            if (it?.size != 0) {
                srl_lost_refresh.resetNoMoreData()
                goodsAdapter?.addData(it)
                lostViewModel!!.querySkip += QUERY_LIMIT
            } else if (it?.size == 0) {
                srl_lost_refresh.finishLoadMoreWithNoMoreData()
                srl_lost_refresh.setEnableLoadMore(false)
                ToastUtil.showToast("查询为空")
            }
        })

        lostViewModel?.optionGoods?.observe(this, Observer<Goods> { it ->
            //如果optionGoods,发生了改变则要清空列表
            it?.let {
                resetList()
                loadModel = LoadMode.LOAD_MODE_OPTION //用作刷新用
                lostViewModel?.loadGoodsByOption()
            }
        })
        lostViewModel?.key?.observe(this, Observer { it ->
            it?.let {
                resetList()
                loadModel = LoadMode.LOAD_MODE_KEY//用作刷新用
                lostViewModel?.loadGoodsByKey()
            }
        })
    }

    /**
     * 清空列表，步长归零
     */
    private fun resetList() {
        Log.i(TAG,"resetList")
        goodsAdapter?.clearData()
        lostViewModel!!.querySkip = 0
        srl_lost_refresh.setEnableLoadMore(true)
    }

    //另一种查询方法
//var query = BmobQuery<Goods>("Goods").apply {
//    this.setLimit(QUERY_LIMIT)
//    this.order("-createdAt")
//}
//    DataRepository.queryDataByTable(query, object : IBmobCallback<JSONArray> {
//        override fun success(result: JSONArray?) {
//            Log.d(TAG, result.toString())
//
//            for (i in 0..(result!!.length() - 1)) {
//                var jsonObject = result.getJSONObject(i)
//                var test = jsonObject.getJSONObject("createor")
//                Log.i(TAG, test.getString("objectId"))
//            }
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

