package com.example.vivic.nolost.lost;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.bean.MyUser;
import com.example.vivic.nolost.bmob.DataRepository;
import com.example.vivic.nolost.bmob.IBmobCallback;
import com.example.vivic.nolost.commonUtil.confirmDialog.ConfirmDialog;
import com.example.vivic.nolost.login.LogOutEvent;
import com.example.vivic.nolost.login.UserEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bmob.v3.BmobUser;

public class MenuView extends FrameLayout {
    private static final String TAG = "MenuView";
    private Context context;
    private TextView tvDelete;
    private TextView tvShare;
    private View div;
    private Goods goods;
    private DeleteCallback deleteCallback;


    public MenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_more, this);
        tvDelete = view.findViewById(R.id.tv_item_delete);
        tvShare = view.findViewById(R.id.tv_item_share);
        tvDelete.setOnClickListener(v -> {
                    new ConfirmDialog.Builder()
                            .confirmListener(new ConfirmDialog.Builder.ConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    delete();
                                }
                            })
                            .content("确定删除？")
                            .build()
                            .show(context);
                }
        );
        tvShare.setOnClickListener(v -> share());
    }

    public void delete() {
        DataRepository.INSTANCE.deleteByObjectId(goods, new IBmobCallback<String>() {
            @Override
            public void success(@org.jetbrains.annotations.Nullable String result) {
                if (deleteCallback != null) {
                    deleteCallback.success();
                }
            }

            @Override
            public void error(@org.jetbrains.annotations.Nullable Throwable throwable) {
                if (deleteCallback != null) {
                    deleteCallback.fail(throwable);
                }
            }
        });
    }

    public void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(goods.getPhotoList().get(0)));
        String text = "物品名称：" + goods.getName() + "\n物品详情：" + goods.getDetail() + "\n丢失地点：" + goods.getLocation();
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, goods.getName());
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "share"));
    }

    interface DeleteCallback {
        void success();

        void fail(Throwable throwable);
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
        updateDeleteVisiblity();
    }

    private void updateDeleteVisiblity() {
        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
        if (currentUser != null && currentUser.getObjectId().equals(goods.getCreatorObjectId())) {
            tvDelete.setVisibility(VISIBLE);
            Log.i(TAG, "showDelete: ");
        } else {
            tvDelete.setVisibility(GONE);
            Log.i(TAG, "hideDelete: ");
        }
    }

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDelete(UserEvent userEvent) {
        if (userEvent.loginResult) {
            updateDeleteVisiblity();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hideDelete(LogOutEvent event) {
        updateDeleteVisiblity();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow: ");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
        EventBus.getDefault().unregister(this);
    }
}
