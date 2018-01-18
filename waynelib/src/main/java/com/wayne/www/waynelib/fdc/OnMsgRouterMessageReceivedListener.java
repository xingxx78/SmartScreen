package com.wayne.www.waynelib.fdc;


import com.wayne.www.waynelib.fdc.message.FdcMessage;

import java.util.Hashtable;

/**
 * Created by Think on 4/25/2016.
 */
public interface OnMsgRouterMessageReceivedListener {
    void onMsgRouterMessageReceived(MessageRouterClient sender, String msgType, String eventType, Hashtable<String, String> parameter);
}
