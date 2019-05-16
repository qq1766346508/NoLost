package com.example.vivic.nolost.lost.fragment;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;
import android.util.Log;

import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.bmob.DataRepository;
import com.example.vivic.nolost.commonUtil.NetworkUtil;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.disposables.Disposable;

public class LostViewModel extends ViewModel {

    public static final int QUERY_LIMIT = 10; //每次请求的数据条数

    private static final String TAG = LostViewModel.class.getSimpleName();
    public MutableLiveData<List<Goods>> totalGoodList = new MutableLiveData<>();
    public MutableLiveData<String> key = new MutableLiveData<>();
    public MutableLiveData<Goods> optionGoods = new MutableLiveData<>();

    public int lastCheck;
    public String lastName;
    public String lastLocation;
    public int querySkip;
    public boolean firstLoad = false;


    public Disposable getGoodList(BmobQuery<Goods> bmobQuery) {
        boolean isCache = bmobQuery.hasCachedResult(Goods.class);
        if (!NetworkUtil.isConnected()) {
            ToastUtil.showToast("当前无网络");
            return null;
        }

        return DataRepository.INSTANCE.queryData(bmobQuery, new FindListener<Goods>() {
            @Override
            public void done(List<Goods> list, BmobException e) {
                if (e == null) {
                    for (Goods goods : list) {
                        Log.i(TAG, "\ngetGoodList success: " + goods.toString());
                    }
                    totalGoodList.setValue(list);
                } else {
                    Log.i(TAG, "getGoodList failed: BmobException:" + e.toString());
                }
            }
        });
    }

    /**
     * 无条件搜索，一般用在首页加载
     */
    public Disposable loadGoods() {
        Log.d(TAG, "loadGoods: ");
        BmobQuery<Goods> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(QUERY_LIMIT);
        bmobQuery.order("-createdAt");
        bmobQuery.setSkip(querySkip);
        if (!firstLoad) {
            firstLoad = true;
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        return getGoodList(bmobQuery);
    }

    public Disposable loadGoodsByUser(String creatorObjectId) {
        BmobQuery<Goods> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(QUERY_LIMIT);
        bmobQuery.order("-createdAt");
        bmobQuery.setSkip(querySkip);
        bmobQuery.addWhereEqualTo("creatorObjectId", creatorObjectId);
        return getGoodList(bmobQuery);
    }


    /**
     * and查询
     * 通过精准筛选过滤
     */
    public Disposable loadGoodsByOption() {
        Log.d(TAG, "loadGoodsByOption: option = " + optionGoods.getValue().toString());
        Goods goods = optionGoods.getValue();
        List<BmobQuery<Goods>> list = new ArrayList<>();
        BmobQuery<Goods> q1 = new BmobQuery<>();
        if (!TextUtils.isEmpty(goods.getType())) {
            q1.addWhereEqualTo("type", goods.getType());
            list.add(q1);
        }
        BmobQuery<Goods> q2 = new BmobQuery<>();
        if (!TextUtils.isEmpty(goods.getName())) {
            q2.addWhereEqualTo("name", goods.getName());
            list.add(q2);

        }
        BmobQuery<Goods> q3 = new BmobQuery<>();
        if (!TextUtils.isEmpty(goods.getLocation())) {
            q3.addWhereEqualTo("location", goods.getLocation());
            list.add(q3);
        }
        BmobQuery<Goods> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(QUERY_LIMIT);
        bmobQuery.order("-createdAt");
        bmobQuery.setSkip(querySkip);
        bmobQuery.and(list);
        return getGoodList(bmobQuery);
    }

    /**
     * or查询
     * 根据关键字准确搜索，每次筛选或者下拉刷新都要清空列表，步长归零
     * key:关键字
     */
    public Disposable loadGoodsByKey() {
        Log.d(TAG, "loadGoodsByKey: key = " + key.getValue().toString());
        String optionKey = key.getValue();
        if (!TextUtils.isEmpty(optionKey)) {
            List<BmobQuery<Goods>> list = new ArrayList<>();
            BmobQuery<Goods> q1 = new BmobQuery<>();
            q1.addWhereEqualTo("name", optionKey);
            list.add(q1);
            BmobQuery<Goods> q2 = new BmobQuery<>();
            q2.addWhereEqualTo("location", optionKey);
            list.add(q2);
            BmobQuery<Goods> q3 = new BmobQuery<>();
            q3.addWhereEqualTo("detail", optionKey);
            list.add(q3);
            BmobQuery<Goods> q4 = new BmobQuery<>();
            q4.addWhereEqualTo("creatorName", optionKey);
            list.add(q4);
            BmobQuery<Goods> mainQuery = new BmobQuery<>();
            mainQuery.or(list);
            mainQuery.setLimit(QUERY_LIMIT);
            mainQuery.order("-createdAt");
            mainQuery.setSkip(querySkip);
            return getGoodList(mainQuery);
        } else {
            return null;
        }
    }
}


