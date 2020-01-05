package com.example.user.chatroom.activity;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.chatroom.R;
import com.example.user.chatroom.activity.App;
import com.example.user.chatroom.base.BaseRecyclerViewAdapter;
import com.example.user.chatroom.bean.MessageBean;
import com.example.user.chatroom.config.Constant;
import com.example.user.chatroom.config.FileState;
import com.example.user.chatroom.config.Protocol;
import com.example.user.chatroom.Interface.OnItemViewClickListener;
import com.example.user.chatroom.util.ImageUtil;
import com.example.user.chatroom.util.SDUtil;
import com.example.user.chatroom.util.ScreenUtil;
import com.example.user.chatroom.util.PlayerSoundView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;



public class MsgRvAdapter extends BaseRecyclerViewAdapter<MessageBean> {

    private static final String TAG = "MsgRvAdapter";

    private static final int TYPE_RIGHT_TEXT = 0;
    private static final int TYPE_RIGHT_IMAGE = 1;
    private static final int TYPE_RIGHT_AUDIO = 2;
    private static final int TYPE_LEFT_TEXT = 3;
    private static final int TYPE_LEFT_IMAGE = 4;
    private static final int TYPE_LEFT_AUDIO = 5;
    private static final int TYPE_LEFT_FILE = 6;
    private static final int TYPE_RIGHT_FILE= 7;
    private OnItemViewClickListener mOnItemViewClickListener;
    private int mTargetPeerImageId;
    private List<String> mFileNameList;

    public MsgRvAdapter(int userImageId){
        mTargetPeerImageId = userImageId;
        mFileNameList = new ArrayList<>();
    }

    @Override
    public void appendData(MessageBean messageBean) {
        super.appendData(messageBean);
        if (messageBean.getMsgType() == Protocol.FILE){
            mFileNameList.add(messageBean.getFileName());
        }
    }

    @Override
    public void appendData(List<MessageBean> dataList) {
        super.appendData(dataList);
        for (MessageBean messageBean : dataList) {
            if (messageBean.getMsgType() == Protocol.FILE){
                mFileNameList.add(messageBean.getFileName());
            }
        }
    }

    public List<String> getFileNameList(){
        return mFileNameList;
    }

    /**
     *
     * @param fileName
     * @return
     */
    public int getPositionByFileName(String fileName){
        for (int i = 0; i < mDataList.size(); i++) {
            if (fileName.equals(mDataList.get(i).getFileName())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        MessageBean messageBean = mDataList.get(position);
        switch (messageBean.getMsgType()){
            case Protocol.TEXT:
                return messageBean.isMine() ? TYPE_RIGHT_TEXT : TYPE_LEFT_TEXT;
            case Protocol.IMAGE:
                return messageBean.isMine() ? TYPE_RIGHT_IMAGE : TYPE_LEFT_IMAGE;
            case Protocol.AUDIO:
                return messageBean.isMine() ? TYPE_RIGHT_AUDIO : TYPE_LEFT_AUDIO;
            case Protocol.FILE:
                return messageBean.isMine() ? TYPE_RIGHT_FILE : TYPE_LEFT_FILE;
            default:
                return TYPE_RIGHT_TEXT;
        }
    }

    @NonNull
    @Override
    public BaseRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_LEFT_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_text, parent, false);
                return new LeftTextHolder(view);
            case TYPE_RIGHT_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_text, parent, false);
                return new RightTextHolder(view);
            case TYPE_LEFT_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_image, parent, false);
                return new LeftImageHolder(view);
            case TYPE_RIGHT_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_image, parent, false);
                return new RightImageHolder(view);
            case TYPE_LEFT_AUDIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_audio, parent, false);
                return new LeftAudioHolder(view);
            case TYPE_RIGHT_AUDIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_audio, parent, false);
                return new RightAudioHolder(view);
            case TYPE_LEFT_FILE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_file, parent, false);
                return new LeftFileHolder(view);
            case TYPE_RIGHT_FILE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_file, parent, false);
                return new RightFileHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_text, parent, false);
                return new LeftTextHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case TYPE_LEFT_TEXT:
                ((LeftTextHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_RIGHT_TEXT:
                ((RightTextHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_LEFT_IMAGE:
                ((LeftImageHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_RIGHT_IMAGE:
                ((RightImageHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_LEFT_AUDIO:
                ((LeftAudioHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_RIGHT_AUDIO:
                ((RightAudioHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_RIGHT_FILE:
                ((RightFileHolder)holder).bindView(mDataList.get(position));
                break;
            case TYPE_LEFT_FILE:
                ((LeftFileHolder)holder).bindView(mDataList.get(position));
                break;
            default:
                ((LeftTextHolder)holder).bindView(mDataList.get(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else {
            int payload = (int) payloads.get(0);
            MessageBean messageBean = mDataList.get(position);
            switch (payload){
                case Constant.UPDATE_FILE_STATE:
                    if (holder instanceof LeftFileHolder){
                        LeftFileHolder leftFileHolder = (LeftFileHolder) holder;
                        updateTransmitFileState(leftFileHolder.mPbReceive,leftFileHolder.mTvReceiveStates,messageBean);
                    }else if (holder instanceof RightFileHolder){
                        RightFileHolder rightFileHolder = (RightFileHolder) holder;
                        updateTransmitFileState(rightFileHolder.mPbSending,rightFileHolder.mTvSendStatus,messageBean);
                    }
                    break;
                case Constant.UPDATE_SEND_MSG_STATE:
                    switch (messageBean.getMsgType()){
                        case Protocol.TEXT:
                            RightTextHolder rightTextHolder = (RightTextHolder) holder;
                            updateSendMsgStatus(rightTextHolder.mPbSending,rightTextHolder.mIvAlter,messageBean.getSendStatus());
                            break;
                        case Protocol.IMAGE:
                            RightImageHolder rightImageHolder = (RightImageHolder) holder;
                            updateSendMsgStatus(rightImageHolder.mPbSending,rightImageHolder.mIvAlter,messageBean.getSendStatus());
                            break;
                        case Protocol.AUDIO:
                            RightAudioHolder rightAudioHolder = (RightAudioHolder) holder;
                            updateSendMsgStatus(rightAudioHolder.mPbSending,rightAudioHolder.mIvAlter,messageBean.getSendStatus());
                            break;
                    }
            }
        }
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener){
        this.mOnItemViewClickListener = onItemViewClickListener;
    }


    class RightAudioHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_right_item_message)
        CircleImageView  mCivRightHeadImage;
        @BindView(R.id.psv_play_sound_right_item_message)
        PlayerSoundView mPsvPlaySound;
        @BindView(R.id.ll_right_audio_item_message)
        LinearLayout mLlRightAudio;
        @BindView(R.id.pb_msg_sending_right_item_message)
        ProgressBar mPbSending;
        @BindView(R.id.iv_alter_right_item_message)
        ImageView mIvAlter;

        RightAudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(final MessageBean messageBean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(App.getUserBean().getUserImageId()))
                    .into(mCivRightHeadImage);
            if (mOnItemViewClickListener != null){
                mLlRightAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onAudioClick(mPsvPlaySound,messageBean.getAudioPath());
                    }
                });
                mIvAlter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onAlterClick(getLayoutPosition());
                    }
                });
            }
            updateSendMsgStatus(mPbSending,mIvAlter,messageBean.getSendStatus());
        }
    }

    class LeftAudioHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_left_item_message)
        CircleImageView mCivLeftHeadImage;
        @BindView(R.id.psv_play_sound_left_item_message)
        PlayerSoundView mPsvPlaySound;
        @BindView(R.id.ll_left_audio_item_message)
        LinearLayout mLlRightAudio;


        LeftAudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(final MessageBean messageBean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(mTargetPeerImageId))
                    .into(mCivLeftHeadImage);
            if (mOnItemViewClickListener != null){
                mLlRightAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onAudioClick(mPsvPlaySound,messageBean.getAudioPath());
                    }
                });
            }
        }
    }

    class RightTextHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_right_item_message)
        CircleImageView  mCivRightHeadImage;
        @BindView(R.id.tv_text_right_item_message)
        TextView mTvRightText;
        @BindView(R.id.pb_msg_sending_right_item_message)
        ProgressBar mPbSending;
        @BindView(R.id.iv_alter_right_item_message)
        ImageView mIvAlter;
        @BindView(R.id.ll_right_text_item_message)
        LinearLayout mLlRightTextLayout;


        RightTextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(MessageBean messageBean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(App.getUserBean().getUserImageId()))
                    .into(mCivRightHeadImage);
            mTvRightText.setText(messageBean.getText());
            if (mOnItemViewClickListener != null){
                mLlRightTextLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemViewClickListener.onTextLongClick(getLayoutPosition(),mLlRightTextLayout);
                        return true;
                    }
                });
                mIvAlter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onAlterClick(getLayoutPosition());
                    }
                });
            }
            updateSendMsgStatus(mPbSending,mIvAlter,messageBean.getSendStatus());
        }
    }

    class LeftTextHolder extends BaseRvHolder{
        @BindView(R.id.civ_head_image_left_item_message)
        CircleImageView mCivLeftHeadImage;
        @BindView(R.id.tv_text_left_item_message)
        TextView mTvLeftText;
        @BindView(R.id.ll_left_text_item_message)
        LinearLayout mLlLeftTextLayout;

        LeftTextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(MessageBean messageBean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(mTargetPeerImageId))
                    .into(mCivLeftHeadImage);
            mTvLeftText.setText(messageBean.getText());
            if (mOnItemViewClickListener != null){
                mLlLeftTextLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemViewClickListener.onTextLongClick(getLayoutPosition(),mLlLeftTextLayout);
                        return true;
                    }
                });
            }
        }
    }

    class RightImageHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_right_item_message)
        CircleImageView  mCivRightHeadImage;
        @BindView(R.id.iv_image_right_item_message)
        ImageView mIvRightImage;
        @BindView(R.id.pb_msg_sending_right_item_message)
        ProgressBar mPbSending;
        @BindView(R.id.iv_alter_right_item_message)
        ImageView mIvAlter;

        RightImageHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(MessageBean messageBean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(App.getUserBean().getUserImageId()))
                    .into(mCivRightHeadImage);
            setIvLayoutParams(mIvRightImage,messageBean.getImagePath());
            Glide.with(itemView.getContext())
                    .load(messageBean.getImagePath())
                    .into(mIvRightImage);
            updateSendMsgStatus(mPbSending,mIvAlter,messageBean.getSendStatus());
            if (mOnItemViewClickListener != null){
                mIvRightImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onImageClick(getLayoutPosition());
                    }
                });
                mIvAlter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onAlterClick(getLayoutPosition());
                    }
                });
            }
        }
    }

    class LeftImageHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_left_item_message)
        CircleImageView mCivLeftHeadImage;
        @BindView(R.id.iv_image_left_item_message)
        ImageView mIvLeftImage;

        LeftImageHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(MessageBean messageBean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(mTargetPeerImageId))
                    .into(mCivLeftHeadImage);
            setIvLayoutParams(mIvLeftImage,messageBean.getImagePath());
            Glide.with(itemView.getContext())
                    .load(messageBean.getImagePath())
                    .into(mIvLeftImage);
            if (mOnItemViewClickListener != null){
                mIvLeftImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onImageClick(getLayoutPosition());
                    }
                });
            }
        }
    }

    class LeftFileHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_left_item_message)
        CircleImageView mCivLeftHeadImage;
        @BindView(R.id.tv_file_name_left_item_message)
        TextView mTvFileName;
        @BindView(R.id.tv_file_size_left_item_message)
        TextView mTvFileSize;
        @BindView(R.id.pb_receiving_progress_left_item_message)
        ProgressBar mPbReceive;
        @BindView(R.id.tv_receiving_states_left_item_message)
        TextView mTvReceiveStates;
        @BindView(R.id.cl_left_file_item_message)
        ConstraintLayout mClFileLayout;

        LeftFileHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindView(MessageBean bean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(mTargetPeerImageId))
                    .into(mCivLeftHeadImage);
            mTvFileName.setText(bean.getFileName());
            mTvFileSize.setText(SDUtil.bytesTransform(bean.getFileSize()));
            mPbReceive.setVisibility(View.VISIBLE);
            if (mOnItemViewClickListener != null){
                mClFileLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onFileClick(getLayoutPosition());
                    }
                });
            }
            switch (bean.getFileState()){
                case FileState.RECEIVE_FILE_ING:
                    float ratio = bean.getTransmittedSize()*1f/bean.getFileSize();
                    mPbReceive.setProgress((int) (ratio*100));
                    mTvReceiveStates.setText(ratio*100+"%");
                    break;
                case FileState.RECEIVE_FILE_FINISH:
                    mPbReceive.setVisibility(View.INVISIBLE);
                    mTvReceiveStates.setText("Downloaded");
                    break;
                case FileState.RECEIVE_FILE_ERROR:
                    mPbReceive.setVisibility(View.INVISIBLE);
                    mTvReceiveStates.setText("Connection Error");
                    break;
            }
        }
    }

    class RightFileHolder extends BaseRvHolder{

        @BindView(R.id.civ_head_image_right_item_message)
        CircleImageView mCivRightHeadImage;
        @BindView(R.id.tv_file_name_right_item_message)
        TextView mTvFileName;
        @BindView(R.id.tv_file_size_right_item_message)
        TextView mTvFileSize;
        @BindView(R.id.pb_sending_progress_right_item_message)
        ProgressBar mPbSending;
        @BindView(R.id.tv_send_status_right_item_message)
        TextView mTvSendStatus;
        @BindView(R.id.cl_right_file_item_message)
        ConstraintLayout mClFileLayout;

        RightFileHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindView(MessageBean bean) {
            Glide.with(itemView.getContext())
                    .load(ImageUtil.getImageResId(App.getUserBean().getUserImageId()))
                    .into(mCivRightHeadImage);
            mTvFileName.setText(bean.getFileName());
            mTvFileSize.setText(SDUtil.bytesTransform(bean.getFileSize()));
            mPbSending.setVisibility(View.VISIBLE);
            if (mOnItemViewClickListener != null){
                mClFileLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemViewClickListener.onFileClick(getLayoutPosition());
                    }
                });
            }
            switch (bean.getFileState()){
                case FileState.SEND_FILE_ING:
                    float ratio = bean.getTransmittedSize()*1f/bean.getFileSize();
                    mPbSending.setProgress((int) (ratio*100));
                    mTvSendStatus.setText(ratio*100+"%");
                    break;
                case FileState.SEND_FILE_FINISH:
                    mPbSending.setVisibility(View.INVISIBLE);
                    mTvSendStatus.setText("The message is Sent");
                    break;
                case FileState.SEND_FILE_ERROR:
                    mPbSending.setVisibility(View.INVISIBLE);
                    mTvSendStatus.setText("Connection Error");
                    break;
            }
        }
    }


    /**
     * 根据图片的高宽比例处理ImageView的高宽
     * @param iv
     * @param path
     */
    private void setIvLayoutParams(ImageView iv,String path){
        float scale = ImageUtil.getBitmapSize(path);
        ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
        int ivWidth;
        if (scale <= 0.65f){
            ivWidth = ScreenUtil.dip2px(App.getContxet(),220);
        }else {
            ivWidth = ScreenUtil.dip2px(App.getContxet(),160);
        }
        layoutParams.width = ivWidth;
        layoutParams.height = (int) (ivWidth * scale);
        iv.setLayoutParams(layoutParams);
    }

    /**
     *
     * @param pbSending
     * @param ivAlter
     * @param status
     */
    private void updateSendMsgStatus(ProgressBar pbSending, ImageView ivAlter, int status){
        switch (status){
            case Constant.SEND_MSG_ING:
                pbSending.setVisibility(View.VISIBLE);
                ivAlter.setVisibility(View.GONE);
                break;
            case Constant.SEND_MSG_FINISH:
                pbSending.setVisibility(View.INVISIBLE);
                ivAlter.setVisibility(View.GONE);
                break;
            case Constant.SEND_MSG_ERROR:
                pbSending.setVisibility(View.GONE);
                ivAlter.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     *
     * @param progressBar
     * @param textView
     * @param messageBean
     */
    @SuppressLint("SetTextI18n")
    private void updateTransmitFileState(ProgressBar progressBar, TextView textView, MessageBean messageBean){
        switch (messageBean.getFileState()){
            case FileState.RECEIVE_FILE_ING:
            case FileState.SEND_FILE_ING:
                float ratio2 = messageBean.getTransmittedSize()*1f/messageBean.getFileSize();
                progressBar.setProgress((int) (ratio2*100));
                textView.setText((int)( ratio2*100)+"%");
                break;
            case FileState.SEND_FILE_FINISH:
            case FileState.RECEIVE_FILE_FINISH:
                progressBar.setVisibility(View.INVISIBLE);
                textView.setText("Completed");
                break;
            case FileState.SEND_FILE_ERROR:
            case FileState.RECEIVE_FILE_ERROR:
                progressBar.setVisibility(View.INVISIBLE);
                textView.setText("Failure");
                break;
        }
    }

}
