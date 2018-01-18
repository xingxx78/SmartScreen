package com.wayne.www.waynelib.fdc;

import android.os.Handler;
import android.os.Looper;

import com.wayne.www.waynelib.Util;
import com.wayne.www.waynelib.ui.Tuple;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.wayne.www.waynelib.fdc.message.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UnknownFormatFlagsException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * Created by Think on 4/19/2016.
 */
public class FdcClient implements OnFdcMessageReceivedListener, OnFdcServiceResponseReceivedListener {
    private static String LOG_TAG = "FdcClient";
    private Logger fileLogger = LoggerFactory.getLogger(FdcClient.class);
    //connectionString:'Host=192.168.1.5,Port=4710,ClientId=101,ClientName=WDFCTest,PortB=4710,PortC=4710'
    //private byte[] remoteServerIpBytes = new byte[]{(byte) 192, (byte) 168, (byte) 199, (byte) 241};
    // for default virtual device
    //private byte[] remoteServerIpBytes = new byte[]{(byte) 10, (byte) 0, (byte) 2, (byte) 2};
    private String remoteServerIpString;
    private int remoteServerPort = 4710;

    // should unique for each POS, just a string with length <=8.
    private String defaultApplicationSender = "android1";

    // should unique for each POS, uncorrelated from POS id (like 100, 101, 102)
    private String defaultWorkstationId = "114";

    // for channel A
    private Socket clientSocket;

    private InetAddress remoteServerIpAdrs;
    private FdcClientState state = FdcClientState.Connecting;

    /*finally, I know the channel B and C were combined into A, what the fuck! */

    //heartbeat related=====================
    private Timer heartbeatTimer = new Timer();
    //every this time, will send a heartbeat actively.
    private static final int heartbeatSendInterval = 12000;

    //if during this time, still no heartbeat received from other peer, will trigger a disconnection event.
    private static final int heartbeatReceivedTimeout = 30000;
    // every 1000ms to check, the less the more accuracy, must less than
    private static final int heartbeatTimeControlAccurate = 1000;
    private int heartbeat_ElapsedCycleFromLastSend = 0;
    private int heartbeat_ElapsedCycleFromLastReceive = 0;
    //heartbeat related end======================

    private boolean shouldStopListening = false;
    // channel A only for sending service request.
    //private InputStream inputStream_channel_A;
    private OutputStream outputStream_channel_A;

    private List<OnFdcClientStateChangedListener> onFdcClientStateChangedListeners = new ArrayList<>();
    private List<OnFdcServiceResponseReceivedListener> onFdcServiceResponseReceivedListeners = new ArrayList<>();
    // unsolicited event
    private List<OnFdcMessageReceivedListener> onFdcMessageReceivedListeners = new ArrayList<>();

    /* set read() timeout to 2000ms, give a chance to quite read earlier rather than wait forever.*/
    private static final int internal_tcp_read_timedOut = 15000;
    private AtomicBoolean isStarted = new AtomicBoolean(false);


    private ReentrantLock outputStreamLock = new ReentrantLock();

    public void setDefaultApplicationSender(String value) {
        this.defaultApplicationSender = value;
    }

    public void setDefaultWorkstationId(String value) {
        this.defaultApplicationSender = value;
    }

    public String getDefaultApplicationSender() {
        return this.defaultApplicationSender;
    }

    public String getDefaultWorkstationId() {
        return this.defaultWorkstationId;
    }

    @Override
    public void onFdcMessageReceived(FdcClient sender, FdcMessage fdcMessage) {
        if (fdcMessage != null && !fdcMessage.getMessageType().equals("FDCHeartBeat")) return;
        // reset the disconnection heart timer.
        fileLogger.trace("<====Received FdcServerHeartBeat Message");
        this.heartbeat_ElapsedCycleFromLastReceive = 0;
    }

    @Override
    public void onServiceResponseReceived(FdcClient sender, ServiceResponse serviceResponse) {
        if (serviceResponse.getRequestType().equals("LogOn") && serviceResponse.getOverallResult().equalsIgnoreCase("Success")) {
            startHeartbeatTimer();
            //sendGetCountrySettingsRequestAsync();
        }
    }

    private static class SingleInstanceLoader {
        private static final FdcClient INSTANCE = new FdcClient();
    }

    public enum FdcClientState {
        // disconnected since timed out for receive other peer's heartbeat. need re-init the tcp to other peer.
        DisconnectedByHeartbeat,
        // connected to cashPos server.
        Connected,
        // disconnected just happened, and now is trying to re-connect to it.
        Connecting,
        //Fdc client fully stopped, and no connection will be made.
        Stopped,
    }

    private FdcClient() {
        this.addOnFdcMessageReceivedListeners(this);
        this.addOnFdcServiceResponseReceivedListeners(this);
    }

    /* trigger send and receive heartbeat timer.*/
    private void startHeartbeatTimer() {
        fileLogger.info("Start heartbeat Service request timer");
        heartbeat_ElapsedCycleFromLastSend = 0;
        heartbeat_ElapsedCycleFromLastReceive = 0;
        this.heartbeatTimer = new Timer();
        this.heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (heartbeat_ElapsedCycleFromLastSend * heartbeatTimeControlAccurate >= heartbeatSendInterval) {
                    boolean succeed = FdcClient.this.sendHeartbeatRequestAsync();
                    if (!succeed) {
                        fileLogger.error("Previous heartbeat request Send failed, do nothing here");
                        //stopHeartbeatTimer();
                    }

                    heartbeat_ElapsedCycleFromLastSend = 0;
                } else {
                    heartbeat_ElapsedCycleFromLastSend++;
                }

                if (heartbeat_ElapsedCycleFromLastReceive * heartbeatTimeControlAccurate >= heartbeatReceivedTimeout) {
                    fileLogger.error("Remote server Heartbeat have not been received, and exceed defined max time, will trigger disconnection");
                    stopHeartbeatTimer();
                    changeFdcClientStateAndNotifyAllListeners(FdcClientState.DisconnectedByHeartbeat);
                    heartbeat_ElapsedCycleFromLastReceive = 0;
                    safeCloseSocketResource();
                } else {
                    heartbeat_ElapsedCycleFromLastReceive++;
                }
            }
            // set a fixed interval here is for avoid create 2 timers for send heartbeat and count for disconnection timer.
        }, 0, this.heartbeatTimeControlAccurate);
    }

    private void stopHeartbeatTimer() {
        fileLogger.info("Stop heartbeat Service request timer");
        this.heartbeatTimer.cancel();
        this.heartbeatTimer.purge();
    }

    /*
    * get default singleton instance of PumpControl
    * */
    public static FdcClient getDefault() {
        return SingleInstanceLoader.INSTANCE;
    }

    private void safeCloseSocketResource() {
        fileLogger.error("Safe close all resources");
        stopHeartbeatTimer();
        if (this.outputStream_channel_A != null)
            try {
                this.outputStream_channel_A.close();
            } catch (IOException e) {
            }
        if (this.clientSocket != null)
            try {
                this.clientSocket.close();
            } catch (Exception e) {
            }
    }

    /*
    * Start the connection to remote FC (fdc server) with IP specified.
    * @param remoteServerIp, like: "10.0.2.2";
    * */
    public void start(String remoteServerIp, int remoteServerPort, String applicationSender, String workstationId) {
        if (!this.isStarted.compareAndSet(false, true)) {
            fileLogger.error("Invalid call for start() since FdcClient still on running...");
            throw new IllegalStateException("Stop the FdcClient first, then start()");
        }

        String ipPatternStr = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        // null indicates it's a restart() call.
        if (remoteServerIp != null) {
            if (!Pattern.matches(ipPatternStr, remoteServerIp)) {
                throw new IllegalArgumentException("remoteServerIp not a IP address: " + remoteServerIp);
            }

            String[] stringBytes = remoteServerIp.split("\\.");
//            this.remoteServerIpBytes = new byte[]{
//                    Byte.parseByte(stringBytes[0]) & 0xFF,
//                    Byte.parseByte(stringBytes[1]),
//                    Byte.parseByte(stringBytes[2]),
//                    Byte.parseByte(stringBytes[3])};
            this.remoteServerIpString = remoteServerIp;
            this.remoteServerPort = remoteServerPort;
            this.defaultApplicationSender = applicationSender;
            this.defaultWorkstationId = workstationId;
            try {
                this.remoteServerIpAdrs = InetAddress.getByName(remoteServerIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("remoteServerIp got UnknownHostException");
            }
        }

        fileLogger.info("Start FdcClient (" + this.remoteServerIpAdrs.getHostAddress() + ":" + Integer.toString(remoteServerPort) + ", workstationId: " + defaultWorkstationId + ", applicationSender: " + defaultApplicationSender + ")");
        this.shouldStopListening = false;
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                // connecting socket to remote host.
                while (!shouldStopListening) {
                    try {
                        //==============init channel A
                        FdcClient.this.clientSocket = new Socket();
                        changeFdcClientStateAndNotifyAllListeners(FdcClientState.Connecting);
                        fileLogger.debug("Initializing Channel A socket to "
                                + FdcClient.this.remoteServerIpString + ", port " + FdcClient.this.remoteServerPort);
                        ;
                        FdcClient.this.clientSocket.setSoTimeout(FdcClient.internal_tcp_read_timedOut);
                        FdcClient.this.clientSocket.connect(new InetSocketAddress(FdcClient.this.remoteServerIpAdrs, FdcClient.this.remoteServerPort), FdcClient.internal_tcp_read_timedOut);
                        fileLogger.debug("Initialized Channel A socket");
                        FdcClient.this.outputStream_channel_A = FdcClient.this.clientSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileLogger.error("Got an IOException when connecting (via channel A) to Fdc Server, will retry later...");
                        safeCloseSocketResource();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                        }

                        continue;
                    }

                    changeFdcClientStateAndNotifyAllListeners(FdcClientState.Connected);

                    byte[] msgLenBytes = new byte[4];
                    while (!shouldStopListening && state != FdcClientState.DisconnectedByHeartbeat) {
                        try {
                            int previousTotalReadCount = 0;
                            do {
                                try {
                                    fileLogger.trace("TCP, Channel A, keep listening...");
                                    int currentReadCount =
                                            FdcClient.this.clientSocket.getInputStream().read(msgLenBytes,
                                                    previousTotalReadCount,
                                                    msgLenBytes.length - previousTotalReadCount);
                                    // detect if need re-connect.
                                    if (currentReadCount < 0) {
                                        break;
                                        //throw new IOException("read negative value when reading MessageLenBytes");
                                    }
                                    previousTotalReadCount += currentReadCount;
                                } catch (java.net.SocketTimeoutException te) {
                                    if (!shouldStopListening && state != FdcClientState.DisconnectedByHeartbeat) {
                                        // it's OK to see timed out for this read().
                                        // nothing will break, let's continue to keep reading.
                                        continue;
                                    } else {
                                        throw new InterruptedException("cancel reading message (header) loop.");
                                    }
                                }
                            } while (previousTotalReadCount < msgLenBytes.length);

                            int len = Util.byteArray4ToInt(msgLenBytes);
                            fileLogger.trace("TCP, Channel A, read 4 bytes MsgLen: " + len);
                            //999999 is an experience value
                            if (len <= 0 || len > 999999) {
                                fileLogger.error("TCP, Channel A, Ignore since len is <=0 or too big, unlikely a valid msg.");
                                throw new InterruptedException("Msg Len abnormal exception.");
                            }

                            //start reading msg body.
                            fileLogger.trace("TCP, Channel A, waiting read msg body (with " + internal_tcp_read_timedOut + "ms timeout)...");
                            // 32 hashed bytes.
                            final int extraBytesCount = 16;
                            len += extraBytesCount;
                            byte[] msgBodyBytes = new byte[len];
                            previousTotalReadCount = 0;
                            do {
                                try {
                                    fileLogger.trace("        reading...");
                                    int currentReadCount = FdcClient.this.clientSocket.getInputStream().read(msgBodyBytes,
                                            previousTotalReadCount, msgBodyBytes.length - previousTotalReadCount);
                                    // detect if need re-connect.
                                    if (currentReadCount < 0) {
                                        break;
                                        //throw new IOException("Channel A, read negative value when reading MessageBodyBytes");
                                    }
                                    previousTotalReadCount += currentReadCount;
                                } catch (SocketTimeoutException ste) {
                                            /* a timed out occurred in reading msgbody */
                                    safeCloseSocketResource();
                                    fileLogger.error("TCP, Channel A, waiting for read msg body timed out, will re-init the TCP connection");
                                    throw new TimeoutException("Channel A, read msg body timed out and exceed max retry times");
                                }
                            } while (previousTotalReadCount < msgBodyBytes.length);

                            byte[] xmlBody_extraBytesExcluded = new byte[msgBodyBytes.length - extraBytesCount];
                            for (int i = 0; i < xmlBody_extraBytesExcluded.length; i++) {
                                xmlBody_extraBytesExcluded[i] = msgBodyBytes[extraBytesCount + i];
                            }

                            // start parsing Xml data.
                            String xmlData = new String(xmlBody_extraBytesExcluded, "UTF-8");
                            // exclude the heartbeat msg logging here, too boring.
                            if (!xmlData.contains("MessageType=\"FDCHeartBeat\"")) {
                                fileLogger.debug("<====TCP, read string data(hashed bytes for validation has been excluded): ");

                                //there is a fixed size buffer in logcat for logs,
                                //spit the xml data, otherwise it can not be shown on Android monitor
                                for (String line : xmlData.split("\r\n")) {
                                    fileLogger.debug("    " + line);
                                }
                            }

                            try {
                                if (xmlData.contains("<ServiceResponse ")) {
                                    for (int i = 0; i < onFdcServiceResponseReceivedListeners.size(); i++)
                                        if (onFdcServiceResponseReceivedListeners != null && onFdcServiceResponseReceivedListeners.size() > 0)
                                            onFdcServiceResponseReceivedListeners.get(i)
                                                    .onServiceResponseReceived(FdcClient.this, FdcClient.this.deserializeFdcServiceResponse(xmlData));
                                } else if (xmlData.contains("<FDCMessage ")) {
                                    for (int i = 0; i < onFdcMessageReceivedListeners.size(); i++)
                                        if (onFdcMessageReceivedListeners != null && onFdcMessageReceivedListeners.size() > 0)
                                            onFdcMessageReceivedListeners.get(i)
                                                    .onFdcMessageReceived(FdcClient.this, FdcClient.this.deserializeFdcMessage(xmlData));
                                }
                            } catch (Exception _) {
                                _.printStackTrace();
                                fileLogger.error("Ignore the exception");
                            }
                        } catch (IOException ee) {
                            ee.printStackTrace();
                            fileLogger.error("TCP, channel A read bytes error, will re-init the whole Socket");
                            safeCloseSocketResource();
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            fileLogger.error("TCP, channel A got InterruptedException, will re-init the whole Socket");
                            safeCloseSocketResource();
                            break;
                        } catch (Exception eee) {
                            eee.printStackTrace();
                            fileLogger.error("TCP, channel A got generic Exception, will re-init the whole Socket");
                            safeCloseSocketResource();
                            break;
                        }
                    }
                }

                isStarted.set(false);
                fileLogger.warn("FDC client quit listening");
            }
        }
        );
        worker.start();
    }

    /*stop and start with previous argument. */
    public void restart() {
        stop();
        start(null, -1, null, null);
    }

    /* may block caller since need wait background worker to quit, if it is called via UI thread, use stopAsync(...) instead */
    public void stop() {
        fileLogger.warn("Stopping the FdcClient");
        this.shouldStopListening = true;
        safeCloseSocketResource();
        // wait the worker thread to fully quit TCP listening.
        while (isStarted.get()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        fileLogger.warn("Stopped the FdcClient");
    }

    public void stopAsync(final OnFdcClientStateChangedListener callback) {
        fileLogger.warn("Stopping the FdcClient with Async");
        this.shouldStopListening = true;
        safeCloseSocketResource();
        Thread _ = new Thread(new Runnable() {
            @Override
            public void run() {
                // wait the worker thread to fully quit TCP listening.
                while (isStarted.get()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                fileLogger.warn("Stopped the FdcClient with Async");
                if (callback != null)
                    callback.onFdcClientStateChanged(FdcClient.this, FdcClientState.Stopped);
            }
        });
        _.start();
    }

    public void addOnFdcClientStateChangedListeners(OnFdcClientStateChangedListener listener) {
        this.onFdcClientStateChangedListeners.add(listener);
    }

    public boolean removeOnFdcClientStateChangedListeners(OnFdcClientStateChangedListener listener) {
        return this.onFdcClientStateChangedListeners.remove(listener);
    }

    public void addOnFdcServiceResponseReceivedListeners(OnFdcServiceResponseReceivedListener listener) {
        this.onFdcServiceResponseReceivedListeners.add(listener);
    }

    public boolean removeOnFdcServiceResponseReceivedListeners(OnFdcServiceResponseReceivedListener listener) {
        return this.onFdcServiceResponseReceivedListeners.remove(listener);
    }

    public void addOnFdcMessageReceivedListeners(OnFdcMessageReceivedListener listener) {
        this.onFdcMessageReceivedListeners.add(listener);
    }

    public boolean removeOnFdcMessageReceivedListeners(OnFdcMessageReceivedListener listener) {
        return this.onFdcMessageReceivedListeners.remove(listener);
    }

    private void changeFdcClientStateAndNotifyAllListeners(FdcClientState newState) {
        // only notify when state switched.
        if (this.state == newState) return;
        fileLogger.info("State changed from '" + this.state + "' to '" + newState + "'");
        this.state = newState;
        for (int i = 0; i < this.onFdcClientStateChangedListeners.size(); i++) {
            this.onFdcClientStateChangedListeners.get(i).onFdcClientStateChanged(this, this.state);
        }
    }


    private MD5Crypter md5Crypter = new MD5Crypter();

    /* the bytes send on TCP is constructed with: [4 bytes RequestMsgLen] + [HashedBytes] + [RequestMsgBodyBytes],
        * while the [HashedBytes] is the hash value for [RequestMsgBodyBytes + KeyBytes], total 16 bytes.
        * */
    private byte[] serializeFdcServiceRequest(ServiceRequest request) throws Exception {
        Serializer serializer = new Persister();
        final List<Byte> result = new ArrayList<>();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
//                byte[] _ = new byte[]{(byte) oneByte};
//                String onechar = new String(_, StandardCharsets.UTF_8);
                result.add((byte) oneByte);
            }
        };
        serializer.write(request, os);
        byte[] _requestBodyBytes = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            _requestBodyBytes[i] = result.get(i);
        }

        byte[] xmlHeader = "<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8);
        byte[] requestBodyBytes = Util.concat(xmlHeader, _requestBodyBytes);
        String reverseGenerateRequestBodyString = new String(requestBodyBytes, StandardCharsets.UTF_8);
        if (reverseGenerateRequestBodyString.contains("RequestType=\"POSHeartBeat\""))
            fileLogger.trace("====>Prepare sending POSHeartBeat ServiceRequest");
        else
            fileLogger.debug("====>Prepare sending ServiceRequest: \n" + reverseGenerateRequestBodyString);
        byte[] requestMsgLen = Util.intToByteArray4Reverse(requestBodyBytes.length);
        //Log.v(LOG_TAG, "requestMsgLen: " + Util.bytesToHexString(requestMsgLen));

        //Log.v(LOG_TAG, "md5Crypter.getPassphrase(): " + Util.bytesToHexString(md5Crypter.getPassphrase()));

        byte[] pendingForHashing = Util.concat(requestBodyBytes, md5Crypter.getPassphrase());
        //Log.v(LOG_TAG, "pendingForHashing: " + Util.bytesToHexString(pendingForHashing));

        byte[] hashed = md5Crypter.computeHash(pendingForHashing);
        //Log.v(LOG_TAG, "hashed: " + Util.bytesToHexString(hashed));

        byte[] finalBytes = Util.concat(Util.concat(requestMsgLen, hashed), requestBodyBytes);
        //Log.v(LOG_TAG, "ServiceRequest: " + request.getRequestType() + " fully converted into bytes: " + Util.bytesToHexString(finalBytes));
        return finalBytes;
    }

    private void safeWrite(byte[] data) throws IOException {
        this.outputStreamLock.lock();
        try {
            if (this.outputStream_channel_A != null)
                this.outputStream_channel_A.write(data);
        } finally {
            this.outputStreamLock.unlock();
        }
    }

    private ServiceResponse deserializeFdcServiceResponse(String xmlData) {
        Serializer serializer = new Persister();
        ServiceResponse result = null;
        try {
            if (xmlData.contains("RequestType=\"LogOn\"")) {
                result = serializer.read(ServiceResponseLogOn.class, xmlData);
            } else if (xmlData.contains("RequestType=\"LogOff\"")) {
                result = serializer.read(ServiceResponseLogOff.class, xmlData);
            } else if (xmlData.contains("RequestType=\"GetCountrySettings\"")) {
                result = serializer.read(ServiceResponseGetCountrySettings.class, xmlData);
            } else if (xmlData.contains("RequestType=\"GetProductTable\"")) {
                result = serializer.read(ServiceResponseGetProductTable.class, xmlData);
            } else if (xmlData.contains("RequestType=\"GetDSPConfiguration\"")) {
                result = serializer.read(ServiceResponseGetDspConfiguration.class, xmlData);
            } else if (xmlData.contains("RequestType=\"ReserveFuelPoint\"")) {
                result = serializer.read(ServiceResponseReserveFuelPoint.class, xmlData);
            } else if (xmlData.contains("RequestType=\"FreeFuelPoint\"")) {
                result = serializer.read(ServiceResponseFreeFuelPoint.class, xmlData);
            } else if (xmlData.contains("RequestType=\"ClearFuelSaleTrx\"")) {
                result = serializer.read(ServiceResponseClearFuelSaleTrx.class, xmlData);
            } else if (xmlData.contains("RequestType=\"AuthoriseFuelPoint\"")) {
                result = serializer.read(ServiceResponseAuthoriseFuelPoint.class, xmlData);
            } else if (xmlData.contains("RequestType=\"GetAvailableFuelSaleTrxs\"")) {
                result = serializer.read(ServiceResponseGetAvailableFuelSaleTrxs.class, xmlData);
            } else if (xmlData.contains("RequestType=\"ClearFuelSaleTrx\"")) {
                result = serializer.read(ServiceResponseClearFuelSaleTrx.class, xmlData);
            } else if (xmlData.contains("RequestType=\"GetFuelSaleTrxDetails\"")) {
                result = serializer.read(ServiceResponseGetFuelSalesTrxDetails.class, xmlData);
            } else if (xmlData.contains("RequestType=\"LockFuelSaleTrx\"")) {
                result = serializer.read(ServiceResponseLockFuelSaleTrx.class, xmlData);
            } else if (xmlData.contains("RequestType=\"GetFPState\"")) {
                result = serializer.read(ServiceResponseGetFPState.class, xmlData);
            } else if (xmlData.contains("RequestType=\"UnlockFuelSaleTrx\"")) {
                result = serializer.read(ServiceResponseUnlockFuelSaleTrx.class, xmlData);
            } else if (xmlData.contains("RequestType=\"ChangeFuelPrice\"")) {
                result = serializer.read(ServiceResponseChangeFuelPrice.class, xmlData);
            }
        } catch (Exception ee) {
            fileLogger.error(LOG_TAG, "deserializeFdcServiceResponse got exception");
            ee.printStackTrace();
        }

        return result;
    }

    private FdcMessage deserializeFdcMessage(String xmlData) {
        Serializer serializer = new Persister();
        FdcMessage result = null;
        try {
            if (xmlData.contains("MessageType=\"FDCHeartBeat\"")) {
                result = serializer.read(FdcMessageHeartbeat.class, xmlData);
            } else if (xmlData.contains("MessageType=\"FDCStarted\"")) {
                result = serializer.read(FdcMessageFdcStarted.class, xmlData);
            } else if (xmlData.contains("MessageType=\"FPStateChange\"")) {
                result = serializer.read(FdcMessageFPStateChange.class, xmlData);
            } else if (xmlData.contains("MessageType=\"FuelSaleTrx\"")) {
                result = serializer.read(FdcMessageFuelSaleTrx.class, xmlData);
            } else if (xmlData.contains("MessageType=\"FuelModeChange\"")) {
                result = serializer.read(FdcMessageFuelModeChange.class, xmlData);
            } else if (xmlData.contains("MessageType=\"CurrentFuellingStatus\"")) {
                result = serializer.read(FdcMessageFuelPointCurrentFuellingStatus.class, xmlData);
            } else if (xmlData.contains("MessageType=\"FuelPriceChange\"")) {
                result = serializer.read(FdcMessageFuelPriceChange.class, xmlData);
            } else {
                throw new UnknownFormatFlagsException("Unknown type of FdcMessage");
            }
        } catch (Exception ee) {
            fileLogger.error("deserializeFdcMessage got exception");
            ee.printStackTrace();
        }

        return result;
    }

    private boolean sendHeartbeatRequestAsync() {
        ServiceRequestHeartbeat requestHeartbeat = new ServiceRequestHeartbeat();
        requestHeartbeat.setApplicationSender(this.defaultApplicationSender);
        requestHeartbeat.setWorkstationID(this.defaultWorkstationId);
        requestHeartbeat.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        List<PosData> posDatas = new ArrayList<>();
        posDatas.add(posData);
        requestHeartbeat.setPosData(posDatas);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestHeartbeat);
            this.safeWrite(fullBytes);
            fileLogger.trace("sendHeartbeatRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* make the service request async call support inline response call back */
    private void buildInlineServiceResponseCallback(final String serviceRequestId, final OnFdcServiceResponseReceivedListener callback, final int timeout) {
        if (callback == null || timeout == 0)
            return;
        fileLogger.debug("Queue request with id: " + serviceRequestId + " with timedout: " + timeout + "ms");
        final AtomicBoolean callbackCalled = new AtomicBoolean(false);
        OnFdcServiceResponseReceivedListener onetimeListener = null;
        // avoid final effect
        final Tuple<OnFdcServiceResponseReceivedListener, Integer> _ = new Tuple<>(onetimeListener, 1);

        // make it on UI thread.
        final Handler timeoutHandler = new Handler(Looper.getMainLooper());
        final Runnable timeoutHandlerRunable = new Runnable() {
            @Override
            public void run() {
                if (false == callbackCalled.compareAndSet(false, true)) return;
                fileLogger.error("Response timed out for request id: " + serviceRequestId + " (timeout set to: " + timeout + "ms)");
                FdcClient.this.removeOnFdcServiceResponseReceivedListeners(_.first);
                // 2nd parameter null indicate timedout.
                callback.onServiceResponseReceived(FdcClient.this, null);
            }
        };
        onetimeListener = new OnFdcServiceResponseReceivedListener() {
            @Override
            public void onServiceResponseReceived(final FdcClient sender, final ServiceResponse serviceResponse) {
                if (Util.isRequestIdEqual(serviceResponse.getRequestID(), serviceRequestId)) {
                    if (false == callbackCalled.compareAndSet(false, true)) return;
                    timeoutHandler.removeCallbacks(timeoutHandlerRunable);
                    FdcClient.this.removeOnFdcServiceResponseReceivedListeners(_.first);
                    // make it on UI thread.
                    final Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onServiceResponseReceived(sender, serviceResponse);
                        }
                    });
                }
            }
        };
        this.addOnFdcServiceResponseReceivedListeners(onetimeListener);
        timeoutHandler.postDelayed(timeoutHandlerRunable, timeout);
    }

    public boolean sendGetCountrySettingsRequestAsync() {
        ServiceRequestGetCountrySettings requestGetCountrySettings = new ServiceRequestGetCountrySettings();
        requestGetCountrySettings.setApplicationSender(this.defaultApplicationSender);
        requestGetCountrySettings.setWorkstationID(this.defaultWorkstationId);
        requestGetCountrySettings.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        requestGetCountrySettings.setSinglePosData(posData);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestGetCountrySettings);
            this.safeWrite(fullBytes);
            fileLogger.info("sendGetCountrySettingsRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //interfaceversion 00.07
    public boolean sendLogonRequestAsync(int responsePort, int unsolicitedPort, String interfaceVersion, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestLogOnV07 requestLogOn = new ServiceRequestLogOnV07();
        requestLogOn.setApplicationSender(this.defaultApplicationSender);
        requestLogOn.setWorkstationID(this.defaultWorkstationId);
        requestLogOn.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosName("");
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        posData.setResponsePort(responsePort);
        posData.setUnsolicitedPort(unsolicitedPort);
        posData.setInterfaceVersion(interfaceVersion);
        requestLogOn.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestLogOn.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestLogOn);
            this.safeWrite(fullBytes);
            fileLogger.info("sendLogonRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendChangeFuelPriceAsync(String productNo, String newPrice, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestChangeFuelPrice requestChangeFuelPrice = new ServiceRequestChangeFuelPrice();
        requestChangeFuelPrice.setApplicationSender(this.defaultApplicationSender);
        requestChangeFuelPrice.setWorkstationID(this.defaultWorkstationId);
        requestChangeFuelPrice.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        ChangeFuelPriceProduct priceChangeProduct = new ChangeFuelPriceProduct();
        priceChangeProduct.setProductNoInAttribute(productNo);
        FuelMode fuelMode = new FuelMode();
        fuelMode.setModeNo(1);
        fuelMode.setPriceNewForSendingToFdcServer(newPrice);
        //fuelMode.setEffectiveDateTime("2016");
        priceChangeProduct.setModeNo(fuelMode);

        posData.setFuelPrice(priceChangeProduct);
        requestChangeFuelPrice.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestChangeFuelPrice.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestChangeFuelPrice);
            this.safeWrite(fullBytes);
            fileLogger.info("sendChangeFuelPriceAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * send log off will trigger FDC server to close the tcp channel and there's no more heart beat response from server anymore.
     *
     * @param callback
     * @param timeout
     * @return
     */
    //interfaceversion 00.07
    public boolean sendLogOffRequestAsync(final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestLogOffV07 requestLogOff = new ServiceRequestLogOffV07();
        requestLogOff.setApplicationSender(this.defaultApplicationSender);
        requestLogOff.setWorkstationID(this.defaultWorkstationId);
        requestLogOff.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        requestLogOff.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestLogOff.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestLogOff);
            this.safeWrite(fullBytes);
            fileLogger.info("sendLogOffRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param deviceId -1 indicates query all available devices state from fdc server.
     * @param callback
     * @param timeout
     * @return
     */
    public boolean sendGetDeviceStateAsync(int deviceId, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestGetDeviceState getDeviceStateRequest = new ServiceRequestGetDeviceState();
        getDeviceStateRequest.setApplicationSender(this.defaultApplicationSender);
        getDeviceStateRequest.setWorkstationID(this.defaultWorkstationId);
        getDeviceStateRequest.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId((deviceId == -1) ? "*" : Integer.toString(deviceId));
        posData.setDeviceClass(deviceClass);
        getDeviceStateRequest.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(getDeviceStateRequest.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(getDeviceStateRequest);
            this.safeWrite(fullBytes);
            fileLogger.info("sendGetDeviceStateAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param deviceId -1 means query all available pumps
     * @param callback
     * @param timeout
     * @return
     */
    public boolean sendGetAvailableFuelSaleTrxs(int deviceId, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestGetAvailableFuelSaleTrxs getAvailableFuelSaleTrxs = new ServiceRequestGetAvailableFuelSaleTrxs();
        getAvailableFuelSaleTrxs.setApplicationSender(this.defaultApplicationSender);
        getAvailableFuelSaleTrxs.setWorkstationID(this.defaultWorkstationId);
        getAvailableFuelSaleTrxs.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId((deviceId == -1) ? "*" : Integer.toString(deviceId));
        posData.setDeviceClass(deviceClass);
        getAvailableFuelSaleTrxs.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(getAvailableFuelSaleTrxs.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(getAvailableFuelSaleTrxs);
            this.safeWrite(fullBytes);
            fileLogger.info("sendGetAvailableFuelSaleTrxs successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendGetProductTableRequestAsync() {
        ServiceRequestGetProductTable requestGetProductTable = new ServiceRequestGetProductTable();
        requestGetProductTable.setApplicationSender(this.defaultApplicationSender);
        requestGetProductTable.setWorkstationID(this.defaultWorkstationId);
        requestGetProductTable.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        requestGetProductTable.setSinglePosData(posData);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestGetProductTable);
            this.safeWrite(fullBytes);
            fileLogger.info("sendGetProductTableRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendGetDspConfigurationRequestAsync(final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestGetDspConfiguration requestGetDspConfiguration = new ServiceRequestGetDspConfiguration();
        requestGetDspConfiguration.setApplicationSender(this.defaultApplicationSender);
        requestGetDspConfiguration.setWorkstationID(this.defaultWorkstationId);
        requestGetDspConfiguration.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        requestGetDspConfiguration.setSinglePosData(posData);
        this.buildInlineServiceResponseCallback(requestGetDspConfiguration.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestGetDspConfiguration);
            this.safeWrite(fullBytes);
            fileLogger.info("sendGetDspConfigurationRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param authorizingDeviceId the id of the device which will be authorized, the pump id.
     * @param productNumbers      which products need to be authorised for fuelling.
     * @param maxTrxAmount
     * @param maxTrxVolume
     * @param callback
     * @param timeout
     * @return
     */
    public boolean sendAuthoriseFuelPointRequestAsync(int authorizingDeviceId, List<Integer> productNumbers, float maxTrxAmount, float maxTrxVolume, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestAuthoriseFuelPoint requestAuthoriseFuelPoint = new ServiceRequestAuthoriseFuelPoint();
        requestAuthoriseFuelPoint.setApplicationSender(this.defaultApplicationSender);
        requestAuthoriseFuelPoint.setWorkstationID(this.defaultWorkstationId);
        requestAuthoriseFuelPoint.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(authorizingDeviceId));
        if (maxTrxAmount != 0) {
            deviceClass.setMaxTrxAmount(maxTrxAmount);
            deviceClass.setMaxTrxVolume(0f);
        } else {
            deviceClass.setMaxTrxVolume(maxTrxVolume);
            deviceClass.setMaxTrxAmount(0f);
        }

        deviceClass.setPayType("PC");

        ArrayList<Product> releaseProducts = new ArrayList<>();
        for (int i = 0; i < productNumbers.size(); i++) {
            Product p = new Product();
            p.setProductNoInAttribute(productNumbers.get(i));
            releaseProducts.add(p);
        }

//        Product product1 = new Product();
//        product1.setProductNoInAttribute(3);
//
//        releaseProducts.add(product1);
        deviceClass.setReleasedProducts(releaseProducts);

//        FuelMode fuelMode = new FuelMode();
//        fuelMode.setModeNo(1);
//
//        deviceClass.setFuelMode(fuelMode);

        deviceClass.setReleaseTokenElement(Integer.toString(authorizingDeviceId));
        // allow other fdc client can pay this trx.
        deviceClass.setLockFuelSaleTrx(false);

        deviceClass.setReservingDeviceId(this.defaultWorkstationId);
        //Unknown = 0, FullService = 1, Postpaid = 2, Prepaid = 3, OptCardPaid = 4, OptCashPaid = 5, DetectedFromAccumulators = 6, TestFuelling = 7
        deviceClass.setFuellingType("2");
        posData.setDeviceClass(deviceClass);
        requestAuthoriseFuelPoint.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestAuthoriseFuelPoint.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestAuthoriseFuelPoint);
            this.safeWrite(fullBytes);
            fileLogger.info("sendAuthoriseFuelPointRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean sendReserveFuelPointRequestAsync(int deviceId, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        final ServiceRequestReserveFuelPoint requestReserveFuelPoint = new ServiceRequestReserveFuelPoint();
        requestReserveFuelPoint.setApplicationSender(this.defaultApplicationSender);
        requestReserveFuelPoint.setWorkstationID(this.defaultWorkstationId);
        requestReserveFuelPoint.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(deviceId));
        posData.setDeviceClass(deviceClass);
        requestReserveFuelPoint.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestReserveFuelPoint.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestReserveFuelPoint);
            this.safeWrite(fullBytes);
            fileLogger.info("sendReserveFuelPointRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendFreeFuelPointRequestAsync(int deviceId) {
        ServiceRequestFreeFuelPoint requestFreeFuelPoint = new ServiceRequestFreeFuelPoint();
        requestFreeFuelPoint.setApplicationSender(this.defaultApplicationSender);
        requestFreeFuelPoint.setWorkstationID(this.defaultWorkstationId);
        requestFreeFuelPoint.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(deviceId));
        posData.setDeviceClass(deviceClass);
        requestFreeFuelPoint.setSinglePosData(posData);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestFreeFuelPoint);
            this.safeWrite(fullBytes);
            fileLogger.info("sendFreeFuelPointRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendClearFuelSaleTrxRequestAsync(int deviceId, String transactionSeqNo, int releaseTokenAttribute, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestClearFuelSaleTrx requestClearFuelSaleTrx = new ServiceRequestClearFuelSaleTrx();
        requestClearFuelSaleTrx.setApplicationSender(this.defaultApplicationSender);
        requestClearFuelSaleTrx.setWorkstationID(this.defaultWorkstationId);
        requestClearFuelSaleTrx.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(deviceId));
        deviceClass.setTransactionSeqNo(transactionSeqNo);
        deviceClass.setReleaseTokenAttribute(Integer.toString(releaseTokenAttribute));
        posData.setDeviceClass(deviceClass);
        requestClearFuelSaleTrx.setSinglePosData(posData);
        this.buildInlineServiceResponseCallback(requestClearFuelSaleTrx.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestClearFuelSaleTrx);
            this.safeWrite(fullBytes);
            fileLogger.info("sendClearFuelSaleTrxRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*get the detail info for a trx.
    * @param transactionSeqNo wildchar * is acceptable by standard doc, means query all trx detail on this device, include Payable and other state trx.   but failed on EMSG FCC, liteFCC is good.
    * */
    public boolean sendGetFuelSalesTrxDetailsRequestAsync(int deviceId, String transactionSeqNo, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        ServiceRequestGetFuelSalesTrxDetails requestGetFuelSalesTrxDetails = new ServiceRequestGetFuelSalesTrxDetails();
        requestGetFuelSalesTrxDetails.setApplicationSender(this.defaultApplicationSender);
        requestGetFuelSalesTrxDetails.setWorkstationID(this.defaultWorkstationId);
        requestGetFuelSalesTrxDetails.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(deviceId));
        deviceClass.setTransactionSeqNo(transactionSeqNo);
        deviceClass.setReleaseTokenAttribute("12345");
        posData.setDeviceClass(deviceClass);
        requestGetFuelSalesTrxDetails.setSinglePosData(posData);
        this.buildInlineServiceResponseCallback(requestGetFuelSalesTrxDetails.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestGetFuelSalesTrxDetails);
            this.safeWrite(fullBytes);
            fileLogger.info("sendGetFuelSalesTrxDetailsRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendLockFuelSaleTrxRequestAsync(int deviceId, String transactionSeqNo, int releaseTokenAttribute, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        final ServiceRequestLockFuelSaleTrx requestLockFuelSaleTrx = new ServiceRequestLockFuelSaleTrx();
        requestLockFuelSaleTrx.setApplicationSender(this.defaultApplicationSender);
        requestLockFuelSaleTrx.setWorkstationID(this.defaultWorkstationId);
        requestLockFuelSaleTrx.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(deviceId));
        deviceClass.setTransactionSeqNo(transactionSeqNo);
        deviceClass.setReleaseTokenAttribute(Integer.toString(releaseTokenAttribute));
        posData.setDeviceClass(deviceClass);
        requestLockFuelSaleTrx.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestLockFuelSaleTrx.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestLockFuelSaleTrx);
            this.safeWrite(fullBytes);
            fileLogger.info("sendLockFuelSaleTrxRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendUnLockFuelSaleTrxRequestAsync(int deviceId, String transactionSeqNo, int releaseTokenAttribute, final OnFdcServiceResponseReceivedListener callback, int timeout) {
        final ServiceRequestUnlockFuelSaleTrx requestUnlockFuelSaleTrx = new ServiceRequestUnlockFuelSaleTrx();
        requestUnlockFuelSaleTrx.setApplicationSender(this.defaultApplicationSender);
        requestUnlockFuelSaleTrx.setWorkstationID(this.defaultWorkstationId);
        requestUnlockFuelSaleTrx.setRequestID(Util.pad(Integer.toString(IdDistributer.getAndConsumeId()), "left", 8, '0'));
        PosData posData = new PosData();
        posData.setPosTimeStamp(Util.getNowTimeWithXmlFormat());
        DeviceClass deviceClass = new DeviceClass();
        deviceClass.setDeviceId(Integer.toString(deviceId));
        deviceClass.setTransactionSeqNo(transactionSeqNo);
        deviceClass.setReleaseTokenAttribute(Integer.toString(releaseTokenAttribute));
        posData.setDeviceClass(deviceClass);
        requestUnlockFuelSaleTrx.setSinglePosData(posData);

        this.buildInlineServiceResponseCallback(requestUnlockFuelSaleTrx.getRequestID(), callback, timeout);
        try {
            byte[] fullBytes = serializeFdcServiceRequest(requestUnlockFuelSaleTrx);
            this.safeWrite(fullBytes);
            fileLogger.info("sendUnLockFuelSaleTrxRequestAsync successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
