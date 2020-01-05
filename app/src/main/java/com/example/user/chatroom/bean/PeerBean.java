package com.example.user.chatroom.bean;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PeerBean {
    private int userImageId;
    private String nickName;
    private String recentMessage;
    private String time;
    private String userIp;

    @Override
    public String toString() {
        return "PeerBean{" +
                "userImageId=" + userImageId +
                ", nickName='" + nickName + '\'' +
                ", recentMessage='" + recentMessage + '\'' +
                ", time='" + time + '\'' +
                ", userIp='" + userIp + '\'' +
                '}';
    }

    public int getUserImageId() {
        return userImageId;
    }

    public void setUserImageId(int userImageId) {
        this.userImageId = userImageId;
    }

    public String getNickName() {
        return nickName == null ? "" : nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? "" : nickName;
    }

    public String getRecentMessage() {
        return recentMessage == null ? "" : recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage == null ? "" : recentMessage;
    }

    public String getTime() {
        if (time == null){
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            time = sdf.format(date);
        }
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? "" : time;
    }

    public String getUserIp() {
        return userIp == null ? "" : userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp == null ? "" : userIp;
    }
}
