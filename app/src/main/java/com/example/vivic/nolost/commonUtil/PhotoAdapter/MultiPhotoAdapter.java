package com.example.vivic.nolost.commonUtil.PhotoAdapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vivic.nolost.GlideApp;
import com.example.vivic.nolost.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 多图展示recyclerview
 */
public class MultiPhotoAdapter extends RecyclerView.Adapter<MultiPhotoAdapter.PhotoViewHolder> {


    private List<String> photoPathList = new ArrayList<>();
    private Context context;

    private boolean editable;
    private int loadMode;
    public static int LOAD_FILE = 1;
    public static int LOAD_INTERNET = 2;

    public MultiPhotoAdapter(List<String> photoPathList, Context context) {
        this.photoPathList = photoPathList;
        this.context = context;
    }

    public MultiPhotoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, viewGroup, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder viewHolder, int position) {
        if (photoPathList != null && photoPathList.size() != 0) {
            String imagePath = photoPathList.get(position);
            viewHolder.initItem(imagePath, position);
        }
    }

    @Override
    public int getItemCount() {
        return photoPathList == null ? 0 : photoPathList.size();
    }

    public void addPhotoPath(List<String> photoPathList) {
        if (photoPathList != null) {
            int beforeAddCount = this.photoPathList.size();
            int addCount = photoPathList.size();
            this.photoPathList.addAll(photoPathList);
            notifyItemRangeChanged(beforeAddCount, addCount);
        }
    }

    public List<String> getPhotoPathList() {
        return photoPathList;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        notifyDataSetChanged();
    }

    public void setLoadMode(int loadMode) {
        this.loadMode = loadMode;
        notifyDataSetChanged();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;
        ImageView ivDelete;


        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_item_photo);
            ivDelete = itemView.findViewById(R.id.iv_item_delete);
        }

        void initItem(String imagePath, int position) {
            if (loadMode == LOAD_FILE) {
                GlideApp.with(context).asDrawable().load(new File(imagePath)).placeholder(R.drawable.icon_default_avatar).thumbnail(0.25f).centerCrop().into(ivPhoto);
            } else if (loadMode == LOAD_INTERNET) {
                GlideApp.with(context).asDrawable().placeholder(R.drawable.icon_default_avatar).thumbnail(0.25f).centerCrop().into(ivPhoto);
            }
            if (!editable) {
                ivDelete.setVisibility(View.GONE);
            }
            ivDelete.setOnClickListener(v -> {
                photoPathList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, photoPathList.size());
            });
        }
    }
}
