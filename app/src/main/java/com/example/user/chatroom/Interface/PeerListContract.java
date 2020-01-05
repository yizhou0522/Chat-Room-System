package com.example.user.chatroom.Interface;

import com.example.user.chatroom.bean.MessageBean;
import com.example.user.chatroom.bean.PeerBean;

import java.util.List;

public interface PeerListContract {

    interface View{
        void updatePeerList(List<PeerBean> list);
        void messageReceived(MessageBean messageBean);
        void fileReceiving(MessageBean messageBean);
        void addPeer(PeerBean peerBean);
        void removePeer(String ip);
        void serverSocketError(String msg);
        void linkPeerSuccess(String ip);
        void linkPeerError(String message,String targetIp);
        void initServerSocketSuccess();
    }

    interface Model{
        void initServerSocket();
        void linkPeers(List<String> list);
        void linkPeer(String targetIp);
        void disconnect();
        boolean isInitServerSocket();
    }

    interface Presenter{
        void disconnect();
        void initServerSocketSuccess();
        void initSocket();
        void linkPeers(List<String> list);
        void linkPeer(String targetIp);
        void linkPeerSuccess(String ip);
        void linkPeerError(String message,String targetIp);
        void updatePeerList(List<PeerBean> list);
        void addPeer(PeerBean peerBean);
        void messageReceived(MessageBean messageBean);
        void removePeer(String ip);
        void serverSocketError(String msg);
        boolean isServerSocketConnected();
        void fileReceiving(MessageBean messageBean);
    }

}
