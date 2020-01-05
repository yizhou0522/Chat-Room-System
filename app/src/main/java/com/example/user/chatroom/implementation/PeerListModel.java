package com.example.user.chatroom.implementation;

import android.util.Log;

import com.example.user.chatroom.activity.App;
import com.example.user.chatroom.bean.PeerBean;
import com.example.user.chatroom.config.Protocol;
import com.example.user.chatroom.Interface.PeerListContract;
import com.example.user.chatroom.socket.SocketManager;
import com.example.user.chatroom.socket.SocketThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PeerListModel implements PeerListContract.Model {

    public static final String TAG = "PeerListModel";


    private static final int CORE_POOL_SIZE = 255;

    private static final int MAX_IMUM_POOL_SIZE = 255;

    private PeerListContract.Presenter mPresenter;
    private ServerSocket mServerSocket;
    private ThreadPoolExecutor mExecutor;
    private AtomicBoolean isInitServerSocket;
    private Thread mPollingSocketThread;

    public PeerListModel(PeerListContract.Presenter presenter) {
        mPresenter = presenter;
        isInitServerSocket = new AtomicBoolean(false);
    }

    @Override
    public void initServerSocket() {
        isInitServerSocket.set(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mServerSocket = new ServerSocket(3000);
                } catch (IOException e) {
                    e.printStackTrace();
                    mPresenter.serverSocketError("Fail to start ServerSocket, port 3000 is " +
                            "occupied!");
                    isInitServerSocket.set(false);
                    return;
                }
                mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE,
                        2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                        CORE_POOL_SIZE));
                mExecutor.allowCoreThreadTimeOut(true);
                isInitServerSocket.set(true);
                mPresenter.initServerSocketSuccess();
                while (true) {
                    Socket socket;
                    try {
                        socket = mServerSocket.accept();
                        String ip = socket.getInetAddress().getHostAddress();
                        Log.d(TAG, "Received a socket connection,ip:" + ip);
                        SocketManager socketManager = SocketManager.getInstance();
                        if (socketManager.isClosedSocket(ip)) {
                            SocketThread socketThread = new SocketThread(socket, mPresenter);
                            socketManager.addSocket(ip, socket);
                            socketManager.addSocketThread(ip, socketThread);
                            mExecutor.execute(socketThread);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "mServerSocket.accept() error !");
                        break;
                    }
                }
                mExecutor.shutdownNow();
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isInitServerSocket.set(false);
            }
        }).start();
    }

    @Override
    public void linkPeers(final List<String> list) {
        if (!isInitServerSocket()) {
            mPresenter.linkPeerError("Please check the status of WIFI!", "");
            mPresenter.updatePeerList(new ArrayList<PeerBean>());
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final String targetIp : list) {
                        mExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                linkSocket(targetIp);
                            }
                        });
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (SocketManager.getInstance().socketCount() == 0) {
                        mPresenter.updatePeerList(new ArrayList<PeerBean>());
                    }
                }
            }).start();
        }
    }

    @Override
    public void linkPeer(final String targetIp) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!linkSocket(targetIp)) {
                    mPresenter.linkPeerError("Fail to create Socket connection; web connection " +
                            "fails or someone has quited.", targetIp);
                } else {
                    mPresenter.linkPeerSuccess(targetIp);
                }
            }
        });
    }

    private boolean linkSocket(String targetIp) {
        Socket socket;
        try {
            socket = new Socket(targetIp, 3000);
            Log.d(TAG, "linkPeers: ip" + targetIp + "success !");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "linkPeer ip = " + targetIp + ",Fail to connect socket");
            return false;
        }
        SocketManager socketManager = SocketManager.getInstance();
        SocketThread socketThread;
        if (socketManager.isClosedSocket(targetIp)) {
            socketThread = new SocketThread(socket, mPresenter);
            socketManager.addSocket(targetIp, socket);
            socketManager.addSocketThread(targetIp, socketThread);
            mExecutor.execute(socketThread);
            return socketThread.sendRequest(App.getUserBean(), Protocol.CONNECT);
        } else {
            return false;
        }
    }

    @Override
    public void disconnect() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isInitServerSocket.set(false);
            SocketManager.getInstance().destroy();
            mExecutor.shutdownNow();
        }
    }

    @Override
    public boolean isInitServerSocket() {
        return isInitServerSocket.get();
    }

    private void pollingSocket() {
        mPollingSocketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        Set<Map.Entry<String, Socket>> socketSet = SocketManager.getInstance()
                                .getSocketSet();
                        Iterator<Map.Entry<String, Socket>> iterator = socketSet.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, Socket> entry = iterator.next();
                            Socket socket = entry.getValue();
                            Log.d(TAG, "pollingSocket: " + entry.getKey());
                            DataOutputStream dos = null;
                            try {
                                if (socket == null) {
                                    iterator.remove();
                                    continue;
                                }
                                OutputStream os = socket.getOutputStream();
                                os.write(Protocol.KEEP_USER);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, "pollingSocket error: " + entry.getKey());
                                iterator.remove();
                                mPresenter.removePeer(entry.getKey());
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mPollingSocketThread.start();
    }


}
