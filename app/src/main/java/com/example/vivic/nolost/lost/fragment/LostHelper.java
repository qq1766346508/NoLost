package com.example.vivic.nolost.lost.fragment;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.bmob.DataRepository;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.disposables.Disposable;

public class LostHelper {
    private static final String TAG = LostHelper.class.getSimpleName();
    public MutableLiveData<List<Goods>> totalGoodList = new MutableLiveData<>();

    public Disposable getGoodList(BmobQuery<Goods> bmobQuery) {
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
                    totalGoodList.setValue(null);
                }
            }
        });
    }

}
