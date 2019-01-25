package com.example.vivic.nolost.lost.fragment;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.bmob.DataRepository;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.disposables.Disposable;

public class LostViewModel extends ViewModel {
    private static final String TAG = LostViewModel.class.getSimpleName();
    public MutableLiveData<List<Goods>> totalGoodList = new MutableLiveData<>();
    public MutableLiveData<String> key = new MutableLiveData<>();
    public MutableLiveData<Goods> optionGoods = new MutableLiveData<>();

    public int lastCheck;
    public String lastName;
    public String lastLocation;


    public Disposable getGoodList(BmobQuery<Goods> bmobQuery) {
        boolean isCache = bmobQuery.hasCachedResult(Goods.class);

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

}