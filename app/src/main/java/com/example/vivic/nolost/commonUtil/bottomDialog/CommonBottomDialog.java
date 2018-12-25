package com.example.vivic.nolost.commonUtil.bottomDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.vivic.nolost.R;

import java.util.ArrayList;
import java.util.List;


public class CommonBottomDialog extends BottomSheetDialog {
    private static final int BUTTON_ITEM_ID = 135798642;

    private ViewGroup mRootView;
    protected ViewGroup mContentView;

    private TextView mTitleTv;
    private TextView mCancelBtn;


    public CommonBottomDialog(Context context, final Builder builder) {
        super(context);
        mRootView = (ViewGroup) View.inflate(getContext(), R.layout.layout_common_popup_dialog, null);
        mContentView = (ViewGroup) mRootView.findViewById(R.id.ll_item);
        mTitleTv = (TextView) mRootView.findViewById(R.id.tv_title);
        mCancelBtn = (TextView) mRootView.findViewById(R.id.btn_cancel);
        setContentView(mRootView);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(params);
        if (!TextUtils.isEmpty(builder.title)) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(builder.title);
            addDivider();
        }
        for (ButtonItem buttonItem : builder.buttonItemList) {
            addItem(buttonItem);
            addDivider();
        }
        if (builder.cancelButtomShow) {
            mCancelBtn.setVisibility(View.VISIBLE);
            mCancelBtn.setText(builder.cancel == null ? "cancel" : builder.cancel);
            mCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.cancelClickListener != null) {
                        builder.cancelClickListener.onClick();
                    }
                    dismiss();
                }
            });
        }
        setCancelable(builder.cancelable);
        setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
    }

    public void addItem(final ButtonItem buttonItem) {
        if (buttonItem == null) {
            return;
        }
        View item = LayoutInflater.from(getContext()).inflate(buttonItem.resourceID, mContentView, false);
        TextView tv = item.findViewById(R.id.tv_content);
        tv.setText(buttonItem.mText);
        tv.setGravity(Gravity.CENTER);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonItem.mClickListener != null) {
                    buttonItem.mClickListener.onClick();
                }
                dismiss();
            }
        });
        item.setId(BUTTON_ITEM_ID + mContentView.getChildCount());

        if (buttonItem.mDrawableLeftResId != 0) {
            Drawable dra = getContext().getResources().getDrawable(buttonItem.mDrawableLeftResId);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
            tv.setCompoundDrawables(dra, null, null, null);

        }
        mContentView.addView(item, mContentView.getChildCount());
    }

    public void addDivider() {
        View divider = LayoutInflater.from(getContext()).inflate(R.layout.layout_common_popup_dialog_divider, mContentView, false);
        mContentView.addView(divider, mContentView.getChildCount());
    }

    public static class Builder {
        public Builder(Context context) {
            this.context = context;
        }

        private Context context;
        private String title;   //标题，默认隐藏
        private String cancel;  //取消文字,默认为cancel
        private ButtonItem.OnClickListener cancelClickListener; //取消的点击
        private boolean canceledOnTouchOutside = true;  //默认能点击外部取消
        private boolean cancelable = true;  //默认可以取消
        private boolean cancelButtomShow = true;    //取消按钮是否展示，默认展示
        private List<ButtonItem> buttonItemList = new ArrayList<>();


        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder cancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder cancel(String cancel, ButtonItem.OnClickListener onCancelClickListener) {
            this.cancel = cancel;
            this.cancelClickListener = cancelClickListener;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder cancelButtomShow(boolean cancelButtomShow) {
            this.cancelButtomShow = cancelButtomShow;
            return this;
        }

        public Builder item(String title, ButtonItem.OnClickListener onItemClickListener) {
            this.buttonItemList.add(new ButtonItem(title, onItemClickListener));
            return this;
        }

        public Builder item(ButtonItem buttonItem) {
            this.buttonItemList.add(buttonItem);
            return this;
        }

        public Builder items(List<ButtonItem> buttonItems) {
            this.buttonItemList.addAll(buttonItems);
            return this;
        }

        public CommonBottomDialog build() {
            return new CommonBottomDialog(context, this);
        }


    }
}
