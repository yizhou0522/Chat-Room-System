package com.example.user.chatroom.implementation;

import android.app.Activity;

import com.example.user.chatroom.base.BasePresenter;
import com.example.user.chatroom.bean.MessageBean;
import com.example.user.chatroom.config.Protocol;
import com.example.user.chatroom.Interface.ChatDetailContract;
import com.example.user.chatroom.implementation.ChatDetailModel;
import com.example.user.chatroom.util.ImageUtil;
import com.zxy.tiny.callback.FileCallback;

public class ChatDetailPresenter extends BasePresenter<ChatDetailContract.View> implements ChatDetailContract.Presenter {

    private ChatDetailContract.Model mModel;
    private Activity mActivity;

    public ChatDetailPresenter(Activity activity,String targetIp){
        mModel = new ChatDetailModel(this,targetIp);
        mActivity = activity;
    }

    @Override
    public void linkSocket() {
        if (isAttachView()){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMvpView().linkSocket();
                }
            });
        }
    }

    @Override
    public void setLinkSocketState(boolean state) {
        mModel.setLinkSocketState(state);
    }

    @Override
    public void sendMsg(final MessageBean msg, final int position) {
        if (msg.getMsgType() == Protocol.IMAGE){
            ImageUtil.compressImage(msg.getImagePath(), new FileCallback() {
                @Override
                public void callback(boolean isSuccess, String outfile, Throwable t) {
                    if (isSuccess){
                        msg.setImagePath(outfile);
                    }
                    mModel.sendMessage(msg,position);
                    msg.save();
                }
            });
        }else {
            mModel.sendMessage(msg,position);
            msg.save();
        }
    }

    @Override
    public void sendMsgSuccess(final int position) {
        if (isAttachView()){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMvpView().sendMsgSuccess(position);
                }
            });
        }
    }

    @Override
    public void sendMsgError(final int position, final String error) {
        if (isAttachView()){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMvpView().sendMsgError(position,error);
                }
            });
        }
    }

    @Override
    public void fileSending(final int position, final MessageBean messageBean) {
        if (isAttachView()){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMvpView().fileSending(position, messageBean);
                }
            });
        }
    }

    @Override
    public void exit() {
        mModel.exit();
    }
}
