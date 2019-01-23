package com.example.vivic.nolost.lost.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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

        fun newInstance(loadModel: Int): LostFragment {
            val fragment = LostFragment()
            fragment.arguments = Bundle().apply {
                this.putInt(com.example.vivic.nolost.lost.fragment.LoadModel.LOAD_MODEL, loadModel)
            }
            return fragment
        }
    }

    private var rootView: View? = null
    private var goodsAdapter: GoodsAdapter? = null
    private var lostViewModel: LostViewModel? = null
    private var querySkip = 0 //步长，忽略前querySkip条数据
    private var key: String? = null
    private var optionGoods: Goods? = null
    private var loadModel: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loadModel = it.getInt(LoadModel.LOAD_MODEL)
            Log.d(TAG, "loadModel = $loadModel")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_lost, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lostViewModel = ViewModelProviders.of(activity!!).get(LostViewModel::class.java)
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
            when (loadModel) {
                LoadModel.LOAD_MODEL_NORMAL -> loadGoods()
                LoadModel.LOAD_MODEL_SEARCH -> loadGoodsByKey(key, true)
                LoadModel.LOAD_MODEL_OPTION -> loadGoodsByOption(optionGoods!!, true)
            }
            refreshLayout.finishRefresh(2000)
        }
        srl_lost_refresh.setOnLoadMoreListener { refreshLayout ->
            when (loadModel) {
                LoadModel.LOAD_MODEL_NORMAL -> loadGoods()
                LoadModel.LOAD_MODEL_SEARCH -> loadGoodsByKey(key, false)
                LoadModel.LOAD_MODEL_OPTION -> loadGoodsByOption(optionGoods!!, false)
            }
            refreshLayout.finishLoadMore(2000)
        }
    }

    private fun initObserver() {
        lostViewModel?.totalGoodList?.observe(this, android.arch.lifecycle.Observer {
            if (it?.size != 0) {
                goodsAdapter?.addData(it)
                querySkip += QUERY_LIMIT
            }
        })
        when (loadModel) { //首次进入调用
            LoadModel.LOAD_MODEL_NORMAL -> loadGoods()
//            LoadModel.LOAD_MODEL_SEARCH -> loadGoodsByKey(key, true)
//            LoadModel.LOAD_MODEL_OPTION -> loadGoodsByOption(optionGoods!!,true)
        }
        lostViewModel?.optionGoods?.observe(this, Observer<Goods> { it ->
            //如果optionGoods,发生了改变则要清空列表
            it?.let {
                loadGoodsByOption(it, true)
            }
        })
    }

    /**
     * 无条件搜索，一般用在首页加载
     */
    fun loadGoods() {
        val query = BmobQuery<Goods>().apply {
            this.setLimit(QUERY_LIMIT)
            this.order("-createdAt")
            this.setSkip(querySkip)
        }
        addSubscribe(lostViewModel?.getGoodList(query)!!)
    }

    /**
     * 通过精准筛选过滤，每次筛选或者下拉刷新都要清空列表，步长归零
     */
    fun loadGoodsByOption(goods: Goods, cleanList: Boolean) {
        loadModel = LoadModel.LOAD_MODEL_OPTION
        if (cleanList) {
            goodsAdapter?.clearData()
            querySkip = 0
        }
        this.optionGoods = goods
        val q1 = BmobQuery<Goods>().apply {
            if (!goods.type?.isEmpty()!!) {
                this.addWhereEqualTo("type", goods.type)
            }
        }
        val q2 = BmobQuery<Goods>().apply {
            if (!goods.name?.isEmpty()!!) {
                this.addWhereEqualTo("name", goods.name)
            }
        }
        val q3 = BmobQuery<Goods>().apply {
            if (!goods.location?.isEmpty()!!) {
                this.addWhereEqualTo("location", goods.location)
            }
        }
        val mainQuery = BmobQuery<Goods>().apply {
            this.and(mutableListOf(q1, q2, q3))
            this.setLimit(QUERY_LIMIT)
            this.order("-createdAt")
            this.setSkip(querySkip)
        }
        addSubscribe(lostViewModel?.getGoodList(mainQuery)!!)
    }

    /**
     * 根据关键字准确搜索，每次筛选或者下拉刷新都要清空列表，步长归零
     * key:关键字
     */
    fun loadGoodsByKey(key: String?, cleanList: Boolean) {
        key?.let {
            loadModel = LoadModel.LOAD_MODEL_SEARCH
            if (cleanList) { //再次搜索需要清空列表
                goodsAdapter?.clearData()
                querySkip = 0
            }
            this.key = key //用于刷新用
            val q1 = BmobQuery<Goods>().apply {
                this.addWhereEqualTo("name", key)
            }
            val q2 = BmobQuery<Goods>().apply {
                this.addWhereEqualTo("location", key)
            }
            val q3 = BmobQuery<Goods>().apply {
                this.addWhereEqualTo("detail", key)
            }
            val mainQuery = BmobQuery<Goods>().apply {
                this.or(mutableListOf(q1, q2, q3))
                this.setLimit(QUERY_LIMIT)
                this.order("-createdAt")
                this.setSkip(querySkip)
            }
            addSubscribe(lostViewModel?.getGoodList(mainQuery)!!)
        }
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

