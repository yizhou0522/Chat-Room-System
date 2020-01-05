package com.example.user.chatroom.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.example.user.chatroom.activity.App;
public class SDUtil {

    private static final String TAG = "SDUtil";
    public static String saveBitmap(Bitmap bm, String name,String type) {
        try {
            File file = getMyAppFile(name,type);
            assert file != null;
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 70, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();    }
        return null;
    }


    public static File getMyAppFile(String fileName, String fileType){
        try {
            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/P2P");
            File file;
            if (dirFile.exists()){
                if (!dirFile.isDirectory()){
                    dirFile.delete();
                    dirFile.mkdirs();
                }
            }else {
                dirFile.mkdirs();
            }
            while (true){
                file = new File(dirFile,fileName+fileType);
                if (file.exists()){
                    fileName = fileName+"&";
                }else {
                    file.createNewFile();
                    break;
                }
            }
            return file;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    public static String getFilePath(String name){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/P2P/"+name;
    }




    public static String saveAudio(byte[] bytes, String name) {
        try {
            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/P2P");
            File file;
            if (dirFile.exists()){
                if (!dirFile.isDirectory()){
                    if (!dirFile.isDirectory()){
                        dirFile.delete();
                        dirFile.mkdirs();
                    }
                }
            }else {
                dirFile.mkdirs();
            }
            while (true){
                file = new File(dirFile,name+".m4a");
                if (file.exists()){
                    name = name+"&";
                }else {
                    file.createNewFile();
                    break;
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String saveFile(byte[] bytes, String name,String fileType) {
        try {
            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/P2P");
            File file;
            if (dirFile.exists()){
                if (!dirFile.isDirectory()){
                    dirFile.delete();
                    dirFile.mkdirs();
                }
            }else {
                dirFile.mkdirs();
            }
            while (true){
                file = new File(dirFile,name+fileType);
                if (file.exists()){
                    name = name+"&";
                }else {
                    file.createNewFile();
                    break;
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getFileName(String filePath){
        File file = new File(filePath);
        return file.getName();
    }


    public static int getFileByteSize(String filePath){
        return (int) new File(filePath).length();
    }


    public static String  bytesTransform(int bytesSize){
        double fileSize = bytesSize;
        DecimalFormat df = new DecimalFormat(".0");
        if (fileSize > 1024*1024){
            return df.format(fileSize/(1024*1024))+" MB";
        }else if (fileSize > 1024){
            return df.format(fileSize/1024)+" KB";
        }else {
            return fileSize+" B";
        }
    }




    public static void deleteFile(String path){
        File file = new File(path);
        if (file.exists() && file.isFile()){
            file.delete();
        }
    }


    public static String getFilePathByUri(final Context context, final Uri uri) {
        if (Build.VERSION.SDK_INT >= 19){
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }else {
            return getDataColumn(context,uri,null,null);
        }
    }

    /**
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param filePath
     * @return
     */
    public static String getMimeTypeFromFilePath(String filePath) {
        String type = null;
        String fName = new File(filePath).getName();
        String fileExtension = getFileExtension(fName);
        if (fileExtension != null){
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        }
        return type;
    }

    /**
     * @param fileName
     * @return String fileExtension or null
     */
    public static String getFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex+1, fileName.length()).toLowerCase(Locale.getDefault());
        }
        return null;
    }

    /**
     * @param filePath
     * @return
     */
    public static Uri getFileUri(String filePath){
        File file = new File(filePath);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24){
            uri = FileProvider.getUriForFile(App.getContxet(),"com.example.user.p2p.fileprovider",file);
            App.getContxet().grantUriPermission(App.getContxet().getPackageName(),uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private static String renameFile(String filePath){
        String fileName = getFileName(filePath);
        String type;
        String name;
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            type = fileName.substring(dotIndex, fileName.length());
            name = fileName.substring(0,dotIndex);
        }else {
            name = fileName;
            type = "";
        }
        File oldFile = new File(filePath);
        File newFile = getMyAppFile(name,type);
        oldFile.renameTo(newFile);
        assert newFile != null;
        return newFile.getAbsolutePath();
    }

    /**
     * @return
     */
    public static String checkFileName(@NonNull List<String> fileNameList, String filePath){
        String fileName = getFileName(filePath);
        int dotIndex = fileName.lastIndexOf(".");
        String type;
        String name;
        if (dotIndex > 0) {
            type = fileName.substring(dotIndex, fileName.length());
            name = fileName.substring(0,dotIndex);
        }else {
            name = fileName;
            type = "";
        }
        while (true){
            if (fileNameList.contains(fileName)){
                name = name + "*";
                fileName = name + type;
            }else {
                break;
            }
        }
        return fileName;
    }


}

