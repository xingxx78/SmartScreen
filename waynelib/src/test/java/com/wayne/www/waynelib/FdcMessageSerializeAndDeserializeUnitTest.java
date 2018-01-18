package com.wayne.www.waynelib;

import com.wayne.www.waynelib.fdc.message.DeviceClass;
import com.wayne.www.waynelib.fdc.message.FdcData;
import com.wayne.www.waynelib.fdc.message.FdcMessageFPStateChange;
import com.wayne.www.waynelib.fdc.message.FdcMessageFuelSaleTrx;
import com.wayne.www.waynelib.fdc.message.Nozzle;
import com.wayne.www.waynelib.fdc.message.PosData;
import com.wayne.www.waynelib.fdc.message.Product;
import com.wayne.www.waynelib.fdc.message.ServiceRequestAuthoriseFuelPoint;
import com.wayne.www.waynelib.fdc.message.ServiceRequestLogOnV07;
import com.wayne.www.waynelib.fdc.message.ServiceRequestStartForecourt;
import com.wayne.www.waynelib.fdc.message.ServiceResponseAuthoriseFuelPoint;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FdcMessageSerializeAndDeserializeUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void serviceRequestLogOn_Serialize() throws Exception {
        ServiceRequestLogOnV07 logOn = new ServiceRequestLogOnV07();
        logOn.setRequestID("1983");
        logOn.setApplicationSender("hello");
        PosData posData = new PosData();
        posData.setPosTimeStamp("2009-11-20T17:30:50");
        posData.setInterfaceVersion("1004");
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        logOn.setPosData(posDatas);
        Serializer serializer = new Persister();
        final List<String> result = new ArrayList<>();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                byte[] _ = new byte[]{(byte) oneByte};
                String onechar = new String(_, StandardCharsets.UTF_8);
                result.add(onechar);
            }
        };
        try {
            serializer.write(logOn, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        String finalResult = "";
        for (int i = 0; i < result.size(); i++)
            finalResult += result.get(i);

        assertEquals(true, finalResult.length() > 0);
    }

    @Test
    public void serviceRequestLogOn_Deserialize() throws Exception {
        String rawXmlString = "<ServiceRequest RequestType=\"LogOn\" ApplicationSender=\"hello\" " +
                "WorkstationID=\"\" RequestID=\"1983\" xmlns=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<POSdata>" +
                "<POSTimeStamp>2009-11-20T17:30:50</POSTimeStamp>" +
                "<interfaceVersion>1004</interfaceVersion>" +
                "</POSdata>" +
                "</ServiceRequest>";
        Serializer serializer = new Persister();
        ServiceRequestLogOnV07 deserializedLogOn = null;
        try {
            deserializedLogOn = serializer.read(ServiceRequestLogOnV07.class, rawXmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        ServiceRequestLogOnV07 localLogOn = new ServiceRequestLogOnV07();
        localLogOn.setRequestID("1983");
        localLogOn.setApplicationSender("hello");
        PosData posData = new PosData();
        posData.setPosTimeStamp("2009-11-20T17:30:50");
        posData.setInterfaceVersion("1004");
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        localLogOn.setPosData(posDatas);

        assertEquals("getRequestID", true, localLogOn.getRequestID().equals(deserializedLogOn.getRequestID()));
        assertEquals("getApplicationSender", true, localLogOn.getApplicationSender().equals(deserializedLogOn.getApplicationSender()));
        assertEquals("getPosData().size()", true, localLogOn.getPosData().size() == deserializedLogOn.getPosData().size());
        assertEquals("getPosTimeStamp", true, localLogOn.getPosData().get(0).getPosTimeStamp().equals(deserializedLogOn.getPosData().get(0).getPosTimeStamp()));
        assertEquals("getInterfaceVersion", true, localLogOn.getPosData().get(0).getInterfaceVersion().equals(deserializedLogOn.getPosData().get(0).getInterfaceVersion()));
    }

    @Test
    public void serviceRequestStartForecourt_Serialize() throws Exception {
        ServiceRequestStartForecourt startForecourt = new ServiceRequestStartForecourt();
        startForecourt.setRequestID("1983");
        startForecourt.setApplicationSender("hello");
        PosData posData = new PosData();
        posData.setPosTimeStamp("2009-11-20T17:30:50");
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        startForecourt.setPosData(posDatas);
        Serializer serializer = new Persister();
        final List<String> result = new ArrayList<>();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                byte[] _ = new byte[]{(byte) oneByte};
                String onechar = new String(_, StandardCharsets.UTF_8);
                result.add(onechar);
            }
        };
        try {
            serializer.write(startForecourt, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        String finalResult = "";
        for (int i = 0; i < result.size(); i++)
            finalResult += result.get(i);

        assertEquals(true, finalResult.length() > 0);
    }


    @Test
    public void serviceRequestStartForecourt_Deserialize() throws Exception {
        String rawXmlString = "<ServiceRequest RequestType=\"StartForecourt\" ApplicationSender=\"hello\" " +
                "WorkstationID=\"\" RequestID=\"1983\" xmlns=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<POSdata>" +
                "<POSTimeStamp>2009-11-20T17:30:50</POSTimeStamp>" +
                "</POSdata>" +
                "</ServiceRequest>";
        Serializer serializer = new Persister();
        ServiceRequestStartForecourt deserializedStartForecourt = null;
        try {
            deserializedStartForecourt = serializer.read(ServiceRequestStartForecourt.class, rawXmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        ServiceRequestStartForecourt localStartForecourt = new ServiceRequestStartForecourt();
        localStartForecourt.setRequestID("1983");
        localStartForecourt.setApplicationSender("hello");
        PosData posData = new PosData();
        posData.setPosTimeStamp("2009-11-20T17:30:50");
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        localStartForecourt.setPosData(posDatas);

        assertEquals("getRequestID", true, localStartForecourt.getRequestID().equals(deserializedStartForecourt.getRequestID()));
        assertEquals("getApplicationSender", true, localStartForecourt.getApplicationSender().equals(deserializedStartForecourt.getApplicationSender()));
        assertEquals("getPosData().size()", true, localStartForecourt.getPosData().size() == deserializedStartForecourt.getPosData().size());
        assertEquals("getPosTimeStamp", true, localStartForecourt.getPosData().get(0).getPosTimeStamp().equals(deserializedStartForecourt.getPosData().get(0).getPosTimeStamp()));
    }

    @Test
    public void fdcMessageFPStateChange_Serialize() throws Exception {
        FdcMessageFPStateChange fpStateChange = new FdcMessageFPStateChange();
        fpStateChange.setMessageId("1983");
        fpStateChange.setApplicationSender("hello");
        fpStateChange.setWorkstationID("wid");
        FdcData fdcData = new FdcData();
        fdcData.setFdcTimeStamp("2119-11-20T17:30:30");
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("23");
        deviceClass.setPumpNo(1);
        deviceClass.setDeviceState("FDC_READY");
        deviceClass.setLockingApplicationSender("POSsell1");
        Nozzle nozzle1 = new Nozzle(1, "NozzleUp", "Unlocked", "ERRCD_OK");
        Nozzle nozzle2 = new Nozzle(2, "NozzleDown", "Unlocked", "ERRCD_OK");
        Nozzle nozzle3 = new Nozzle(3, "NozzleDown", "Unlocked", "ERRCD_OK");
        Nozzle nozzle4 = new Nozzle(4, "NozzleDown", "Unlocked", "ERRCD_OK");
        List<Nozzle> ns = new ArrayList<>();
        ns.add(nozzle1);
        ns.add(nozzle2);
        ns.add(nozzle3);
        ns.add(nozzle4);
        deviceClass.setNozzle(ns);
        fdcData.setSingleDeviceClass(deviceClass);
        List<FdcData> fdcDatas = new ArrayList<>();
        fdcDatas.add(fdcData);
        fpStateChange.setFdcData(fdcDatas);
        Serializer serializer = new Persister();
        final List<String> result = new ArrayList<>();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                byte[] _ = new byte[]{(byte) oneByte};
                String onechar = new String(_, StandardCharsets.UTF_8);
                result.add(onechar);
            }
        };
        try {
            serializer.write(fpStateChange, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        String finalResult = "";
        for (int i = 0; i < result.size(); i++)
            finalResult += result.get(i);

        assertEquals(true, finalResult.length() > 0);
    }


    @Test
    public void fdcMessageFPStateChange_Deserialize() throws Exception {
        String rawXmlString = "<FDCMessage MessageType=\"FPStateChange\" ApplicationSender=\"CD001\" " +
                "WorkstationID=\"CD-01\" MessageID=\"073322\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:noNamespaceSchemaLocation=\"FDC_FPStateChange_Unsolicited.xsd\">    <FDCdata>" +
                "<FDCTimeStamp> 2009-11-20T17:30:50 </FDCTimeStamp>    " +
                "<DeviceClass Type=\"FP\" DeviceID=\"23\" PumpNo=\"1\">       " +
                "<DeviceState>FDC_READY</DeviceState>      " +
                "<LockingApplicationSender> POSsell1</LockingApplicationSender>" +
                "<Nozzle  NozzleNo=\"1\">         <LogicalNozzle>NozzleUp</LogicalNozzle>         <LogicalState>Unlocked</LogicalState>" +
                "<TankLogicalState>Unlocked</TankLogicalState>        <ErrorCode>ERRCD_OK</ErrorCode>       </Nozzle>" +
                "<Nozzle NozzleNo=\"2\">         <LogicalNozzle>NozzleDown</LogicalNozzle>         <LogicalState>Unlocked</LogicalState>" +
                "<TankLogicalState>Unlocked</TankLogicalState>         <ErrorCode>ERRCD_OK</ErrorCode>       </Nozzle>" +
                "<Nozzle NozzleNo=\"3\">         <LogicalNozzle>NozzleDown</LogicalNozzle>         <LogicalState>Unlocked</LogicalState>" +
                "<TankLogicalState>Unlocked</TankLogicalState>         <ErrorCode>ERRCD_OK</ErrorCode>       </Nozzle>" +
                "<Nozzle NozzleNo=\"4\">         <LogicalNozzle>NozzleDown</LogicalNozzle>         <LogicalState>Unlocked</LogicalState>" +
                "<TankLogicalState>Unlocked</TankLogicalState>         <ErrorCode>ERRCD_OK</ErrorCode>       </Nozzle>" +
                "<ErrorCode>ERRCD_OK</ErrorCode>     </DeviceClass>   </FDCdata> </FDCMessage>";
        Serializer serializer = new Persister();
        FdcMessageFPStateChange deserializedFPStateChange = null;
        try {
            deserializedFPStateChange = serializer.read(FdcMessageFPStateChange.class, rawXmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        FdcMessageFPStateChange localFpStateChange = new FdcMessageFPStateChange();
        localFpStateChange.setMessageId("073322");
        localFpStateChange.setApplicationSender("CD001");
        localFpStateChange.setWorkstationID("CD-01");
        FdcData fdcData = new FdcData();
        fdcData.setFdcTimeStamp(" 2009-11-20T17:30:50");
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("23");
        deviceClass.setPumpNo(1);
        deviceClass.setDeviceState("FDC_READY");
        deviceClass.setLockingApplicationSender("POSsell1");
        Nozzle nozzle1 = new Nozzle(1, "NozzleUp", "Unlocked", "ERRCD_OK");
        Nozzle nozzle2 = new Nozzle(2, "NozzleDown", "Unlocked", "ERRCD_OK");
        Nozzle nozzle3 = new Nozzle(3, "NozzleDown", "Unlocked", "ERRCD_OK");
        Nozzle nozzle4 = new Nozzle(4, "NozzleDown", "Unlocked", "ERRCD_OK");
        List<Nozzle> ns = new ArrayList<>();
        ns.add(nozzle1);
        ns.add(nozzle2);
        ns.add(nozzle3);
        ns.add(nozzle4);
        deviceClass.setNozzle(ns);
        fdcData.setSingleDeviceClass(deviceClass);
        List<FdcData> fdcDatas = new ArrayList<>();
        fdcDatas.add(fdcData);
        localFpStateChange.setFdcData(fdcDatas);

        assertEquals("MessageType", true, localFpStateChange.getMessageType().equals(deserializedFPStateChange.getMessageType()));
        assertEquals("getApplicationSender", true, localFpStateChange.getApplicationSender().equals(deserializedFPStateChange.getApplicationSender()));
        assertEquals("MessageID", true, localFpStateChange.getMessageId().equals(deserializedFPStateChange.getMessageId()));
        assertEquals("getFdcData().size()", true, localFpStateChange.getFdcData().size() == deserializedFPStateChange.getFdcData().size());
        assertEquals("FdcData.FDCTimeStamp", true, localFpStateChange.getFdcData().get(0).getFdcTimeStamp().equals(localFpStateChange.getFdcData().get(0).getFdcTimeStamp()));
        assertEquals("FdcData.DeviceClass.DeviceId", true, localFpStateChange.getSingleDeviceClass().getDeviceId() == (localFpStateChange.getSingleDeviceClass().getDeviceId()));
        assertEquals("FdcData.DeviceClass.PumpNo", true, localFpStateChange.getSingleDeviceClass().getPumpNo() == (localFpStateChange.getSingleDeviceClass().getPumpNo()));
        assertEquals("FdcData.DeviceClass.DeviceState", true, localFpStateChange.getSingleDeviceClass().getDeviceState()
                .equals(localFpStateChange.getSingleDeviceClass().getDeviceState()));
        assertEquals("FdcData.DeviceClass.LockingApplicationSender", true, localFpStateChange.getSingleDeviceClass().getLockingApplicationSender()
                .equals(localFpStateChange.getSingleDeviceClass().getLockingApplicationSender()));
        assertEquals("FdcData.DeviceClass.NozzleCount", true, localFpStateChange.getSingleDeviceClass().getNozzles().size()
                == localFpStateChange.getSingleDeviceClass().getNozzles().size());
    }

    String fdcMessageFuelSaleTrx_RawXmlString = "<FDCMessage MessageType=\"FuelSaleTrx\" ApplicationSender=\"CD001\" " +
            "WorkstationID=\"CD-01\" MessageID=\"073322\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:noNamespaceSchemaLocation=\"FDC_FuelSaleTrx_Unsolicited.xsd\"> <FDCdata>    " +
            "<FDCTimeStamp>2009-11-20T17:30:50</FDCTimeStamp>     " +
            "<DeviceClass Type=\"FP\" DeviceID=\"23\" PumpNo=\"1\" NozzleNo=\"1\" " +
            "TransactionSeqNo=\"120301\" >       <State>Locked</State>       " +
            "<ReleaseToken></ReleaseToken>       <FuelMode ModeNo=\"2\"></FuelMode>       " +
            "<Amount>76.96</Amount>       <Volume>38.521</Volume>       <UnitPrice>1.999</UnitPrice>" +
            "<VolumeProduct1>38.52</VolumeProduct1>       <VolumeProduct2></VolumeProduct2>   " +
            "<ProductNo1>1000</ProductNo1>       <ProductNo2></ProductNo2>      " +
            "<BlendRatio>100</BlendRatio>       <LockingApplicationSender>POSsell1</LockingApplicationSender>"
            + "<AuthorisationApplicationSender>POSsell1</AuthorisationApplicationSender>   " +
            "<DSPFields>Field1 Field2 …Fieldn CheckSum</DSPFields>   " +
            "<CRCMode>ProcedureNo=\"1\"</CRCMode>       <ErrorCode>ERRCD_OK</ErrorCode> " +
            "</DeviceClass>   </FDCdata>  </FDCMessage>  ";

    @Test
    public void fdcMessageFuelSaleTrx_Serialize() throws Exception {
        FdcMessageFuelSaleTrx fuelSaleTrx = new FdcMessageFuelSaleTrx();
        fuelSaleTrx.setMessageId("073322");
        fuelSaleTrx.setApplicationSender("CD001");
        fuelSaleTrx.setWorkstationID("CD-01");
        FdcData fdcData = new FdcData();
        fdcData.setFdcTimeStamp("2009-11-20T17:30:50");
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("23");
        deviceClass.setPumpNo(1);
        deviceClass.setNozzleNo(1);
        deviceClass.setTransactionSeqNo("120301");
        deviceClass.setState("Locked");
        deviceClass.setReleaseTokenElement("");
        deviceClass.setAmount(76.96f);
        deviceClass.setVolume(38.521f);
        deviceClass.setUnitPrice(1.999f);
        deviceClass.setProductNo1("1000");
        deviceClass.setLockingApplicationSender("POSsell1");
        deviceClass.setAuthorisationApplicationSender("POSsell1");
        deviceClass.setErrorCode("ERRCD_OK");
        fdcData.setSingleDeviceClass(deviceClass);
        List<FdcData> fdcDatas = new ArrayList<>();
        fdcDatas.add(fdcData);
        fuelSaleTrx.setFdcData(fdcDatas);
        Serializer serializer = new Persister();
        final List<String> result = new ArrayList<>();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                byte[] _ = new byte[]{(byte) oneByte};
                String onechar = new String(_, StandardCharsets.UTF_8);
                result.add(onechar);
            }
        };
        try {
            serializer.write(fuelSaleTrx, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        String finalResult = "";
        for (int i = 0; i < result.size(); i++)
            finalResult += result.get(i);

        assertEquals(finalResult, true, finalResult.length() > 0);
    }


    @Test
    public void fdcMessageFuelSaleTrx_Deserialize() throws Exception {

        Serializer serializer = new Persister();
        FdcMessageFuelSaleTrx deserializedFuelSaleTrx = null;
        try {
            deserializedFuelSaleTrx = serializer.read(FdcMessageFuelSaleTrx.class, fdcMessageFuelSaleTrx_RawXmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        FdcMessageFuelSaleTrx localFuelSaleTrx = new FdcMessageFuelSaleTrx();
        localFuelSaleTrx.setMessageId("073322");
        localFuelSaleTrx.setApplicationSender("CD001");
        localFuelSaleTrx.setWorkstationID("CD-01");
        FdcData fdcData = new FdcData();
        fdcData.setFdcTimeStamp("2009-11-20T17:30:50");
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("23");
        deviceClass.setPumpNo(1);
        deviceClass.setNozzleNo(1);
        deviceClass.setTransactionSeqNo("120301");
        deviceClass.setState("Locked");
        //deviceClass.setReleaseToken("");
        deviceClass.setAmount(76.96f);
        deviceClass.setVolume(38.521f);
        deviceClass.setUnitPrice(1.999f);
        deviceClass.setProductNo1("1000");
        deviceClass.setLockingApplicationSender("POSsell1");
        deviceClass.setAuthorisationApplicationSender("POSsell1");
        deviceClass.setErrorCode("ERRCD_OK");
        fdcData.setSingleDeviceClass(deviceClass);
        List<FdcData> fdcDatas = new ArrayList<>();
        fdcDatas.add(fdcData);
        localFuelSaleTrx.setFdcData(fdcDatas);

        assertEquals("MessageType", true, localFuelSaleTrx.getMessageType().equals(deserializedFuelSaleTrx.getMessageType()));
        assertEquals("getApplicationSender", true, localFuelSaleTrx.getApplicationSender().equals(deserializedFuelSaleTrx.getApplicationSender()));
        assertEquals("MessageID", true, localFuelSaleTrx.getMessageId().equals(deserializedFuelSaleTrx.getMessageId()));
        assertEquals("getFdcData().size()", true, localFuelSaleTrx.getFdcData().size() == deserializedFuelSaleTrx.getFdcData().size());
        assertEquals("FdcData.FDCTimeStamp", true, localFuelSaleTrx.getFdcData().get(0).getFdcTimeStamp().equals(deserializedFuelSaleTrx.getFdcData().get(0).getFdcTimeStamp()));
        assertEquals("FdcData.DeviceClass.DeviceId", true, localFuelSaleTrx.getSingleDeviceClass().getDeviceId().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getDeviceId()));
        assertEquals("FdcData.DeviceClass.PumpNo", true, localFuelSaleTrx.getSingleDeviceClass().getPumpNo()
                == (deserializedFuelSaleTrx.getSingleDeviceClass().getPumpNo()));
        assertEquals("FdcData.DeviceClass.NozzleNo", true, localFuelSaleTrx.getSingleDeviceClass().getNozzleNo()
                == (deserializedFuelSaleTrx.getSingleDeviceClass().getNozzleNo()));
        assertEquals("FdcData.DeviceClass.TransactionSeqNo", true, localFuelSaleTrx.getSingleDeviceClass().getTransactionSeqNo()
                == (deserializedFuelSaleTrx.getSingleDeviceClass().getTransactionSeqNo()));

        assertEquals("FdcData.DeviceClass.State", true, localFuelSaleTrx.getSingleDeviceClass().getState()
                .equals(deserializedFuelSaleTrx.getSingleDeviceClass().getState()));
        if (localFuelSaleTrx.getSingleDeviceClass().
                getReleaseTokenElement() == null && deserializedFuelSaleTrx.getSingleDeviceClass().getReleaseTokenElement() != null)
            fail("ReleaseToken is null in local");
        if (localFuelSaleTrx.getSingleDeviceClass().
                getReleaseTokenElement() != null && deserializedFuelSaleTrx.getSingleDeviceClass().getReleaseTokenElement() == null)
            fail("ReleaseToken is null in new");
        if (localFuelSaleTrx.getSingleDeviceClass().
                getReleaseTokenElement() != null && deserializedFuelSaleTrx.getSingleDeviceClass().getReleaseTokenElement() != null)
            assertEquals("FdcData.DeviceClass.ReleaseToken, local: " + localFuelSaleTrx.getSingleDeviceClass().
                            getReleaseTokenElement() + ", new: " + deserializedFuelSaleTrx.getSingleDeviceClass().getReleaseTokenElement(), true,
                    localFuelSaleTrx.getSingleDeviceClass().
                            getReleaseTokenElement().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getReleaseTokenElement()));
        assertEquals("FdcData.DeviceClass.Amount", true,
                localFuelSaleTrx.getSingleDeviceClass().
                        getAmount().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getAmount()));
        assertEquals("FdcData.DeviceClass.Volume", true,
                localFuelSaleTrx.getSingleDeviceClass().
                        getVolume().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getVolume()));
        assertEquals("FdcData.DeviceClass.UnitPrice", true,
                localFuelSaleTrx.getSingleDeviceClass().
                        getUnitPrice().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getUnitPrice()));
        assertEquals("FdcData.DeviceClass.ProductNo1", true,
                localFuelSaleTrx.getSingleDeviceClass().
                        getProductNo1().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getProductNo1()));


        assertEquals("FdcData.DeviceClass.LockingApplicationSender", true,
                localFuelSaleTrx.getSingleDeviceClass().getLockingApplicationSender().equals(deserializedFuelSaleTrx.getSingleDeviceClass().getLockingApplicationSender()));
        assertEquals("FdcData.DeviceClass.AuthorisationApplicationSender", true,
                localFuelSaleTrx.getSingleDeviceClass().getAuthorisationApplicationSender()
                        .equals(deserializedFuelSaleTrx.getSingleDeviceClass().getAuthorisationApplicationSender()));
        assertEquals("FdcData.DeviceClass.ErrorCode", true,
                localFuelSaleTrx.getSingleDeviceClass().getErrorCode()
                        .equals(deserializedFuelSaleTrx.getSingleDeviceClass().getErrorCode()));

    }


    @Test
    public void serviceRequestAuthoriseFuelPoint_Serialize() throws Exception {
        ServiceRequestAuthoriseFuelPoint authoriseFuelPoint = new ServiceRequestAuthoriseFuelPoint();
        authoriseFuelPoint.setRequestID("01254");
        authoriseFuelPoint.setApplicationSender("POSsell1");
        authoriseFuelPoint.setWorkstationID("POS01");

        PosData posData = new PosData();
        posData.setPosTimeStamp("2009-11-20T17:30:50");
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        authoriseFuelPoint.setPosData(posDatas);
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("1");
        deviceClass.setMaxTrxAmount(50.00f);
//        FuelMode fuelMode = new FuelMode();
//        fuelMode.setModeNo(1);
//        deviceClass.setFuelMode(fuelMode);
        Product product0 = new Product();
        product0.setProductNo(1000);
        Product product1 = new Product();
        product1.setProductNo(4000);
        ArrayList<Product> products = new ArrayList<>();
        products.add(product0);
        products.add(product1);
        deviceClass.setReleasedProducts(products);
        deviceClass.setReleaseTokenElement("01");
        deviceClass.setLockFuelSaleTrx(true);
        posData.setDeviceClass(deviceClass);

        Serializer serializer = new Persister();
        final List<String> result = new ArrayList<>();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                byte[] _ = new byte[]{(byte) oneByte};
                String onechar = new String(_, StandardCharsets.UTF_8);
                result.add(onechar);
            }
        };
        try {
            serializer.write(authoriseFuelPoint, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        String finalResult = "";
        for (int i = 0; i < result.size(); i++)
            finalResult += result.get(i);

        assertEquals(true, finalResult.length() > 0);
    }

    @Test
    public void serviceRequestAuthoriseFuelPoint_Deserialize() throws Exception {
        String rawXmlString = "<ServiceRequest RequestType=\"AuthoriseFuelPoint\" ApplicationSender=\"POSsell1\" " +
                "WorkstationID=\"POS01\" RequestID=\"01254\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:noNamespaceSchemaLocation=\"FDC_AuthoriseFuelPoint_Request.xsd\">   <POSdata>   " +
                "<POSTimeStamp>2009-11-20T17:30:50</POSTimeStamp>     <DeviceClass Type=\"FP\" DeviceID=\"1\">   " +
                "<MaxTrxAmount>50.00</MaxTrxAmount>       <MaxTrxVolume></MaxTrxVolume>       <FuelMode ModeNo=\"1\"/>  " +
                "<ReleasedProducts>       <Product ProductNo=\"1000\"/>       <Product ProductNo=\"4000\"/>     </ReleasedProducts>   " +
                "<ReleaseToken>01</ReleaseToken>       <LockFuelSaleTrx>true</LockFuelSaleTrx>     </DeviceClass>   </POSdata> </ServiceRequest>";
        Serializer serializer = new Persister();
        ServiceRequestAuthoriseFuelPoint deserializedAuthoriseFuelPoint = null;
        try {
            deserializedAuthoriseFuelPoint = serializer.read(ServiceRequestAuthoriseFuelPoint.class, rawXmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        ServiceRequestAuthoriseFuelPoint localAuthoriseFuelPoint = new ServiceRequestAuthoriseFuelPoint();
        localAuthoriseFuelPoint.setRequestID("01254");
        localAuthoriseFuelPoint.setApplicationSender("POSsell1");
        localAuthoriseFuelPoint.setWorkstationID("POS01");

        PosData posData = new PosData();
        posData.setPosTimeStamp("2009-11-20T17:30:50");
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        localAuthoriseFuelPoint.setPosData(posDatas);
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("1");
        deviceClass.setMaxTrxAmount(50.00f);
//        FuelMode fuelMode = new FuelMode();
//        fuelMode.setModeNo(1);
//        deviceClass.setFuelMode(fuelMode);
        Product product0 = new Product();
        product0.setProductNo(1000);
        Product product1 = new Product();
        product1.setProductNo(4000);
        ArrayList<Product> products = new ArrayList<>();
        products.add(product0);
        products.add(product1);
        deviceClass.setReleasedProducts(products);
        deviceClass.setReleaseTokenElement("01");
        deviceClass.setLockFuelSaleTrx(true);
        posData.setDeviceClass(deviceClass);

        assertEquals("getRequestID", true, localAuthoriseFuelPoint.getRequestID().equals(deserializedAuthoriseFuelPoint.getRequestID()));
        assertEquals("getApplicationSender", true, localAuthoriseFuelPoint.getApplicationSender().equals(deserializedAuthoriseFuelPoint.getApplicationSender()));
        assertEquals("getPosData().size()", true, localAuthoriseFuelPoint.getPosData().size() == deserializedAuthoriseFuelPoint.getPosData().size());
        assertEquals("getPosTimeStamp", true, localAuthoriseFuelPoint.getSinglePosData().getPosTimeStamp()
                .equals(deserializedAuthoriseFuelPoint.getSinglePosData().getPosTimeStamp()));
        assertEquals("getPosData().DeviceClass.DeviceId", true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getDeviceId().equals(deserializedAuthoriseFuelPoint.getSingleDeviceClass().getDeviceId()));
        assertEquals("getPosData().DeviceClass.MaxTrxAmount, local: " + localAuthoriseFuelPoint.getSingleDeviceClass().getMaxTrxAmount()
                        + ", new: " + deserializedAuthoriseFuelPoint.getSingleDeviceClass().getMaxTrxAmount(), true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getMaxTrxAmount()
                        .equals(deserializedAuthoriseFuelPoint.getSingleDeviceClass().getMaxTrxAmount()));
        assertEquals("getPosData().DeviceClass.ReleasedProducts.Product0.ProductNo, local: "
                        + localAuthoriseFuelPoint.getSingleDeviceClass().getReleasedProducts().get(0).getProductNo()
                        + ", new: " + deserializedAuthoriseFuelPoint.getSingleDeviceClass().getReleasedProducts().get(0).getProductNo(), true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getReleasedProducts().get(0).getProductNo().intValue()
                        == deserializedAuthoriseFuelPoint.getSingleDeviceClass().getReleasedProducts().get(0).getProductNo().intValue());
        assertEquals("getPosData().DeviceClass.ReleasedProducts.Product1.ProductNo", true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getReleasedProducts().get(1).getProductNo().intValue()
                        == deserializedAuthoriseFuelPoint.getSingleDeviceClass().getReleasedProducts().get(1).getProductNo().intValue());
        assertEquals("getPosData().DeviceClass.ReleaseToken", true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getReleaseTokenElement().equals(
                        deserializedAuthoriseFuelPoint.getSingleDeviceClass().getReleaseTokenElement()));
        assertEquals("getPosData().DeviceClass.LockFuelSaleTrx", true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getLockFuelSaleTrx().booleanValue() ==
                        deserializedAuthoriseFuelPoint.getSingleDeviceClass().getLockFuelSaleTrx().booleanValue());

    }

    @Test
    public void serviceResponseAuthoriseFuelPoint_Deserialize() throws Exception {
        String rawXmlString = "<ServiceResponse RequestType=\"AuthoriseFuelPoint\" ApplicationSender=\"POSsell1\" " +
                "WorkstationID=\"POS01\" RequestID=\"01254\" OverallResult=\"Success\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:noNamespaceSchemaLocation=\"FDC_AuthoriseFuelPoint_Response.xsd\">   <FDCdata>  " +
                "<FDCTimeStamp>2009-11-20T17:30:50</FDCTimeStamp>     <DeviceClass Type=\"FP\" DeviceID=\"1\" >  " +
                "<ErrorCode>ERRCD_OK</ErrorCode>     </DeviceClass>   </FDCdata> </ServiceResponse> ";
        Serializer serializer = new Persister();
        ServiceResponseAuthoriseFuelPoint deserializedAuthoriseFuelPoint = null;
        try {
            deserializedAuthoriseFuelPoint = serializer.read(ServiceResponseAuthoriseFuelPoint.class, rawXmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail("got exception");
        }

        ServiceResponseAuthoriseFuelPoint localAuthoriseFuelPoint = new ServiceResponseAuthoriseFuelPoint();
        localAuthoriseFuelPoint.setRequestID("01254");
        localAuthoriseFuelPoint.setApplicationSender("POSsell1");
        localAuthoriseFuelPoint.setWorkstationID("POS01");
        localAuthoriseFuelPoint.setOverallResult("Success");

        FdcData fdcData = new FdcData();
        fdcData.setFdcTimeStamp("2009-11-20T17:30:50");
        List<FdcData> fdcDatas = new ArrayList<>();
        fdcDatas.add(fdcData);
        localAuthoriseFuelPoint.setFdcData(fdcDatas);
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId("1");
        deviceClass.setErrorCode("ERRCD_OK");
        fdcData.setSingleDeviceClass(deviceClass);

        assertEquals("getRequestID", true, localAuthoriseFuelPoint.getRequestID().equals(deserializedAuthoriseFuelPoint.getRequestID()));
        assertEquals("getApplicationSender", true, localAuthoriseFuelPoint.getApplicationSender().equals(deserializedAuthoriseFuelPoint.getApplicationSender()));
        assertEquals("getWorkstationID", true,
                localAuthoriseFuelPoint.getWorkstationID().equals(deserializedAuthoriseFuelPoint.getWorkstationID()));
        assertEquals("OverallResult", true,
                localAuthoriseFuelPoint.getOverallResult().equals(deserializedAuthoriseFuelPoint.getOverallResult()));

        assertEquals("getFdcData().size()", true, localAuthoriseFuelPoint.getFdcData().size() == deserializedAuthoriseFuelPoint.getFdcData().size());
        assertEquals("getFdcTimeStamp", true,
                localAuthoriseFuelPoint.getFdcData().get(0).getFdcTimeStamp().equals(deserializedAuthoriseFuelPoint.getFdcData().get(0).getFdcTimeStamp()));
        assertEquals("getPosData().DeviceClass.DeviceId, local: "
                        + localAuthoriseFuelPoint.getSingleDeviceClass().getDeviceId()
                        + ", new: " + deserializedAuthoriseFuelPoint.getSingleDeviceClass().getDeviceId(), true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getDeviceId().equals(
                        deserializedAuthoriseFuelPoint.getSingleDeviceClass().getDeviceId()));
        assertEquals("getPosData().DeviceClass.ErrorCode", true,
                localAuthoriseFuelPoint.getSingleDeviceClass().getErrorCode().equals(
                        deserializedAuthoriseFuelPoint.getSingleDeviceClass().getErrorCode()));

    }
}