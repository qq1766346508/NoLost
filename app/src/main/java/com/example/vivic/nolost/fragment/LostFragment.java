package com.example.vivic.nolost.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.commonUtil.bottomDialog.ButtonItem;
import com.example.vivic.nolost.commonUtil.bottomDialog.CommonBottomDialog;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;

public class LostFragment extends Fragment {
    private static final String TAG = LostFragment.class.getSimpleName();

    public static LostFragment newInstance(Bundle bundle) {
        LostFragment lostFragment = new LostFragment();
        lostFragment.setArguments(bundle);
        return lostFragment;
    }

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lost, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview();
    }

    private void initview() {
        TextView tv = rootView.findViewById(R.id.tv_test);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ConfirmDialog.Builder().cancelText("cancel").cancelListener(new ConfirmDialog.Builder.CancelListener() {
//                    @Override
//                    public void onCancel() {
//                        ToastUtil.showToast("cancel");
//                    }
//                }).confirmText("confirm").confirmListener(new ConfirmDialog.Builder.ConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        ToastUtil.showToast("confirm");
//                    }
//                }).content("neirong").build().show(getActivity());

                new CommonBottomDialog.Builder(getContext()).item(new ButtonItem("1", new ButtonItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ToastUtil.showToast("1");
                    }
                })).item(new ButtonItem("2", new ButtonItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        ToastUtil.showToast("2");
                    }
                })).build().show();
            }
        });
    }
}
