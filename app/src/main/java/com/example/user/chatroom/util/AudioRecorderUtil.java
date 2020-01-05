package com.example.user.chatroom.util;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioRecorderUtil {

    private String filePath;
    private String FolderPath;
    private int BASE = 1;
    private int SPACE = 100;
    private MediaRecorder mMediaRecorder;
    private final String TAG = "fan";
    private static final int MAX_LENGTH = 1000 * 60 * 10;
    private boolean isRecording = false;


    private OnAudioStatusUpdateListener audioStatusUpdateListener;


    public AudioRecorderUtil(){
        this(Environment.getExternalStorageDirectory()+"/record/");
    }

    private AudioRecorderUtil(String filePath) {
        File path = new File(filePath);
        if(!path.exists())
            path.mkdirs();
        this.FolderPath = filePath;
    }

    private long startTime;
    private long endTime;




    public void startRecord() {
        isRecording = true;
        if (mMediaRecorder == null){
            mMediaRecorder = new MediaRecorder();
        }
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioSamplingRate(44100);
            mMediaRecorder.setAudioChannels(1);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setAudioEncodingBitRate(96000);


            filePath = FolderPath + System.currentTimeMillis() + ".m4a" ;
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            updateMicStatus();
            Log.e("fan", "startTime" + startTime);
        } catch (IllegalStateException | IOException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    public long stopRecord() {
        isRecording = false;
        if (mMediaRecorder == null){
            return 0L;
        }
        endTime = System.currentTimeMillis();
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            audioStatusUpdateListener.onStop(filePath);
            filePath = "";
        }catch (RuntimeException e){
            if (mMediaRecorder != null){
                mMediaRecorder.reset();
                mMediaRecorder.release();
            }
            mMediaRecorder = null;
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
        return endTime - startTime;
    }
    public void cancelRecord(){
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }catch (RuntimeException e){
            if (mMediaRecorder != null){
                mMediaRecorder.reset();
                mMediaRecorder.release();
            }
            mMediaRecorder = null;
        }
        if (isRecording){
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }


    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double)mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if(null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db,System.currentTimeMillis()-startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {

         void onUpdate(double db,long time);

         void onStop(String filePath);
    }

}
