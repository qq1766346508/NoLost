package com.example.vivic.nolost.search;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;

public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private TextInputEditText edSearchContent;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
    }

    private void initView() {
        ImageView ivBack = findViewById(R.id.iv_search_back);
        ivBack.setOnClickListener(v -> finish());
        edSearchContent = findViewById(R.id.ed_search_content);
        edSearchContent.requestFocus();
        inputMethodManager.showSoftInput(edSearchContent, 0);
        Button btnOption = findViewById(R.id.btn_search_option);
        btnOption.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showSearchOption();
            }

            @Override
            protected void onDoubleClick() {

            }
        });
    }

    private void showSearchOption() {
        Log.d(TAG, "showSearchOption: ");
        Fragment searchOption = getSupportFragmentManager().findFragmentByTag("SearchOption");
        if (searchOption != null && searchOption.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(searchOption).commitAllowingStateLoss();
        } else {
            SearchOption.getInstance().show(getFragmentManager(), "SearchOption");
        }
    }
}
