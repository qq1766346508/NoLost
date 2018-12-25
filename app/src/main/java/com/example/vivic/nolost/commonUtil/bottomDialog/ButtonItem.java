package com.example.vivic.nolost.commonUtil.bottomDialog;


import com.example.vivic.nolost.R;

public class ButtonItem {
    public static final int BUTTON_TYPE_NORMAL = 0;
    public static final int BUTTON_TYPE_CANCEL = 1;
    public static final int BUTTON_TYPE_WARN = 2;

    public String mText;
    public int resourceID;
    public OnClickListener mClickListener;
    public int mButtonType;

    public int mDrawableLeftResId = 0;

    public ButtonItem(String text, OnClickListener l) {
        this(text, BUTTON_TYPE_NORMAL, 0, l);
    }

    public ButtonItem(String text, int drawableLeftResId, OnClickListener l) {
        this(text, BUTTON_TYPE_NORMAL, drawableLeftResId, l);
    }

    public ButtonItem(String text, int buttonType, int drawableLeftResId, OnClickListener l) {
        mText = text;
        mClickListener = l;
        mButtonType = buttonType;
        mDrawableLeftResId = drawableLeftResId;
        if (buttonType == BUTTON_TYPE_WARN) {
            resourceID = R.layout.layout_common_popup_dialog_warn_button;
        } else {
            resourceID = R.layout.layout_common_popup_dialog_button;
        }
    }

    public void setText(String text) {
        this.mText = text;
    }

    public interface OnClickListener {
        void onClick();
    }
}