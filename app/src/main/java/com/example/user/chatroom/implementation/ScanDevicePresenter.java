package com.example.user.chatroom.implementation;

import com.example.user.chatroom.base.BasePresenter;
import com.example.user.chatroom.Interface.ScanDeviceContract;
import com.example.user.chatroom.implementation.ScanDeviceModel;

import java.util.List;

public class ScanDevicePresenter extends BasePresenter<ScanDeviceContract.View> implements ScanDeviceContract.Presenter {

    private ScanDeviceContract.Model model;

    public ScanDevicePresenter(){
        model = new ScanDeviceModel(this);
    }

    @Override
    public void scanDevice() {
        model.scanDevice();
    }

    @Override
    public void scanDeviceSuccess(List<String> ipList) {
        if (isAttachView()){
            getMvpView().scanDeviceSuccess(ipList);
        }
    }

    @Override
    public void scanDeviceError(String message) {
        if (isAttachView()){
            getMvpView().scanDeviceError(message);
        }
    }

}
