package com.wayne.www.waynelib.fdc.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wayne.www.waynelib.NotifyPropertyChanged;
import com.wayne.www.waynelib.fdc.message.*;

/**
 * Created by Think on 3/15/2016.
 */
public class Pump extends NotifyPropertyChanged {
    private List<Nozzle> nozzles;
    public int PumpNo;
    private PumpOrNozzleState currentState = PumpOrNozzleState.Closed;

    public void setNozzles(List<Nozzle> nozzles) {
        this.nozzles = nozzles;
    }

    public List<Nozzle> getNozzles() {
        return this.nozzles;
    }

    public void setState(PumpOrNozzleState newState) {
        if (0 != this.currentState.compareTo(newState)) {
            this.currentState = newState;
            super.notifyAllListeners(this, "State");
        }
    }

    public PumpOrNozzleState getCurrentState() {
        return this.currentState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Pump))
            return false;

        Pump target = (Pump) obj;
        if (target.PumpNo != this.PumpNo)
            return false;
        if ((target.nozzles == null && this.nozzles != null)
                || target.nozzles != null && this.nozzles == null)
            return false;
        if (target.nozzles == null && this.nozzles == null)
            return true;
        if (target.nozzles.size() != this.nozzles.size())
            return false;
        for (int i = 0; i < target.nozzles.size(); i++) {
            if (!target.nozzles.get(i).equals(this.nozzles.get(i)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0; i < this.nozzles.size(); i++) {
            result += this.PumpNo ^ this.nozzles.get(i).NozzleNo;
        }

        return result;
    }

    public FuelSaleTransaction getLatestUnpaidTrx() {
        List<FuelSaleTransaction> trxs = new ArrayList<>();
        if (this.getNozzles() == null || this.getNozzles().size() == 0) return null;
        for (int i = 0; i < this.getNozzles().size(); i++) {
            if (this.getNozzles().get(i).getTransactions() == null || this.getNozzles().get(i).getTransactions().size() == 0)
                continue;
            for (int j = 0; j < this.getNozzles().get(i).getTransactions().size(); j++) {
                trxs.add(this.getNozzles().get(i).getTransactions().get(j));
            }
        }

        if (trxs.size() == 0) return null;
        Collections.sort(trxs, new FuelSaleTransactionComparer());
        return trxs.get(0);
    }

    public void clearAllTransactionOnNozzles() {
        if (this.getNozzles() == null || this.getNozzles().size() == 0) return;
        for (int i = 0; i < this.getNozzles().size(); i++) {
            this.getNozzles().get(i).clearAllTransaction();
        }
    }

    // sample msg like below:
//    <ServiceResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" RequestType="GetDSPConfiguration" ApplicationSender="android0" WorkstationID="110" RequestID="3" OverallResult="Success">
//        <FDCdata>
//            <FDCTimeStamp>2016-04-29T10:32:10</FDCTimeStamp>
//            <DeviceClass Type="DSP" DeviceID="1">
//                <Product ProductNo="2" ProductName="SYN 2000">
//                      <FuelPrice ModeNo="1">1.777</FuelPrice>
//                </Product>
//                <Product ProductNo="3" ProductName="DIESELS">
//                      <FuelPrice ModeNo="1">1.313</FuelPrice>
//                </Product>
//                <Product ProductNo="4" ProductName="SYN 8000">
//                      <FuelPrice ModeNo="1">1.893</FuelPrice>
//                </Product>
//                <Product ProductNo="7" ProductName="SYN 5000">
//                      <FuelPrice ModeNo="1">1.817</FuelPrice>
//                </Product>
//                <DeviceClass Type="FP" DeviceID="1" PumpNo="1">
//                    <Nozzle NozzleNo="1">
//                          <ProductID ProductNo1="2" ProductNo2="0" BlendRatio="100" BlendProductNo="0" />
//                    </Nozzle>
//                    <Nozzle NozzleNo="2">
//                          <ProductID ProductNo1="3" ProductNo2="0" BlendRatio="100" BlendProductNo="0" />
//                    </Nozzle>
//                    <Nozzle NozzleNo="3">
//                          <ProductID ProductNo1="4" ProductNo2="0" BlendRatio="100" BlendProductNo="0" />
//                    </Nozzle>
//                    <Nozzle NozzleNo="4">
//                          <ProductID ProductNo1="7" ProductNo2="0" BlendRatio="100" BlendProductNo="0" />
//                    </Nozzle>
//                </DeviceClass>
//            </DeviceClass>
//            <FDCStatus>ERRCD_OK</FDCStatus>
//        </FDCdata>
//    </ServiceResponse>
    public static List<Pump> extractPumps(ServiceResponseGetDspConfiguration response) {
        // extract Pumps
        List<Pump> modelPumps = new ArrayList<>();
        List<DeviceClass> pumpDeviceClasses = new ArrayList<>();
        for (int i = 0; i < response.getSingleFdcData().getDeviceClasses().size(); i++) {
            pumpDeviceClasses.add(response.getSingleFdcData().getDeviceClasses().get(i).getDeviceClasses().get(0));
        }

        for (int i = 0; i < pumpDeviceClasses.size(); i++) {
            Pump modelPump = new Pump();
            modelPump.PumpNo = Integer.parseInt(pumpDeviceClasses.get(i).getDeviceId());
            List<Nozzle> modelNozzles = new ArrayList<>();
            List<com.wayne.www.waynelib.fdc.message.Nozzle> fdcNozzles = pumpDeviceClasses.get(i).getNozzles();
            for (int j = 0; j < fdcNozzles.size(); j++) {
                Nozzle modelNozzle = Nozzle.convert(fdcNozzles.get(j),
                        Product.convert(
                                com.wayne.www.waynelib.fdc.message.Product.findProductByProductNo(fdcNozzles.get(j).getProductID().getProductNo1())));
                modelNozzles.add(modelNozzle);
            }

            modelPump.setNozzles(modelNozzles);
            modelPumps.add(modelPump);
        }

        return modelPumps;
    }

//    public static List<Pump> extractPumpsTest(ServiceResponseGetDspConfiguration response, int simulatePumpCount) {
//        // extract Product first
//        List<com.wayne.www.waynelib.fdc.message.Product> fdcProducts = response.getSingleDeviceClass().getProducts();
//        List<Product> modelProducts = new ArrayList<>();
//        for (int i = 0; i < fdcProducts.size(); i++) {
//            modelProducts.add(Product.convert(fdcProducts.get(i)));
//        }
//        //===============
//
//        // extract Pumps
//        List<Pump> modelPumps = new ArrayList<>();
//        List<DeviceClass> fdcPumps = response.getSingleDeviceClass().getDeviceClasses();
//        for (int i = 0; i < simulatePumpCount; i++) {
//            Pump modelPump = new Pump();
//            modelPump.PumpNo = Integer.parseInt(fdcPumps.get(0).getDeviceId()) + i;
//            List<Nozzle> modelNozzles = new ArrayList<>();
//            List<com.wayne.www.waynelib.fdc.message.Nozzle> fdcNozzles = fdcPumps.get(0).getNozzles();
//            for (int j = 0; j < fdcNozzles.size(); j++) {
//                Product linkedProduct = null;
//                // correlate Product
//                for (int k = 0; k < modelProducts.size(); k++) {
//                    if (modelProducts.get(k).ProductNo == Integer.parseInt(fdcNozzles.get(j).getProductID().getProductNo1())) {
//                        linkedProduct = modelProducts.get(k);
//                        break;
//                    }
//                }
//
//                Nozzle modelNozzle = Nozzle.convert(fdcNozzles.get(j), linkedProduct);
//                modelNozzles.add(modelNozzle);
//            }
//
//            modelPump.setNozzles(modelNozzles);
//            modelPumps.add(modelPump);
//        }
//
//        return modelPumps;
//    }

}
