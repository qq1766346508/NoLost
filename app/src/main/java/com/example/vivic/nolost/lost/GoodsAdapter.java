package com.example.vivic.nolost.lost;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.GridSpacingItemDecoration;
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter;
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoRecyclerView;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;
import com.example.vivic.nolost.lost.activity.HistoryActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter.LOAD_INTERNET;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {

    private static final String TAG = GoodsAdapter.class.getSimpleName();
    private List<Goods> goodsList = new ArrayList<>();
    private Context context;
    private List<MenuView> menuViewList = new ArrayList<>();


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
        return new GoodsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        if (goodsList.size() != 0) {
            Goods goods = goodsList.get(position);
            holder.initItem(position, goods);
        }
        holder.itemView.setOnTouchListener((v, event) -> {
            hideItemMenu();
            return false;
        });
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
        MultiPhotoRecyclerView rvGoodsPhoto;
        ImageView ivMore;
        MenuView menuView;


        public GoodsViewHolder(View itemView) {
            super(itemView);
            ivCreatorAvatar = itemView.findViewById(R.id.iv_item_goods_avatar);
            tvCreatorName = itemView.findViewById(R.id.tv_item_goods_createor);
            tvGoodsTime = itemView.findViewById(R.id.tv_item_goods_time);
            tvGoodsLocation = itemView.findViewById(R.id.tv_item_goods_location);
            tvGoodsName = itemView.findViewById(R.id.tv_item_goods_name);
            tvGoodsDetail = itemView.findViewById(R.id.tv_item_goods_details);
            rvGoodsPhoto = itemView.findViewById(R.id.rv_item_goods_photo);
            ivMore = itemView.findViewById(R.id.iv_item_more);
            menuView = itemView.findViewById(R.id.mv_item_menu);
        }

        void initItem(int position, Goods goods) {
            GlideApp.with(context).load(goods.getCreatorAvatar() == null ? R.drawable.icon_default_avatar : goods.getCreatorAvatar())
                    .circleCrop().placeholder(R.drawable.icon_default_avatar).into(ivCreatorAvatar);
            tvCreatorName.setText(goods.getCreatorName());
            tvGoodsTime.setText(goods.getUpdatedAt());
            tvGoodsLocation.setText(goods.getLocation());
            tvGoodsName.setText("物品名称：" + goods.getName());
            tvGoodsDetail.setText("物品详情：" + goods.getDetail());
            if (goods.getLocation() == null || TextUtils.isEmpty(goods.getLocation())) {
                tvGoodsLocation.setVisibility(View.GONE);
            } else {
                tvGoodsLocation.setVisibility(View.VISIBLE);
            }
            MultiPhotoAdapter multiPhotoAdapter = new MultiPhotoAdapter(context);
            multiPhotoAdapter.setEditable(false);
            multiPhotoAdapter.setLoadMode(LOAD_INTERNET);
            GridLayoutManager manager = new GridLayoutManager(context, 3);
            rvGoodsPhoto.setLayoutManager(manager);
            rvGoodsPhoto.addItemDecoration(new GridSpacingItemDecoration(3, 5, true));
            rvGoodsPhoto.setAdapter(multiPhotoAdapter);
            multiPhotoAdapter.addPhotoPath(goods.getPhotoList());
            ivCreatorAvatar.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(goods.getCreatorObjectId()))
                    HistoryActivity.Companion.getActivity((Activity) context, goods.getCreatorObjectId());
            });
            initMoreMenu(position, goods);
        }

        private void initMoreMenu(int position, Goods goods) {
            ivMore.setOnClickListener(v -> {
                hideItemMenu();
                menuView.setVisibility(View.VISIBLE);
                menuViewList.add(menuView);
            });
            menuView.setGoods(goods);
            menuView.setDeleteCallback(new MenuView.DeleteCallback() {
                @Override
                public void success() {
                    GoodsAdapter.this.goodsList.remove(position);
                    GoodsAdapter.this.notifyItemRemoved(position);
                    GoodsAdapter.this.notifyItemRangeChanged(position, GoodsAdapter.this.goodsList.size());
                    hideItemMenu();
                }

                @Override
                public void fail(Throwable throwable) {
                    ToastUtil.showToast("删除失败：" + throwable.toString());
                    hideItemMenu();
                }
            });
        }
    }

    public void hideItemMenu() {
        for (MenuView menuView : menuViewList) {
            menuView.setVisibility(View.GONE);
        }
        menuViewList.clear();
    }

}
