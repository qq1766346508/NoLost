package com.example.vivic.nolost.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivic.nolost.R;

public class FoundFragment extends Fragment {
    private static final String TAG = FoundFragment.class.getSimpleName();

    public static FoundFragment newInstance(Bundle bundle) {
        FoundFragment foundFragment = new FoundFragment();
        foundFragment.setArguments(bundle);
        return foundFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_found, container, false);
    }
}
