package com.example.user.chatroom.implementation;

import android.util.Log;

import com.example.user.chatroom.activity.App;
import com.example.user.chatroom.Interface.BaseContract;
import com.example.user.chatroom.Interface.ScanDeviceContract;
import com.example.user.chatroom.util.ScanDeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class ScanDeviceModel implements ScanDeviceContract.Model, BaseContract.Model {

    private ScanDeviceContract.Presenter mPresenter;

    public ScanDeviceModel(ScanDeviceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void scanDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!ScanDeviceUtil.getInstance().getLocalAddressPrefix()) {
                    mPresenter.scanDeviceError("Fail to scan the protocol, please check the WIFI！");
                    return;
                }
                App.setMyIP(ScanDeviceUtil.getInstance().getDevAddress());
                Long startTime = System.currentTimeMillis();
                ScanDeviceUtil.getInstance().scan();
                while (true) {
                    if (System.currentTimeMillis() - startTime > 30000) {
                        mPresenter.scanDeviceError("Please scan the protocol again！");
                        break;
                    }
                    try {
                        if (ScanDeviceUtil.getInstance().isFinish()) {
                            ScanDeviceUtil.getInstance().gc();
                            List<String> ipList = new ArrayList<>(ScanDeviceUtil.getInstance()
                                    .getIpList());
                            Log.d("ScanDeviceModel", "ipList：" + ipList.toString());
                            mPresenter.scanDeviceSuccess(ipList);
                            break;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        break;
                    }
                }
            }


        }).start();
    }
}
