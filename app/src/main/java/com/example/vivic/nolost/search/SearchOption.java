package com.example.vivic.nolost.search;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vivic.nolost.R;

public class SearchOption extends DialogFragment {

    private static final String TAG = SearchOption.class.getSimpleName();
    private View rootView;

    public static SearchOption getInstance() {
        SearchOption searchOption = new SearchOption();
        return searchOption;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_option, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview();
    }

    private void initview() {
        Button btnCancel = rootView.findViewById(R.id.btn_option_cancel);
        btnCancel.setOnClickListener(v -> dismissAllowingStateLoss());
    }
}
