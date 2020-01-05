package com.example.user.chatroom.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.user.chatroom.bean.MessageBean;
import com.example.user.chatroom.bean.UserBean;
import com.example.user.chatroom.fragment.ScanDeviceFragment;
import com.example.user.chatroom.fragment.SelectImageFragment;
import com.example.user.chatroom.R;
import com.example.user.chatroom.base.BaseActivity;
import com.example.user.chatroom.base.BasePresenter;
import com.example.user.chatroom.bean.Image;
import com.example.user.chatroom.util.ImageUtil;
import com.example.user.chatroom.util.NetUtil;
import com.example.user.chatroom.util.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "Login";

    @BindView(R.id.civ_user_image_act_login)
    CircleImageView mCivUserImage;
    @BindView(R.id.et_nickname_act_login)
    EditText mEtNickname;
    @BindView(R.id.btn_login_act_login)
    Button mBtnLogin;

    private List<Image> mImageList;
    private int mSelectedImageId;
    @Override
    public BasePresenter getInstance() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        getPermission(this);
        LitePal.getDatabase();
        DataSupport.deleteAll(MessageBean.class);
    }

    /**
     *
     * @param activity
     * @return
     */

    public void getPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            finish();
                            showToast("Cannot use this app due to permission！");
                        }
                    }
                }else {
                    showToast("Cannot use this app due to permission！");
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        mSelectedImageId = 0;
        mImageList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            Image imageBean = new Image();
            imageBean.setImageId(i);
            mImageList.add(imageBean);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        mCivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImageFragment selectImageFragment = new SelectImageFragment();
                selectImageFragment.show(getSupportFragmentManager(),"DialogFragment");
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEtNickname.getText())){
                    UserBean userBean = new UserBean();
                    userBean.setNickName(getString(mEtNickname));
                    userBean.setUserImageId(mSelectedImageId);
                    UserUtil.saveUser(userBean);
                    App.setUserBean(userBean);
                    if (NetUtil.isWifi(LoginActivity.this)){
                        ScanDeviceFragment scanDeviceFragment = new ScanDeviceFragment();
                        scanDeviceFragment.setCancelable(false);
                        scanDeviceFragment.show(getSupportFragmentManager(),"progressFragment");
                    }else {
                        showToast("Please connect to WIFI！");
                    }
                }else {
                    showToast("Username cannot be empty！");
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ScanDeviceFinished(List<String> ipList){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putStringArrayListExtra("ipList", (ArrayList<String>) ipList);
        startActivity(intent);
        finish();
    }

    public void setImageId(int imageId){
        mSelectedImageId = imageId;
        Glide.with(this).load(ImageUtil.getImageResId(imageId)).into(mCivUserImage);
    }
}
