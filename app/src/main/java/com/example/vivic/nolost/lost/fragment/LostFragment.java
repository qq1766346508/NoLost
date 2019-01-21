package com.example.vivic.nolost.lost.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivic.nolost.lost.GoodsAdapter;
import com.example.vivic.nolost.lost.GoodsEvent;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.commonUtil.BindEventBus;
import com.example.vivic.nolost.commonUtil.LeakCanaryUtils;
import com.example.vivic.nolost.fragment.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

@BindEventBus
public class LostFragment extends BaseFragment {
    private static final String TAG = LostFragment.class.getSimpleName();

    public static LostFragment newInstance(Bundle bundle) {
        LostFragment lostFragment = new LostFragment();
        lostFragment.setArguments(bundle);
        return lostFragment;
    }

    private View rootView;
    private RecyclerView recyclerView;
    private GoodsAdapter goodsAdapter;
    private SmartRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lost, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview();
    }

    private void initview() {
        recyclerView = rootView.findViewById(R.id.rv_goods);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goodsAdapter = new GoodsAdapter(initList(), getContext());
        recyclerView.setAdapter(goodsAdapter);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                goodsAdapter.clearData();
                goodsAdapter.addData(initList());
                refreshLayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                goodsAdapter.addData(initList());
                refreshLayout.finishLoadMore(2000);
            }
        });
    }

    private List<Goods> initList() {
        List<Goods> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Goods goods = new Goods();
            goods.setName(String.valueOf(i));
            list.add(goods);
        }
        return list;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateGoodsList(GoodsEvent goodsEvent) {
        //数据查询
        Log.d(TAG, "updateGoodsList: ");
    }

    @Override
    public void onDestroy() {
        LeakCanaryUtils.watch(this);
        super.onDestroy();
    }
}
