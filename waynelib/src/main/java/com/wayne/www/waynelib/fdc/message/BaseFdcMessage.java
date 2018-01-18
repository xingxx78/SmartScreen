package com.wayne.www.waynelib.fdc.message;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 4/28/2016.
 * Fdc Message is send from remote Fdc server, for now, there's 2 kind of message belong to this type:
 * ServiceResponse, and unsolicited FdcMessage.
 */
public abstract class BaseFdcMessage {
    @ElementList(inline = true)
    private List<FdcData> fdcDataList;

    public List<FdcData> getFdcData() {
        return this.fdcDataList;
    }

    /* as a client, we have no reason to setFdcData, but leave here for unit testing purpose */
    public void setFdcData(List<FdcData> value) {
        this.fdcDataList = value;
    }

    public FdcData getSingleFdcData() {
        return this.fdcDataList.get(0);
    }

    /* as a client, we have no reason to setFdcData, but leave here for unit testing purpose */
    public void setSingleFdcData(FdcData value) {
        this.fdcDataList = new ArrayList<>();
        this.fdcDataList.add(value);
    }

    /* Gets the first DeviceClass element from all DeviceClasses, which from the first FdcData element from all FdcDatas*/
    public DeviceClass getSingleDeviceClass() {
        if (this.fdcDataList != null && this.fdcDataList.size() > 0)
            if (this.getSingleFdcData().getDeviceClasses() != null && this.getSingleFdcData().getDeviceClasses().size() > 0)
                return this.getSingleFdcData().getDeviceClasses().get(0);
        return null;
    }
}
