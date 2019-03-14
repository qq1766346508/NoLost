package com.example.vivic.nolost.commonUtil.progressBarDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vivic.nolost.R;

public class ProgressBarDialog extends DialogFragment {


    public static final String TAG = ProgressBarDialog.class.getSimpleName();
    private View rootView;
    private CustomCircleProgressBar pbTotalBar;
    private CustomCircleProgressBar pbSingleBar;
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
        setCancelable(false);
    }

    private void initView() {
        pbTotalBar = rootView.findViewById(R.id.pb_progress_total);
        tvTotalBar = rootView.findViewById(R.id.tv_progress_total);
        pbSingleBar = rootView.findViewById(R.id.pb_progress_single);
    }

    public void setValue(int curIndex, int curPercent, int total, int totalPercent) {
        tvTotalBar.setText(curIndex + "/" + total);
        pbTotalBar.setProgress(totalPercent);
        pbSingleBar.setProgress(curPercent);
    }

    public void show(FragmentActivity fragmentActivity) {
        try {
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(ProgressBarDialog.TAG);
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            if (this.isAdded()) {
                return;
            }
            fragmentTransaction.add(this, TAG).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
