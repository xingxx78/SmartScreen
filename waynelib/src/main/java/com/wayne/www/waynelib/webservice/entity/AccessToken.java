package com.wayne.www.waynelib.webservice.entity;

import android.text.TextUtils;
import android.text.format.Time;

import com.google.gson.annotations.SerializedName;

import java.util.GregorianCalendar;

/**
 * Created by Shawn on 5/31/2017.
 */

public class AccessToken {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String userName;
    private String alias;
    private String roleNames;
    @SerializedName("posDeviceFdcClientId")
    private String posDeviceFdcClientId;

    @SerializedName(".issued")
    private String issuedTimeString;
    @SerializedName(".expires")
    private String expiredTimeString;

    private String error;
    private String error_description;

    public String getAccessToken() {
        return access_token;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getRoleNames() {
        return this.roleNames;
    }

    public String getPosDeviceFdcClientId() {
        return this.posDeviceFdcClientId;
    }

    /**
     * by seconds
     *
     * @return
     */
    public int getExpiresIn() {
        return expires_in;
    }

    public GregorianCalendar getIssuedTime() {
        return null;
    }

    public GregorianCalendar getExpiredTime() {
        return null;
    }

    public boolean isRejectedByServer() {
        return this.error != null || this.error_description != null;
    }

    public void setIsRejectedByServer(String error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

    public String getErrorMsg() {
        return this.error_description + " (" + this.error + ")";
    }
}
