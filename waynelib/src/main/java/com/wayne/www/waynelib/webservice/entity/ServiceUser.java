package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.Streams;

/**
 * Created by Shawn on 6/8/2017.
 */

public class ServiceUser {
    @SerializedName("UserName")
    private String userName;

    @SerializedName("Alias")
    private String alias;

    public String getUserName() {
        return this.userName;
    }

    public String getAlias() {
        return this.alias;
    }
}
