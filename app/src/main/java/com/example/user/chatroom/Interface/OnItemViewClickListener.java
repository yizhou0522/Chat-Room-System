package com.example.user.chatroom.Interface;

import android.view.View;

import com.example.user.chatroom.util.PlayerSoundView;

public interface OnItemViewClickListener {
    void onImageClick(int position);

    void onTextLongClick(int position, View view);

    void onFileClick(int position);

    void onAlterClick(int position);

    void onAudioClick(PlayerSoundView mPsvPlaySound, String audioUrl);
}
