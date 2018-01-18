package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 4/28/2016.
 * Pos Message is send from client to remote Fdc server, for now, there's 1 kind of message belong to this type:
 * ServiceRequest
 */
public abstract class BasePosMessage {
    @ElementList(inline = true)
    private List<PosData> posDataList;

    /* as a client(msg sender), we have no reason to getPosData, but leave here for unit testing purpose */
    public List<PosData> getPosData() {
        return this.posDataList;
    }

    public void setPosData(List<PosData> value) {
        this.posDataList = value;
    }

    /* as a client, we have no reason to getSinglePosData, but leave here for unit testing purpose */
    public PosData getSinglePosData() {
        return this.posDataList.get(0);
    }


    public void setSinglePosData(PosData value) {
        this.posDataList = new ArrayList<>();
        this.posDataList.add(value);
    }

    /* Gets the first DeviceClass element from all DeviceClasses, which from the first PosData element from all PosDatas*/
    public DeviceClass getSingleDeviceClass() {
        if (this.posDataList != null && this.posDataList.size() > 0)
            return this.getSinglePosData().getDeviceClass();
        return null;
    }
}
