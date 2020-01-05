package com.example.user.chatroom.util;

import android.content.SharedPreferences;

import com.example.user.chatroom.activity.App;
import com.example.user.chatroom.bean.UserBean;

public class UserUtil {

    public static void saveMyIp(String ip){
        SharedPreferences.Editor editor = App.getContxet().getSharedPreferences("user",0).edit();
        editor.putString("ip",ip);
        editor.apply();
    }

    public static String getMyIp(){
        SharedPreferences sp = App.getContxet().getSharedPreferences("user",0);
        return sp.getString("ip","");
    }

    /**
     * @param userBean
     */
    public static void saveUser(UserBean userBean){
        SharedPreferences.Editor editor = App.getContxet().getSharedPreferences("user",0).edit();
        editor.putInt("imageId",userBean.getUserImageId());
        editor.putString("nickName",userBean.getNickName());
        editor.apply();
    }


    /**
     */
    public static UserBean getUser(){
        UserBean userBean = new UserBean();
        SharedPreferences sp = App.getContxet().getSharedPreferences("user",0);
        userBean.setUserImageId(sp.getInt("imageId",0));
        userBean.setNickName(sp.getString("nickName",""));
        return userBean;
    }
}
