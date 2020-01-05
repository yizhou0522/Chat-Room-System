package com.example.user.chatroom.Interface;


public interface OnClickRecyclerViewListener {

    /**
     * @param position
     */
    void onItemClick(int position);

    /**
     * @param position
     * @return
     */
    boolean onItemLongClick(int position);

}
