package com.example.user.chatroom.activity;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.chatroom.R;
import com.example.user.chatroom.base.BaseActivity;
import com.example.user.chatroom.base.BasePresenter;
import com.example.user.chatroom.fragment.PeerListFragment;
import com.example.user.chatroom.fragment.ScanDeviceFragment;

import java.net.InetAddress;
import java.net.MulticastSocket;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.vp_act_main)
    ViewPager mVpContent;

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private boolean checking = true;
    private static final String TAG = "LYT";
    private static final int BROADCAST_PORT = 3000;
    private static final String BROADCAST_IP = "239.0.0.3";
    private MulticastSocket mSocket;
    private InetAddress mAddress;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    break;
            }
            return true;
        }
    });
    private PeerListFragment mPeerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public BasePresenter getInstance() {
        return null;
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void initView() {
        initToolbar();
        mPeerListFragment = new PeerListFragment();
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mPeerListFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public void restoreState(Parcelable state, ClassLoader loader) {
                super.restoreState(state, loader);
            }

            @Override
            public Parcelable saveState() {
                return super.saveState();
            }
        };
        mVpContent.setAdapter(mFragmentPagerAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolbar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if (mPeerListFragment.isServerSocketConnected()) {
                    ScanDeviceFragment mScanDeviceFragment = new ScanDeviceFragment();
                    mScanDeviceFragment.setCancelable(false);
                    mScanDeviceFragment.show(getSupportFragmentManager(), "scanDevice");
                } else {
                    showToast("ServerSocket has not been connected，please check your WIFI！");
                }
                break;

        }
        return true;
    }

    @Override
    protected void initListener() {
//        mLlBottomLeft.setOnClickListener(this);
//        mLlBottomRight.setOnClickListener(this);
    }


}
