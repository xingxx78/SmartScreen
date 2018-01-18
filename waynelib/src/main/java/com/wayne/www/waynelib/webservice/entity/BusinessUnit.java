package com.wayne.www.waynelib.webservice.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shawn on 7/29/2017.
 */

public class BusinessUnit {
    //    Global,
//    Region,
//    Country,
//    Project,
//    Site,
    @SerializedName("UnitType")
    private int unitType;

    @SerializedName("Name")
    private String name;
    @SerializedName("Description")
    private String description;

    @SerializedName("ParentBusinessUnit")
    private BusinessUnit parentBusinessUnit;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}
