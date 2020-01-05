package com.example.user.chatroom.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.user.chatroom.R;
import com.example.user.chatroom.base.BaseRecyclerViewAdapter;
import com.example.user.chatroom.bean.Image;
import com.example.user.chatroom.util.ImageUtil;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserImageRvAdapter extends BaseRecyclerViewAdapter<Image> {

    private Context mContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head_image,parent,false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ImageHolder)holder).bindView(mDataList.get(position));
    }

    class ImageHolder extends BaseRvHolder{

        @BindView(R.id.civ_user_image_item)
        CircleImageView mCivImage;

        public ImageHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(Image imageBean) {
            Glide.with(mContext).load(ImageUtil.getImageResId(imageBean.getImageId())).into(mCivImage);
        }
    }
}
