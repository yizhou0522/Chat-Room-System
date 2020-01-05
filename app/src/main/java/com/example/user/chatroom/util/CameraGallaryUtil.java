package com.example.user.chatroom.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;

public class CameraGallaryUtil {
    private static final int RESULT_OK = -1;
    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    public static final int PHOTO_REQUEST_GALLERY = 2;
    public static final int PHOTO_REQUEST_CUT = 3;

    public static final Uri fileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
            .getPath() + File.separator + "temp.jpg"));

    public static Bitmap getBitmapFromCG(Activity activity, int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.getData() != null) {
                            startPhotoZoom(activity, data.getData());
                        }
                    } else {
                        startPhotoZoom(activity, fileUri);
                    }
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    if (data.getData() != null) {
                        startPhotoZoom(activity, data.getData());
                    }
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        bitmap = bundle.getParcelable("data");
                    }
                }
                break;
        }
        return bitmap;
    }

    private static void startPhotoZoom(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 300);
        intent.putExtra("aspectY", 400);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 400);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}
