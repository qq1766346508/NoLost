package com.example.vivic.nolost.commonUtil.confirmDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.vivic.nolost.R;

import java.lang.ref.WeakReference;

public class ConfirmDialog extends DialogFragment {
    public static final String TAG = "ConfirmDialog";

    private ConfirmDialog.Builder mBuilder;
    private DialogListener listener;
    private TextView tvTitle, tvContent, tvConfirm, tvCancel;

    private static ConfirmDialog newInstance(Builder builder) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setBuilder(builder);
        return dialog;
    }

    public void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (null == getDialog()) {
            onGetLayoutInflater(savedInstanceState);
        }
        super.onActivityCreated(savedInstanceState);
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    private void setStyle() {
        if (mBuilder != null && mBuilder.styleId != 0) {
            setStyle(DialogFragment.STYLE_NO_TITLE, mBuilder.styleId);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Fullscreen);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        showFullScreen();
        View root = inflater.inflate(R.layout.layout_confirm_dialog, container, false);
        initView(root);
        return root;
    }

    private void showFullScreen() {
        if (getDialog() != null && getDialog().getWindow() != null && mBuilder != null && mBuilder.showFullScreen) {
            int flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getDialog().getWindow().setFlags(flags, flags);
            getDialog().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void initView(View root) {
        if (mBuilder == null) {
            Log.i(TAG, "mBuilder==null");
            dismissAllowingStateLoss();
            return;
        }
        tvCancel = root.findViewById(R.id.tv_cancel);
        if (!TextUtils.isEmpty(mBuilder.cancel)) {
            tvCancel.setText(mBuilder.cancel);
        }
        tvCancel.setOnClickListener(v -> {
            if (mBuilder.cancelListener != null && mBuilder.cancelListener.get() != null) {
                mBuilder.cancelListener.get().onCancel();
            }
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            }, mBuilder.hideDelay);
        });
        if (mBuilder.hideCancel) {
            tvCancel.setVisibility(View.GONE);
        }

        tvConfirm = root.findViewById(R.id.tv_confirm);
        if (!TextUtils.isEmpty(mBuilder.confirm)) {
            tvConfirm.setText(mBuilder.confirm);
        }
        tvConfirm.setOnClickListener(v -> {
            if (mBuilder.confirmListener != null && mBuilder.confirmListener.get() != null) {
                mBuilder.confirmListener.get().onConfirm();
            }
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            }, mBuilder.hideDelay);
        });
        if (mBuilder.hideConfirm) {
            tvConfirm.setVisibility(View.GONE);
        }
        tvTitle = root.findViewById(R.id.tv_title);
        tvContent = root.findViewById(R.id.tv_content);

        if (!TextUtils.isEmpty(mBuilder.title)) {
            tvTitle.setText(mBuilder.title);
            tvContent.setTextColor(Color.parseColor("#80000000"));
            tvContent.setTextSize(14);
        } else {
            tvTitle.setVisibility(View.GONE);
            tvContent.setTextColor(Color.parseColor("#000000"));
            tvContent.setTextSize(18);
        }

        if (!TextUtils.isEmpty(mBuilder.content)) {
            if (mBuilder.parseContentHtml) {
                tvContent.setText(Html.fromHtml(mBuilder.content));
            } else {
                tvContent.setText(mBuilder.content);
            }
        } else {
            tvContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBuilder == null) {
            Log.i(TAG, "mBuilder==null");
            dismissAllowingStateLoss();
            return;
        }
        Dialog dialog = getDialog();
        if (dialog != null) {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(dpToPx(300), ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.setCancelable(mBuilder.canceledOnTouchOutside);
            dialog.setCanceledOnTouchOutside(mBuilder.canceledOnTouchOutside);
        }
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
            show(activity.getSupportFragmentManager(), "confirm_dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void show(Fragment fragment) {
        if (isAdded()) {
            return;
        }
        try {
            show(fragment.getChildFragmentManager(), "confirm_dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(Context context) {
        try {
            if (context instanceof FragmentActivity) {
                show((FragmentActivity) context);
            } else {
                Log.i(TAG, "showConfirmDialog fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            Fragment lastFragment = manager.findFragmentByTag(tag);
            if (lastFragment != null) {
                ft.remove(lastFragment).commitAllowingStateLoss();
            }
            super.show(manager, tag);
        } catch (Exception e) {
            Log.e("ConfirmDialog", "showConfirmDialog fail", e);
        }
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDismiss();
        }
    }

    public static class Builder {
        private String title;
        private String content;
        private boolean hideConfirm;
        private boolean hideCancel;
        private boolean parseContentHtml;
        private String confirm;
        private String cancel;
        private long hideDelay;
        private boolean canceledOnTouchOutside = true;
        private int styleId;
        private WeakReference<ConfirmListener> confirmListener;
        private WeakReference<CancelListener> cancelListener;
        private boolean showFullScreen;

        public Builder showFullScreen(boolean showFullScreen) {
            this.showFullScreen = showFullScreen;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder hideCancel(boolean hideCancel) {
            this.hideCancel = hideCancel;
            return this;
        }

        public Builder hideConfirm(boolean hideConfirm) {
            this.hideConfirm = hideConfirm;
            return this;
        }

        public Builder parseContentHtml(boolean parseContentHtml) {
            this.parseContentHtml = parseContentHtml;
            return this;
        }

        public Builder confirmText(String confirm) {
            this.confirm = confirm;
            return this;
        }

        public Builder cancelText(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder hideDeley(long delay) {
            this.hideDelay = delay;
            return this;
        }

        public Builder confirmListener(ConfirmListener listener) {
            this.confirmListener = new WeakReference<>(listener);
            return this;
        }

        public Builder cancelListener(CancelListener listener) {
            this.cancelListener = new WeakReference<>(listener);
            return this;
        }

        public Builder style(@StyleRes int styleId) {
            this.styleId = styleId;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean cancel) {
            this.canceledOnTouchOutside = cancel;
            return this;
        }

        public ConfirmDialog build() {
            return ConfirmDialog.newInstance(this);
        }

        public static abstract class ConfirmListener implements OnConfirmListener {
            @Override
            public abstract void onConfirm();
        }

        public static abstract class CancelListener implements OnCancelListener {
            @Override
            public abstract void onCancel();
        }

        private interface OnConfirmListener {
            void onConfirm();
        }

        private interface OnCancelListener {
            void onCancel();
        }
    }

    public interface DialogListener {
        void onDismiss();
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
