package com.example.vivic.nolost.Lost.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivic.nolost.Lost.GoodsAdapter;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.commonUtil.LeakCanaryUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class LostFragment extends Fragment {
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
            goods.name = String.valueOf(i);
            list.add(goods);
        }
        return list;
    }

    @Override
    public void onDestroy() {
        LeakCanaryUtils.watch(this);
        super.onDestroy();
    }
}

