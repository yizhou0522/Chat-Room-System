package com.example.user.chatroom.implementation;

import com.example.user.chatroom.bean.MessageBean;
import com.example.user.chatroom.config.FileState;
import com.example.user.chatroom.config.Protocol;
import com.example.user.chatroom.Interface.ChatDetailContract;
import com.example.user.chatroom.Interface.OnSocketSendCallback;
import com.example.user.chatroom.socket.SocketManager;
import com.example.user.chatroom.socket.SocketThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatDetailModel implements ChatDetailContract.Model {

    private static final String TAG = "ChatDetailModel";
    private ChatDetailContract.Presenter mPresenter;
    private ThreadPoolExecutor mExecutor;
    private AtomicBoolean mIsLinkedSocket;
    private AtomicBoolean mIsLinkingSocket;
    private OnSocketSendCallback mOnSocketSendCallback;
    private String mTargetIp;

    private static final int CORE_POOL_SIZE = 1;

    private static final int MAX_IMUM_POOL_SIZE = 255;

    public ChatDetailModel(ChatDetailContract.Presenter presenter,String targetIp) {
        mPresenter = presenter;
        mTargetIp = targetIp;
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE,
                1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                MAX_IMUM_POOL_SIZE));
        mIsLinkedSocket = new AtomicBoolean(true);
        mIsLinkingSocket = new AtomicBoolean(false);
        mOnSocketSendCallback = new OnSocketSendCallback() {
            @Override
            public void sendMsgSuccess(int position) {
                mPresenter.sendMsgSuccess(position);
            }

            @Override
            public void sendMsgError(int position) {
                mPresenter.sendMsgError(position,"Socket does not connect！");
            }

            @Override
            public void fileSending(int position,MessageBean messageBean) {
                mPresenter.fileSending(position,messageBean);
            }
        };
        SocketThread socketThread = SocketManager.getInstance().getSocketThreadByIp(targetIp);
        socketThread.setOnSocketSendCallback(mOnSocketSendCallback);
    }

    @Override
    public void sendMessage(final MessageBean msg, final int position) {
        if (mIsLinkedSocket.get()){
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SocketThread socketThread = SocketManager.getInstance().getSocketThreadByIp(mTargetIp);
                    if (socketThread != null){
                        socketThread.sendMsg(msg,position);
                    }else{
                        mIsLinkedSocket.set(false);
                        if (msg.getMsgType() == Protocol.FILE){
                            msg.setFileState(FileState.SEND_FILE_ERROR);
                            mPresenter.fileSending(position,msg);
                        }else {
                            mPresenter.sendMsgError(position,"Socket does not connect！");
                        }
                    }
                }
            });
        }else if (!mIsLinkingSocket.get()){
            mIsLinkingSocket.set(true);
            mPresenter.linkSocket();
            if (msg.getMsgType() == Protocol.FILE){
                msg.setFileState(FileState.SEND_FILE_ERROR);
                mPresenter.fileSending(position,msg);
            }else {
                mPresenter.sendMsgError(position,"Socket is disconnected！");
            }
        }else {
            if (msg.getMsgType() == Protocol.FILE){
                msg.setFileState(FileState.SEND_FILE_ERROR);
                mPresenter.fileSending(position,msg);
            }else {
                mPresenter.sendMsgError(position,"Socket is disconnected！");
            }
        }
    }

    @Override
    public void setLinkSocketState(boolean isLink) {
        if (isLink){
            SocketThread socketThread = SocketManager.getInstance().getSocketThreadByIp(mTargetIp);
            socketThread.setOnSocketSendCallback(mOnSocketSendCallback);
        }
        mIsLinkedSocket.set(isLink);
        mIsLinkingSocket.set(false);
    }

    @Override
    public void exit() {
        SocketManager.getInstance().removeSocketThreadByIp(mTargetIp);
        SocketManager.getInstance().removeSocketByIp(mTargetIp);
    }


}
