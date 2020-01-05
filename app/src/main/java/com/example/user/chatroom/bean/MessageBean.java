package com.example.user.chatroom.bean;

import android.annotation.SuppressLint;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MessageBean extends DataSupport implements Cloneable {
    private String userIp;
    private String belongIp;
    private String text;
    private String time;
    private String imagePath;
    private String audioPath;
    private String filePath;
    private String fileName;
    private int fileSize;
    private int fileState;
    private int transmittedSize;
    private int msgType;
    private boolean isMine;
    private int sendStatus;



    public MessageBean(String belongIp){
        this.belongIp = belongIp;
    }

    @Override
    public MessageBean clone() {
        MessageBean messageBean = null;
        try {
            messageBean = (MessageBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return messageBean;
    }

    public void updateFileState(MessageBean messageBean){
        this.transmittedSize = messageBean.getTransmittedSize();
        this.fileState = messageBean.getFileState();
        this.filePath = messageBean.getFilePath();
        this.fileName = messageBean.getFileName();
        this.fileSize = messageBean.getFileSize();
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "userIp='" + userIp + '\'' +
                ", belongIp='" + belongIp + '\'' +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileState=" + fileState +
                ", transmittedSize=" + transmittedSize +
                ", msgType=" + msgType +
                ", isMine=" + isMine +
                ", sendStatus=" + sendStatus +
                '}';
    }

    public String getBelongIp() {
        return belongIp == null ? "" : belongIp;
    }

    public void setBelongIp(String belongIp) {
        this.belongIp = belongIp == null ? "" : belongIp;
    }

    public String getFilePath() {
        return filePath == null ? "" : filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? "" : filePath;
    }

    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? "" : fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileState() {
        return fileState;
    }

    public void setFileState(int fileState) {
        this.fileState = fileState;
    }

    public int getTransmittedSize() {
        return transmittedSize;
    }

    public void setTransmittedSize(int transmittedSize) {
        this.transmittedSize = transmittedSize;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getUserIp() {
        return userIp == null ? "" : userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp == null ? "" : userIp;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getText() {
        if (text == null || text.equals("")){
            if (audioPath != null){
                return "Voice";
            }else if (imagePath != null){
                return "Image";
            }else if (filePath != null){
                return "File";
            }
        }
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
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

    public String getImagePath() {
        return imagePath == null ? "" : imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath == null ? "" : imagePath;
    }

    public String getAudioPath() {
        return audioPath == null ? "" : audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath == null ? "" : audioPath;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
