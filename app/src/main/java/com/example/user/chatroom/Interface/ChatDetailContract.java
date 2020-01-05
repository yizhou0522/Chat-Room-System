package com.example.user.chatroom.Interface;

import com.example.user.chatroom.bean.MessageBean;

public interface ChatDetailContract {
    interface View{
        void linkSocket();
        void sendMsgSuccess(int position);
        void sendMsgError(int position,String error);
        void fileSending(int position,MessageBean messageBean);
    }

    interface Model{
        void sendMessage(MessageBean msg,int position);
        void setLinkSocketState(boolean isLink);
        void exit();
    }

    interface Presenter{
        void linkSocket();
        void setLinkSocketState(boolean state);
        void sendMsg(MessageBean msg,int position);
        void sendMsgSuccess(int position);
        void sendMsgError(int position,String error);
        void fileSending(int position, MessageBean fileBean);
        void exit();
    }

}
