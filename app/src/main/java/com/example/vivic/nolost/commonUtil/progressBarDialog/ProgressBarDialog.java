package com.example.vivic.nolost.commonUtil.progressBarDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vivic.nolost.R;

public class ProgressBarDialog extends DialogFragment {


    public static final String TAG = ProgressBarDialog.class.getSimpleName();
    private View rootView;
    private ProgressBar pbSingleBar;
    private ProgressBar pbTotalBar;
    private TextView tvSingleBar;
    private TextView tvTotalBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_progress_bar_dialog, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        pbSingleBar = rootView.findViewById(R.id.rb_progress_single);
        pbTotalBar = rootView.findViewById(R.id.rb_progress_total);
        tvSingleBar = rootView.findViewById(R.id.tv_progress_single);
        tvTotalBar = rootView.findViewById(R.id.tv_progress_total);
    }

    public void setValue(int curIndex, int curPercent, int total, int totalPercent) {
        tvSingleBar.setText("当前上传第:" + curIndex + "个");
        pbSingleBar.setProgress(curPercent);
        tvTotalBar.setText("一共：" + curIndex + "/" + total);
        pbTotalBar.setProgress(totalPercent);
    }

}
