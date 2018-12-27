package com.example.vivic.nolost.commonUtil;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by anwei on 2017/4/27.
 */

public abstract class NoDoubleClickListener implements OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 800;
    private long lastClickTime;

    protected abstract void onNoDoubleClick(View v);

    protected abstract void onDoubleClick();

    @Override
    public void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();
        long delayTime = currentTime - lastClickTime;
        if (delayTime > MIN_CLICK_DELAY_TIME) {
            onNoDoubleClick(v);
            lastClickTime = currentTime;
        } else {
            onDoubleClick();
        }
    }
}
