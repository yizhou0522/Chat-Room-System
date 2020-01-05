package com.example.user.chatroom.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SocketManager {

    private static final String TAG = "SocketManager";

    private ConcurrentHashMap<String,Socket> mClients;
    private ConcurrentHashMap<String,SocketThread> mSocketThreads;
    private static SocketManager mSocketManager = null;

    @Override
    public String toString() {
        Collection<Socket> socketCollection = mClients.values();
        StringBuilder s = new StringBuilder();
        for (Socket socket : socketCollection) {
            s.append(socket.isClosed()).append(",");
        }
        Collection<SocketThread> socketThreads = mSocketThreads.values();
        StringBuilder s1 = new StringBuilder();
        for (SocketThread socketThread : socketThreads) {
            s1.append(socketThread.getState()).append(",");
        }

        return "SocketManager{" +
                "socketIp:"+mClients.keySet()+","+
                "isClosedSocket:"+s1+
                '}';
    }

    private SocketManager(){
        mClients = new ConcurrentHashMap<>();
        mSocketThreads = new ConcurrentHashMap<>();
    }

    public static SocketManager getInstance(){
        if (mSocketManager == null){
            synchronized (SocketManager.class){
                if (mSocketManager == null){
                    mSocketManager = new SocketManager();
                }
            }
        }
        return mSocketManager;
    }



    /**
     * @param ip
     * @return
     */
    public SocketThread getSocketThreadByIp(String ip){
        SocketThread socketThread = mSocketThreads.get(ip);
        if (socketThread == null){
            mSocketThreads.remove(ip);
            return null;
        }
        return socketThread;
    }

    /**
     * @param ip
     */
    public void removeSocketThreadByIp(String ip){
        SocketThread socketThread = mSocketThreads.remove(ip);
        if (socketThread != null){
            socketThread.interrupt();
        }
    }

    /**
     * @param ip
     * @param socketThread
     */
    public void addSocketThread(String ip,SocketThread socketThread){
//        if (mSocketThreads.containsKey(ip)){
//            removeSocketByIp(ip);
//        }
        mSocketThreads.put(ip, socketThread);
    }

    /**
     * @return
     */
    public Set<Map.Entry<String, Socket>> getSocketSet(){
        return  mClients.entrySet();
    }

    /**
     * @return
     */
    public int socketCount(){
        return mClients.size();
    }

    /**
     */
    public void destroy(){
        Collection<Socket> socketCollection = mClients.values();
        for (Socket socket : socketCollection) {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mClients.clear();
        Collection<SocketThread> socketThreadCollection = mSocketThreads.values();
        for (SocketThread socketThread : socketThreadCollection) {
            if (socketThread != null){
                socketThread.interrupt();
            }
        }
        mSocketThreads.clear();
    }

    /**
     * @param ip
     * @return socket or null
     */
    public Socket querySocketByIp(String ip){
        return mClients.get(ip);
    }

    /**
     * @param ip
     */
    public void removeSocketByIp(String ip){
        try {
            Socket socket = mClients.remove(ip);
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ip
     * @return
     */
    public boolean isContainSocket(String ip){
        return mClients.containsKey(ip);
    }

    /**
     * @param ip
     * @return return true if socket is closed or null ,else return false
     */
    public boolean isClosedSocket(String ip){
        Socket socket = mClients.get(ip);
        return socket == null || socket.isClosed();
    }

    /**
     * @param ip
     * @param s
     */
    public void addSocket(String ip,Socket s){
        mClients.put(ip, s);
    }
}
