package com.example.user.chatroom.Interface;

import java.util.List;

public interface ScanDeviceContract {
    interface View{
        void scanDeviceSuccess(List<String> ipList);
        void scanDeviceError(String message);
    }

    interface Presenter{
        void scanDevice();
        void scanDeviceSuccess(List<String> ipList);
        void scanDeviceError(String message);
    }

    interface Model{
        void scanDevice();
    }
}
