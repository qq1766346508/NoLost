package com.example.vivic.nolost.Lost;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Goods;
import com.goyourfly.multi_picture.MultiPictureView;

import java.util.ArrayList;
import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {

    private static final String TAG = GoodsAdapter.class.getSimpleName();
    private List<Goods> goodsList;
    private Context context;

    public GoodsAdapter(List<Goods> goodsList, Context context) {
        this.context = context;
        if (goodsList.size() == 0) {
            this.goodsList = new ArrayList<>();
        } else {
            this.goodsList = goodsList;
        }
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false);
        GoodsViewHolder holder = new GoodsViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        if (goodsList.size() != 0) {
            Goods goods = goodsList.get(position);
            holder.initItem(goods);
        }
    }

    @Override
    public int getItemCount() {
        return goodsList == null ? 0 : goodsList.size();
    }

    public void clearData() {
        goodsList.clear();
        notifyDataSetChanged();
    }

    public void addData(List<Goods> list) {
        goodsList.addAll(list);
        notifyDataSetChanged();
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        ImageView ivUserAvatar;
        TextView tvNickName;
        MultiPictureView mutiIvPhoto;


        public GoodsViewHolder(View itemView) {
            super(itemView);
            tvNickName = itemView.findViewById(R.id.tv_goods_createor);
            mutiIvPhoto = itemView.findViewById(R.id.mutiIvPhoto);
        }

        public void initItem(Goods goods) {
            tvNickName.setText(goods.name);

            mutiIvPhoto.addItem(Uri.parse("https://avatars1.githubusercontent.com/u/7019862?s=88&v=4"));
        }
    }
}
