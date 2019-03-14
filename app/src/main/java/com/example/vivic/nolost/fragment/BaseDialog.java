package com.example.vivic.nolost.fragment;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;


public abstract class BaseDialog extends DialogFragment {
    private static final String TAG = BaseDialog.class.getSimpleName();

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            Fragment lastFragment = manager.findFragmentByTag(tag);
            if (lastFragment != null) {
                ft.remove(lastFragment).commitAllowingStateLoss();
            }
            super.show(manager, tag);
            /**
             * fix
             * http://crash.yypm.com/static/list_page_details.html?reportId=5c257e53-e8d3-3807-8f36-e9872b0e26ca
             * 该问题在 ShowLivingFragment 的页面由于倒计时动画卡顿，然后再 ShowLivingFragment 页面不断点击关闭会出现该问题
             * 现在 ConfirmDialog 在commit() 后 调用 executePendingTransactions() 方法，会把当前的事务直接放到主进程，这样
             * 调用 Fragment isAdded() 方法才会真正起作用
             *
             */
            boolean execute = manager.executePendingTransactions();
            Log.i(TAG, "立即执行 " + execute);
        } catch (Exception e) {
            Log.e(TAG, "showDialog fail", e);
        }
    }

    @Override
    public void dismiss() {
        try {
            if (!isAdded()) {
                return;
            }
            super.dismissAllowingStateLoss();
        } catch (Exception e) {
            Log.e(TAG, "dismiss error", e);
        }
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public void hide() {
        try {
            dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(FragmentActivity activity) {
        if (isAdded()) {
            return;
        }
        try {
            show(activity.getSupportFragmentManager(), getTagName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void show(Fragment fragment) {
        if (isAdded()) {
            return;
        }
        try {
            show(fragment.getChildFragmentManager(), getTagName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(Context context) {
        try {
            if (context instanceof FragmentActivity) {
                show((FragmentActivity) context);
            } else {
                Log.i(TAG, "showDialog fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract String getTagName();
}
