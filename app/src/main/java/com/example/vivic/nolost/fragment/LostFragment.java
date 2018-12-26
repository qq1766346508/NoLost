package com.example.vivic.nolost.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Person;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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
//                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        //test bean

//        Person person = new Person();
//        person.nickName = "小明";
//        person.portrait = "https://avatar.csdn.net/C/8/C/3_carson_ho.jpg";
//        person.school = "中山大学";
//        person.sex = "男";
//        person.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    Log.d(TAG, "done: 创建数据库成功，返回objectId=" + s);
//                } else {
//                    Log.d(TAG, "done: 创建数据库失败+" + e.toString());
//                }
//            }
//        });

        BmobQuery<Person> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("nickName", "小明");
        bmobQuery.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: query success:" + list.get(0));
                } else {
                    Log.d(TAG, "done: query error:" + e.toString());
                }
            }
        });
    }

}

