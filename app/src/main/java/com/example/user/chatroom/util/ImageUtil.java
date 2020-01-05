package com.example.user.chatroom.util;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.user.chatroom.activity.App;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;

public class ImageUtil {

    private static File mFile;

    /**
     * @param imageNumber
     * @return
     */
    public static int getImageResId(int imageNumber){
        return App.getContxet().getResources().getIdentifier(
                "iv_"+imageNumber,
                "drawable",
                App.getContxet().getPackageName());
    }

    /**
     * @param path
     * @return
     */
    public static float getBitmapSize(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Log.d("util", "getBitmapSize: width="+options.outWidth+",height="+options.outHeight);
        return options.outHeight*1f/options.outWidth;
    }


    /**
     * @param filePath
     * @param callback
     */
    public static void compressImage(String filePath, FileCallback callback) {
        mFile = new File(filePath);
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(new File(filePath))
                .asFile()
                .withOptions(options)
                .compress(callback);
    }

    /**
     */
    public static void clearCompressImageDirectory(){
        Tiny.getInstance().clearCompressDirectory();
    }
}
