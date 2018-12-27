package com.example.vivic.nolost.Lost.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.Login.LoginActivity;

public class LostFragment extends Fragment {
    private static final String TAG = LostFragment.class.getSimpleName();

    public static LostFragment newInstance(Bundle bundle) {
        LostFragment lostFragment = new LostFragment();
        lostFragment.setArguments(bundle);
        return lostFragment;
    }

    private View rootView;
    private RecyclerView recyclerView;

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
       recyclerView=rootView.findViewById(R.id.rv_goods);


    }

}

