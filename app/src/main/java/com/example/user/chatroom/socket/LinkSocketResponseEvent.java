package com.example.user.chatroom.socket;

import com.example.user.chatroom.bean.PeerBean;

public class LinkSocketResponseEvent {
    private boolean state;
    private PeerBean peerBean;

    public PeerBean getPeerBean() {
        return peerBean;
    }

    public void setPeerBean(PeerBean peerBean) {
        this.peerBean = peerBean;
    }

    public LinkSocketResponseEvent(boolean state, PeerBean peerBean){
        this.state = state;
        this.peerBean = peerBean;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
