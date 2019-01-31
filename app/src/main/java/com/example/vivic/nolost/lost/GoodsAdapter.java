package com.example.vivic.nolost.lost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.vivic.nolost.GlideApp;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.Goods;
import com.example.vivic.nolost.bmob.DataRepository;
import com.example.vivic.nolost.bmob.IBmobCallback;
import com.example.vivic.nolost.commonUtil.dimensUtil.DimensUtils;
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.GridSpacingItemDecoration;
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter;
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoRecyclerView;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;
import com.example.vivic.nolost.lost.activity.HistoryActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter.LOAD_INTERNET;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {

    private static final String TAG = GoodsAdapter.class.getSimpleName();
    private List<Goods> goodsList = new ArrayList<>();
    private Context context;
    private List<PopupWindow> popupWindowList = new ArrayList<>();

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
            holder.initItem(position, goods);
        }
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (PopupWindow popupWindow : popupWindowList) {
                    popupWindow.dismiss();
                }
                return false;
            }
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
        TextView tvShare;
        ImageView ivMore;
        TextView tvDelete;


        public GoodsViewHolder(View itemView) {
            super(itemView);
            ivCreatorAvatar = itemView.findViewById(R.id.iv_item_goods_avatar);
            tvCreatorName = itemView.findViewById(R.id.tv_item_goods_createor);
            tvGoodsTime = itemView.findViewById(R.id.tv_item_goods_time);
            tvGoodsLocation = itemView.findViewById(R.id.tv_item_goods_location);
            tvGoodsName = itemView.findViewById(R.id.tv_item_goods_name);
            tvGoodsDetail = itemView.findViewById(R.id.tv_item_goods_details);
            rvGoodsPhoto = itemView.findViewById(R.id.rv_item_goods_photo);
            rvGoodsPhoto.addItemDecoration(new GridSpacingItemDecoration(3, 5, true));
            ivMore = itemView.findViewById(R.id.iv_item_more);
        }

        public void initItem(int position, Goods goods) {
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
            rvGoodsPhoto.setAdapter(multiPhotoAdapter);
            multiPhotoAdapter.addPhotoPath(goods.getPhotoList());
            ivCreatorAvatar.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(goods.getCreatorObjectId()))
                    HistoryActivity.Companion.getActivity((Activity) context, goods.getCreatorObjectId());
            });
            initMoreMenu(position, goods);
        }

        private void initMoreMenu(int position, Goods goods) {
            View view = LayoutInflater.from(context).inflate(R.layout.menu_item_more, null);

            ivMore.setOnClickListener(v -> {
                for (PopupWindow i : popupWindowList) {
                    i.dismiss();
                }
                PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindowList.add(popupWindow);
                popupWindow.showAsDropDown(ivMore, -DimensUtils.dip2pixel(130f), -DimensUtils.dip2pixel(35f));
            });
            tvShare = view.findViewById(R.id.tv_item_share);
            tvShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(goods.getPhotoList().get(0)));
                shareIntent.putExtra(Intent.EXTRA_TEXT, goods.getDetail());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, goods.getName());
                shareIntent.setType("image/*");
                context.startActivity(Intent.createChooser(shareIntent, "test"));
            });
            tvDelete = view.findViewById(R.id.tv_item_delete);
            tvDelete.setOnClickListener(v -> {
                DataRepository.INSTANCE.deleteByObjectId(goods, new IBmobCallback<String>() {
                    @Override
                    public void success(@Nullable String result) {
                        GoodsAdapter.this.goodsList.remove(position);
                        GoodsAdapter.this.notifyItemRemoved(position);
                        GoodsAdapter.this.notifyItemRangeChanged(position, GoodsAdapter.this.goodsList.size());
                    }

                    @Override
                    public void error(@Nullable Throwable throwable) {
                        ToastUtil.showToast("删除失败：" + throwable.toString());
                    }
                });
                for (PopupWindow i : popupWindowList) {
                    i.dismiss();
                }
            });
        }
    }
}
