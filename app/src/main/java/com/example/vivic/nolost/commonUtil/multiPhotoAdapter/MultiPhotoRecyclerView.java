package com.example.vivic.nolost.commonUtil.multiPhotoAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;


/**
 * 停止滑动状态下，甩出， 才会加载图片的recyclerview
 * 拖拽时，不加载dd
 */
public class MultiPhotoRecyclerView extends RecyclerView {

    private Context context;

    public MultiPhotoRecyclerView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MultiPhotoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MultiPhotoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                    case SCROLL_STATE_SETTLING:
                        Glide.with(context).resumeRequests();
                        break;
                    default:
                        Glide.with(context).pauseRequests();
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}
