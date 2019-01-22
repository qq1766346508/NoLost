package com.example.vivic.nolost.lost;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivic.nolost.GlideApp;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Goods;
import com.goyourfly.multi_picture.MultiPictureView;

import java.util.ArrayList;
import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {

    private static final String TAG = GoodsAdapter.class.getSimpleName();
    private List<Goods> goodsList = new ArrayList<>();
    private Context context;

    public GoodsAdapter(List<Goods> goodsList, Context context) {
        this.context = context;
        this.goodsList = goodsList;
    }

    public GoodsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false);
        GoodsViewHolder holder = new GoodsViewHolder(itemView);
        context = parent.getContext();
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
        int positionStart = goodsList.size();
        goodsList.addAll(list);
        int itemCount = list.size();
        notifyItemRangeChanged(positionStart, itemCount);
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCreatorAvatar;
        TextView tvCreatorName;
        TextView tvGoodsTime;
        TextView tvGoodsLocation;
        TextView tvGoodsName;
        TextView tvGoodsDetail;
        MultiPictureView mpvGoodsPhoto;


        public GoodsViewHolder(View itemView) {
            super(itemView);
            ivCreatorAvatar = itemView.findViewById(R.id.iv_item_goods_avatar);
            tvCreatorName = itemView.findViewById(R.id.tv_item_goods_createor);
            tvGoodsTime = itemView.findViewById(R.id.tv_item_goods_time);
            tvGoodsLocation = itemView.findViewById(R.id.tv_item_goods_location);
            tvGoodsName = itemView.findViewById(R.id.tv_item_goods_name);
            tvGoodsDetail = itemView.findViewById(R.id.tv_item_goods_details);
            mpvGoodsPhoto = itemView.findViewById(R.id.mpv_item_goods_photo);
        }

        public void initItem(Goods goods) {
            GlideApp.with(context).load(goods.getCreatorAvatar()).placeholder(R.drawable.icon_default_avatar).into(ivCreatorAvatar);
            tvCreatorName.setText(goods.getCreatorName());
            tvGoodsTime.setText(goods.getUpdatedAt());
            tvGoodsLocation.setText(goods.getLocation());
            tvGoodsName.setText("物品名称："+goods.getName());
            tvGoodsDetail.setText("物品详情："+goods.getDetail());

            if (null == goods.getLocation() || TextUtils.isEmpty(goods.getLocation())) {
                tvGoodsLocation.setVisibility(View.INVISIBLE);
            }
//            mpvGoodsPhoto.addItem(Uri.parse("https://avatars1.githubusercontent.com/u/7019862?s=88&v=4"));
        }
    }
}
