package com.example.user.chatroom.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.chatroom.R;
import com.example.user.chatroom.activity.ChatDetailActivity;
import com.example.user.chatroom.activity.PeerListRvAdapter;
import com.example.user.chatroom.base.BaseFragment;
import com.example.user.chatroom.config.FileState;
import com.example.user.chatroom.socket.LinkSocketRequestEvent;
import com.example.user.chatroom.bean.MessageBean;
import com.example.user.chatroom.bean.PeerBean;
import com.example.user.chatroom.Interface.PeerListContract;
import com.example.user.chatroom.socket.LinkSocketResponseEvent;
import com.example.user.chatroom.socket.RecentMsgEvent;
import com.example.user.chatroom.Interface.OnClickRecyclerViewListener;
import com.example.user.chatroom.socket.SocketManager;
import com.example.user.chatroom.implementation.PeerListPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class PeerListFragment extends BaseFragment<PeerListPresenter> implements PeerListContract.View  {

    public static final String TAG = "PeerListFragment";
    private static final int INIT_SERVER_SOCKET = 0;

    @BindView(R.id.rv_peer_list_fragment_peer_list)
    RecyclerView mRvPeerList;
    @BindView(R.id.ll_loadingPeersInfo_fragment_peer_list)
    LinearLayout mLlLoadingPeersInfo;
    @BindView(R.id.tv_tip_nonePeer_fragment_peer_list)
    TextView mTvTipNonePeer;

    private PeerListRvAdapter mPeerListRvAdapter;
    private WifiReceiver mWifiReceiver;
    private List<String> mPeerList;
    private boolean isFirstScanDeviceFinished;
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case INIT_SERVER_SOCKET:
                    mPresenter.initSocket();
                    break;
            }
            return true;
        }
    });

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_peer_list;
    }

    @Override
    protected PeerListPresenter getInstance() {
        return new PeerListPresenter(mBaseActivity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.initSocket();
        EventBus.getDefault().register(this);
        mWifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mBaseActivity.registerReceiver(mWifiReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mBaseActivity != null){
            mBaseActivity.unregisterReceiver(mWifiReceiver);
        }else {
            Log.d(TAG, "onDestroyView：PeerListFragment Activity is null !");
        }
        if (mPresenter != null){
            mPresenter.disconnect();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void scanDeviceFinished(List<String> ipList){
        mPeerList.clear();
        mPeerList.addAll(ipList);
        if (isFirstScanDeviceFinished){
            isFirstScanDeviceFinished = false;
        }else {
            if (mPresenter.isServerSocketConnected()){
                mPresenter.linkPeers(new ArrayList<>(mPeerList));
                mPeerList.clear();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void linkSocket(LinkSocketRequestEvent linkSocketRequestEvent){
        mPresenter.linkPeer(linkSocketRequestEvent.getTargetIp());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRecentMsg(RecentMsgEvent recentMsgEvent){
       mPeerListRvAdapter.updateItemText(recentMsgEvent.getText(),recentMsgEvent.getTargetIp());
    }

    public boolean isServerSocketConnected(){
        return mPresenter.isServerSocketConnected();
    }

    @Override
    protected void initData(Bundle bundle) {
        isFirstScanDeviceFinished = true;
        mPeerList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        mPeerListRvAdapter = new PeerListRvAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBaseActivity,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_divider)));
        mRvPeerList.addItemDecoration(dividerItemDecoration);
        mRvPeerList.setLayoutManager(new LinearLayoutManager(mBaseActivity,LinearLayoutManager.VERTICAL,false));
        mRvPeerList.setAdapter(mPeerListRvAdapter);
    }

    @Override
    protected void setListener() {
        mPeerListRvAdapter.setOnRecyclerViewListener(new OnClickRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                PeerBean peerBean = mPeerListRvAdapter.getDataList().get(position);
                if (SocketManager.getInstance().isClosedSocket(peerBean.getUserIp())){
                    showToast("Connecting socket...");
                    mPresenter.linkPeer(peerBean.getUserIp());
                }else {
                    ChatDetailActivity.actionStart(mBaseActivity,peerBean.getUserIp(),peerBean.getNickName(),peerBean.getUserImageId());
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }

        });
    }

    @Override
    public void updatePeerList(List<PeerBean> list) {
        if (list.size() == 0){
            mRvPeerList.setVisibility(View.GONE);
            mLlLoadingPeersInfo.setVisibility(View.GONE);
            mTvTipNonePeer.setVisibility(View.VISIBLE);
        }else {
            mRvPeerList.setVisibility(View.VISIBLE);
            mLlLoadingPeersInfo.setVisibility(View.GONE);
            mTvTipNonePeer.setVisibility(View.GONE);
            mPeerListRvAdapter.updateData(list);
        }
    }

    @Override
    public void messageReceived(MessageBean messageBean) {
        PeerBean peer = mPeerListRvAdapter.updateItemText(messageBean.getText(),messageBean.getUserIp());
        if (peer == null){
            showToast("Rece！");
        }else {
            EventBus.getDefault().post(messageBean);
        }
    }

    @Override
    public void fileReceiving(MessageBean messageBean) {
        if (messageBean.getFileState() == FileState.RECEIVE_FILE_START){
            mPeerListRvAdapter.updateItemText(messageBean.getText(),messageBean.getUserIp());
        }
        EventBus.getDefault().post(messageBean);
    }

    @Override
    public void addPeer(PeerBean peerBean) {
        Log.d(TAG, "addPeer: "+peerBean.getUserIp());
        mRvPeerList.setVisibility(View.VISIBLE);
        mLlLoadingPeersInfo.setVisibility(View.GONE);
        mTvTipNonePeer.setVisibility(View.GONE);
        if (mPeerListRvAdapter.isContained(peerBean.getUserIp())){
            mPeerListRvAdapter.updateItem(peerBean);
        }else {
            mPeerListRvAdapter.addItem(peerBean);
        }
        EventBus.getDefault().post(new LinkSocketResponseEvent(true,peerBean));
    }

    @Override
    public void removePeer(String ip) {
        mPeerListRvAdapter.removeItem(ip);
        if (mPeerListRvAdapter.getDataList().size() == 0){
            mRvPeerList.setVisibility(View.GONE);
            mLlLoadingPeersInfo.setVisibility(View.GONE);
            mTvTipNonePeer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void serverSocketError(String msg) {
        showToast(msg);
        mRvPeerList.setVisibility(View.GONE);
        mLlLoadingPeersInfo.setVisibility(View.GONE);
        mTvTipNonePeer.setVisibility(View.VISIBLE);
    }

    @Override
    public void linkPeerSuccess(String ip) {
        showToast("Successful to connect the socket！");
    }

    @Override
    public void linkPeerError(String message,String targetIp) {
        showToast(message);
        PeerBean peerBean = new PeerBean();
        peerBean.setUserIp(targetIp);
        EventBus.getDefault().post(new LinkSocketResponseEvent(false,peerBean));
    }

    @Override
    public void initServerSocketSuccess() {
        mPresenter.linkPeers(mPeerList);
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.d(TAG, "Opened");
                    if (!mPresenter.isServerSocketConnected()){
                        mHandler.sendEmptyMessage(INIT_SERVER_SOCKET);
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.d(TAG, "Opening");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.d(TAG, "Closed");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.d(TAG, "Closing");
                    mPresenter.disconnect();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Log.d(TAG, "Unknown");
                    break;
            }
        }
    }
}
