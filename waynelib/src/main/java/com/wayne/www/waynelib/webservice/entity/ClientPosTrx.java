package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shawn on 6/16/2017.
 */

public class ClientPosTrx {
    @SerializedName("PosTrxUniqueId")
    private String posTrxUniqueId;

    @SerializedName("RequestingCreationTimeInPosClient")
    private String requestingCreationTimeInPosClient;

    @SerializedName("Items")
    private List<ClientRingUpPosItem> items = new ArrayList<ClientRingUpPosItem>();
    @SerializedName("Type")
    private int posTrxType;

    public ClientPosTrx(int posTrxType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String currentDateAndTime = sdf.format(new Date());
        this.requestingCreationTimeInPosClient = currentDateAndTime;
        this.posTrxType = posTrxType;
    }

    /**
     * specify the Id value (a Guid value), the server side will create the PosTrx object in database by this value.
     * Leave it null in most cases, and let the server side create the Id value.
     *
     * @param posTrxUniqueId
     */
    public void setPosTrxUniqueId(String posTrxUniqueId) {
        this.posTrxUniqueId = posTrxUniqueId;
    }

    public void addClientRingUpPosItem(ClientRingUpPosItem item) {
        this.items.add(item);
    }

    public void addClientRingUpPosItems(List<ClientRingUpPosItem> items) {
        this.items.addAll(items);
    }
}
