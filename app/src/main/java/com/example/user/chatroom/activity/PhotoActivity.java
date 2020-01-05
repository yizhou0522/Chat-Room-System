package com.example.user.chatroom.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.user.chatroom.R;
import com.example.user.chatroom.base.BaseActivity;
import com.example.user.chatroom.base.BasePresenter;
import com.example.user.chatroom.fragment.PhotoFragment;
import com.example.user.chatroom.util.GsonUtil;
import com.example.user.chatroom.util.PhotosVP;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PhotoActivity extends BaseActivity {

    @BindView(R.id.photosVP_act_photo)
    PhotosVP mPhotosVp;

    private static List<String> mImagePathList;
    private static int mCurrentPosition;
    private List<PhotoFragment> mPhotoFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String s = GsonUtil.gsonToJson(mImagePathList);
        outState.putString("ImagePathList",s);
        outState.putInt("CurrentPosition",mCurrentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String s = savedInstanceState.getString("ImagePathList");
        if (s != null){
            mImagePathList = GsonUtil.gsonToList(s,String.class);
        }
        mCurrentPosition = savedInstanceState.getInt("CurrentPosition");
    }

    @Override
    public BasePresenter getInstance() {
        return null;
    }

    public static void actionStart(Context context,List<String> imagePathList,int currentPosition){
        mImagePathList = imagePathList;
        mCurrentPosition = currentPosition;
        context.startActivity(new Intent(context,PhotoActivity.class));
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_photo;
    }

    @Override
    protected void initData() {
        mPhotoFragmentList = new ArrayList<>();
        for (String s : mImagePathList) {
            PhotoFragment photoFragment = new PhotoFragment();
            photoFragment.setData(s);
            mPhotoFragmentList.add(photoFragment);
        }
    }

    @Override
    protected void initView() {
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public PhotoFragment getItem(int position) {
                return mPhotoFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mPhotoFragmentList.size();
            }
        };
        mPhotosVp.setAdapter(fragmentPagerAdapter);
        mPhotosVp.setCurrentItem(mCurrentPosition);
    }

    @Override
    protected void initListener() {

    }
}
