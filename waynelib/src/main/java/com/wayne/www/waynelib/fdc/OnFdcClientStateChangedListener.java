package com.wayne.www.waynelib.fdc;

/**
 * Created by Think on 4/19/2016.
 */
public interface OnFdcClientStateChangedListener {
    void onFdcClientStateChanged(FdcClient sender, FdcClient.FdcClientState newState);
}
