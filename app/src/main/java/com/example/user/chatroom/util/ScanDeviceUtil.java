package com.example.user.chatroom.util;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.text.TextUtils;
import android.util.Log;

public class ScanDeviceUtil {

    private static final String TAG = "ScanDeviceUtil";

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 255;
    private static final int QUEUE_LENGTH = 125;

    private String mDevAddress;
    private String mLocAddress;
    private Runtime mRun = Runtime.getRuntime();
    private String mPing = "ping -c 1 -w 3 ";
    private CopyOnWriteArrayList<String> mIpList;
    private ThreadPoolExecutor mExecutor;
    private static ScanDeviceUtil mScanDeviceUtil;
    private ScanDeviceUtil(){
        mIpList = new CopyOnWriteArrayList<>();
    }
    public static ScanDeviceUtil getInstance(){
        if (mScanDeviceUtil == null){
            synchronized (ScanDeviceUtil.class){
                if (mScanDeviceUtil == null){
                    mScanDeviceUtil = new ScanDeviceUtil();
                }
            }
        }
        return mScanDeviceUtil;
    }

    public boolean isFinish(){
        return mExecutor.isTerminated();
    }

    /**
     * @return
     */
    public String getDevAddress(){
        return mDevAddress == null ? "" :mDevAddress;
    }

    public void gc(){
        mRun.gc();
    }

    /**
     * @return List<String>
     */
    public List<String> getIpList(){
        return mIpList;
    }

    /**
     */
    public boolean getLocalAddressPrefix() {
        mDevAddress = getLocAddress();// 获取本机IP地址
        mLocAddress = getLocalAddressIndex(mDevAddress);// 获取本地ip前缀
        if (TextUtils.isEmpty(mLocAddress)){
            return false;
        }
        return true;
    }

    /**
     *
     * @return void
     */
    public void scan() {
        mIpList.clear();
        Log.d(TAG, "开始扫描设备,本机Ip为：" + mDevAddress);
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                QUEUE_LENGTH));


        for (int i = 1; i < 256; i++) {
            final int lastAddress = i;
            Runnable run = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String ping = ScanDeviceUtil.this.mPing + mLocAddress
                            + lastAddress;
                    String currentIp = mLocAddress + lastAddress;
                    if (mDevAddress.equals(currentIp)){
                        return;
                    }
                    Process process = null;
                    try {
                         process = mRun.exec(ping);
                        int result = process.waitFor();
                        if (result == 0) {
                            mIpList.add(currentIp);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Scan the exception" + e.toString());
                    } finally {
                        if (process != null){
                            process.destroy();
                        }
                    }
                }
            };
            mExecutor.execute(run);
        }
        mExecutor.shutdown();
    }

    /**
     *
     * @return void
     */
    public void destory() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }

    /**
     *
     * @return String
     */
    public String getLocAddress() {
        String ipAddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                Enumeration<InetAddress> address = networks.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        ipAddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    private String getLocalAddressIndex(String devAddress) {
        if (!devAddress.equals("")) {
            return devAddress.substring(0, devAddress.lastIndexOf(".") + 1);
        }
        return null;
    }

}
