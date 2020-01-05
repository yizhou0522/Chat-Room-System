package com.example.user.chatroom.Interface;

import com.example.user.chatroom.bean.MessageBean;

public interface OnSocketSendCallback {

    void sendMsgSuccess(int position);

    void sendMsgError(int position);

    void fileSending(int position,MessageBean messageBean);
}
