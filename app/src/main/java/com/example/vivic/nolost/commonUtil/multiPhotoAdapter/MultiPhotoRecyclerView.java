package com.example.vivic.nolost.commonUtil.multiPhotoAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * 停止滑动状态下，甩出， 才会加载图片的recyclerview
 * 拖拽时，不加载dd
 * 2019/1/26 由于图片加载进行了压缩，不对recyclerview停止加载11
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

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                return true;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                return true;
//        }
//        return super.onInterceptTouchEvent(e);
//    }

    private void init() {
//        addOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                switch (newState) {
//                    case SCROLL_STATE_IDLE:
//                    case SCROLL_STATE_SETTLING:
//                        Glide.with(context).resumeRequests();
//                        break;
//                    default:
//                        Glide.with(context).pauseRequests();
//                        break;
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

    }
}
