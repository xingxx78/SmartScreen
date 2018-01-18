package com.wayne.www.waynelib.fdc;

/**
 * Created by Think on 4/19/2016.
 */
public interface OnMsgRouterClientStateChangedListener {
    void onMsgRouterClientStateChanged(MessageRouterClient sender, MessageRouterClient.MessageRouterClientState newState);
}
