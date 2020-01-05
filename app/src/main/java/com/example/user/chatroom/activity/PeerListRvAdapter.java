package com.example.user.chatroom.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.chatroom.R;
import com.example.user.chatroom.base.BaseRecyclerViewAdapter;
import com.example.user.chatroom.bean.PeerBean;
import com.example.user.chatroom.util.ImageUtil;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PeerListRvAdapter extends BaseRecyclerViewAdapter<PeerBean> {

    /**
     *
     * @param ip
     */
    public void removeItem(String ip){
        int index = getIndexByIp(ip);
        if (index != -1){
            mDataList.remove(index);
            notifyItemRemoved(index);
        }
    }

    /**
     *
     * @param peerBean
     */
    public void addItem(PeerBean peerBean){
        mDataList.add(peerBean);
        notifyItemRangeChanged(mDataList.size()-1,1);
    }

    /**
     *
     * @param text
     */
    public PeerBean updateItemText(String text, String peerIp){
        int index = getIndexByIp(peerIp);
        if (index != -1){
            PeerBean peer = getDataList().get(index);
            peer.setRecentMessage(text);
            notifyItemChanged(index);
            return peer;
        }
        return null;
    }

    /**
     *
     * @param peerIp
     * @return
     */
    public PeerBean getItem(String peerIp){
        for (PeerBean peerBean : mDataList) {
            if (peerBean.getUserIp().equals(peerIp)){
                return peerBean;
            }
        }
        return null;
    }

    /**
     *
     * @param peerBean
     */
    public void updateItem(PeerBean peerBean){
        int index = getIndexByIp(peerBean.getUserIp());
        if (index != -1){
            mDataList.set(index,peerBean);
            notifyItemChanged(index);
        }
    }


    /**
     *
     * @param ip
     * @return
     */
    public boolean isContained(String ip){
        return getIndexByIp(ip) != -1;
    }

    /**
     *
     * @param ip
     * @return the position of element, -1 if fails
     */
    private int getIndexByIp(String ip){
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getUserIp().equals(ip)){
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peer_list,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder)holder).bindView(mDataList.get(position));
    }

    class ItemHolder extends BaseRvHolder{

        @BindView(R.id.tv_nickname_item_peer_list)
        TextView mTvNickname;
        @BindView(R.id.civ_user_image_item_peer_list)
        CircleImageView mCivUserImage;
        @BindView(R.id.tv_recent_message_item_peer_list)
        TextView mTvRecentMessage;
        @BindView(R.id.tv_time_item_peer_list)
        TextView mTvTime;

        public ItemHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(PeerBean peerBean) {
            mTvNickname.setText(peerBean.getNickName());
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(peerBean.getUserImageId())).into(mCivUserImage);
            mTvRecentMessage.setText(peerBean.getRecentMessage());
            mTvTime.setText(peerBean.getTime());
        }
    }
}
