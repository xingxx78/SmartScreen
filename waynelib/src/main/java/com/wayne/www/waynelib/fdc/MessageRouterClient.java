package com.wayne.www.waynelib.fdc;

import android.nfc.FormatException;
import android.os.Message;

import com.wayne.www.waynelib.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * Created by Shawn on 10/26/2017.
 */

public class MessageRouterClient {
    public enum MessageRouterClientState {
        // disconnected since timed out for receive other peer's heartbeat. need re-init the tcp to other peer.
        DisconnectedByHeartbeat,
        // connected to cashPos server.
        Connected,
        // disconnected just happened, and now is trying to re-connect to it.
        Connecting,
        //Fdc client fully stopped, and no connection will be made.
        Stopped,
    }

    private static String LOG_TAG = "MessageRouterClient";
    private Logger fileLogger = LoggerFactory.getLogger(MessageRouterClient.class);
    //private char cryptFormat;
    //heartbeat related=====================
    private Timer heartbeatTimer = new Timer();
    //every this time, will send a heartbeat actively.
    private static final int heartbeatSendInterval = 150 * 1000;
    // every 1000ms to check, the less the more accuracy, must less than
    //private static final int heartbeatTimeControlAccurate = 1000;
    public static int MSG_ROUTER_MSG_LEN_LENGTH = 5;
    private AtomicBoolean isStarted = new AtomicBoolean(false);
    private OutputStream outputStream;
    private boolean shouldStopListening = false;
    private MessageRouterClientState state = MessageRouterClientState.Connecting;
    private List<OnMsgRouterClientStateChangedListener> onMsgRouterClientStateChangedListeners = new ArrayList<>();
    //private List<OnFdcServiceResponseReceivedListener> onFdcServiceResponseReceivedListeners = new ArrayList<>();
    // unsolicited event
    private List<OnMsgRouterMessageReceivedListener> onMsgRouterMessageReceivedListeners = new ArrayList<>();

    /* set read() timeout to 2000ms, give a chance to quite read earlier rather than wait forever.*/
    private static final int internal_tcp_read_timedOut = 30000;
    // Fusion message crypt format
    private static char CRYPT_FORMAT = '3';
    // Fusion message version
    private static char MESSAGE_VERSION = '2';
    // Fusion message router server port;
    private static int FUSION_MESSAGE_ROUTER_PORT = 3011;
    private int localPort = -1;
    public static String DEFAULT_FUSION_USER_NAME = "SSF";        // default fusion user id
    public static String DEFAULT_FUSION_PASSWORD = "MINIMEINFINITERINGTONES";   // default fusion password
    public static int MAX_PACKET_LENGTH = 1024 * 1024;
    private String remoteServerIpString;
    private InetAddress remoteServerIpAdrs;
    private Socket clientSocket;

    public void addOnMsgRouterClientStateChangedListeners(OnMsgRouterClientStateChangedListener listener) {
        this.onMsgRouterClientStateChangedListeners.add(listener);
    }

    public boolean removeOnMsgRouterClientStateChangedListeners(OnMsgRouterClientStateChangedListener listener) {
        return this.onMsgRouterClientStateChangedListeners.remove(listener);
    }

    public void addOnMsgRouterMessageReceivedListeners(OnMsgRouterMessageReceivedListener listener) {
        this.onMsgRouterMessageReceivedListeners.add(listener);
    }

    public boolean removeOnMsgRouterMessageReceivedListeners(OnMsgRouterMessageReceivedListener listener) {
        return this.onMsgRouterMessageReceivedListeners.remove(listener);
    }

    private static class SingleInstanceLoader {
        private static final MessageRouterClient INSTANCE = new MessageRouterClient();
    }

    /*
    * get default singleton instance of msg router client
    */
    public static MessageRouterClient getDefault() {
        return SingleInstanceLoader.INSTANCE;
    }

    private void safeCloseSocketResource() {
        fileLogger.error("Safe close all resources");
        //stopHeartbeatTimer();
        if (this.outputStream != null)
            try {
                this.outputStream.close();
            } catch (IOException e) {
            }
        if (this.clientSocket != null)
            try {
                this.clientSocket.close();
            } catch (Exception e) {
            }
    }

    /* trigger send and receive heartbeat timer.*/
    private void startHeartbeatTimer() {
        fileLogger.info("Start heartbeat ECHO request timer");
        this.heartbeatTimer = new Timer();
        this.heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String singleWayHeartbeat = MessageRouterClient.MESSAGE_VERSION + "||ECHO|||||^";
                try {
                    boolean succeed = MessageRouterClient.this.sendMessage(singleWayHeartbeat);
                    if (!succeed) {
                        fileLogger.error("Previous heartbeat request Send failed(return false), do nothing here");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    fileLogger.error("Previous heartbeat request Send with exception:" + e.getMessage());
                }
            }
            // set a fixed interval here is for avoid create 2 timers for send heartbeat and count for disconnection timer.
        }, 0, this.heartbeatSendInterval);
    }

    public Boolean sendMessage(String msg) throws IOException {
        //Console.WriteLine("WRITE LEN: {0}", msg.Length + 1);
        //Console.WriteLine("WRITE CRYPT: {0}", cryptFormat);
        //Console.WriteLine("WRITE DATA: {0}", msg);

        String msgx;
        if (CRYPT_FORMAT != '5') {
            if (msg.substring(msg.length() - 1) == "^")
                msg = msg.substring(0, msg.length() - 1);
            //String szKey = String.format("{0:d5}|{1:c}|{2:d6}", msg.length() + 1, cryptFormat, localPort);
            String szKey = Util.pad(Integer.toString(msg.length() + 1), "left", 5, '0') + "|"
                    + CRYPT_FORMAT + "|"
                    + Util.pad(Integer.toString(localPort), "left", 6, '0');
            write(CRYPT_FORMAT, SSFCrypt(msg, szKey));
            return true;
        }

        throw new UnsupportedOperationException("Only support CRYPT_FORMAT type 5 message sending to MsgRouterServer");
    }

    private void write(char cryptFormat, byte[] bytes) throws IOException {
        byte[] lenBuffer = new byte[MessageRouterClient.MSG_ROUTER_MSG_LEN_LENGTH];
        String msgLenStr = Util.pad(Integer.toString(bytes.length), "left", 5, '0');
        this.outputStream.write(msgLenStr.toString().getBytes());

        this.outputStream.write(0x7C);
        this.outputStream.write(cryptFormat);
        this.outputStream.write(0x7C);
        this.outputStream.write(bytes);
        this.outputStream.flush();
    }

    /*
    * Start the connection to remote FC (fdc server) with IP specified.
    * @param remoteServerIp, like: "10.0.2.2";
    * */
    public void start(String remoteServerIp) {
        if (!this.isStarted.compareAndSet(false, true)) {
            fileLogger.error("Invalid call for start() since MessageRouterClient still on running...");
            throw new IllegalStateException("Stop the MessageRouterClient first, then start()");
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
            try {
                this.remoteServerIpAdrs = InetAddress.getByName(remoteServerIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("remoteServerIp got UnknownHostException");
            }
        }

        fileLogger.info("Start MessageRouterClient (" + this.remoteServerIpAdrs.getHostAddress() + ":" + Integer.toString(FUSION_MESSAGE_ROUTER_PORT) + ")");
        this.shouldStopListening = false;
        Thread worker = new Thread(() -> {
            // connecting socket to remote host.
            while (!shouldStopListening) {
                try {
                    //==============init channel A
                    MessageRouterClient.this.clientSocket = new Socket();
                    changeMsgRouterClientStateAndNotifyAllListeners(MessageRouterClientState.Connecting);
                    fileLogger.debug("Initializing MsgRouter client socket to "
                            + MessageRouterClient.this.remoteServerIpString + ", port " + FUSION_MESSAGE_ROUTER_PORT);
                    ;
                    MessageRouterClient.this.clientSocket.setSoTimeout(MessageRouterClient.internal_tcp_read_timedOut);
                    MessageRouterClient.this.clientSocket.connect(
                            new InetSocketAddress(MessageRouterClient.this.remoteServerIpAdrs, FUSION_MESSAGE_ROUTER_PORT),
                            MessageRouterClient.internal_tcp_read_timedOut);
                    fileLogger.debug("Initialized msgRouter client socket");
                    MessageRouterClient.this.localPort = clientSocket.getLocalPort();
                    MessageRouterClient.this.outputStream = MessageRouterClient.this.clientSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    fileLogger.error("Got an IOException when connecting (via channel A) to Fdc Server, will retry later...");
                    safeCloseSocketResource();
                    try {
                        Thread.sleep(12000);
                    } catch (InterruptedException e1) {
                    }

                    continue;
                }

                startHeartbeatTimer();
                changeMsgRouterClientStateAndNotifyAllListeners(MessageRouterClientState.Connected);

                byte[] msgLenBytes = new byte[MSG_ROUTER_MSG_LEN_LENGTH];
                while (!shouldStopListening && state != MessageRouterClientState.DisconnectedByHeartbeat) {
                    try {
                        int previousTotalReadCount = 0;
                        do {
                            try {
                                fileLogger.trace("TCP, keep listening...");
                                int currentReadCount =
                                        MessageRouterClient.this.clientSocket.getInputStream().read(msgLenBytes,
                                                previousTotalReadCount,
                                                msgLenBytes.length - previousTotalReadCount);
                                // detect if need re-connect.
                                if (currentReadCount < 0) {
                                    break;
                                    //throw new IOException("read negative value when reading MessageLenBytes");
                                }
                                previousTotalReadCount += currentReadCount;
                            } catch (java.net.SocketTimeoutException te) {
                                if (!shouldStopListening && state != MessageRouterClientState.DisconnectedByHeartbeat) {
                                    // it's OK to see timed out for this read().
                                    // nothing will break, let's continue to keep reading.
                                    continue;
                                } else {
                                    throw new InterruptedException("cancel reading message (header) loop.");
                                }
                            }
                        } while (previousTotalReadCount < msgLenBytes.length);
                        String len_str = new String(msgLenBytes, "US-ASCII");
                        int removeHeaderZeroCount = 0;
                        for (int k = 0; k < len_str.length(); k++)
                            if (len_str.charAt(k) == '0')
                                removeHeaderZeroCount++;
                            else
                                break;
                        String real_len_str = len_str.substring(removeHeaderZeroCount);
                        int len = Integer.parseInt(real_len_str);
                        fileLogger.trace("TCP, read " + Integer.toString(MSG_ROUTER_MSG_LEN_LENGTH) + " bytes MsgLen: " + len);
                        //999999 is an experience value
                        if (len <= 0 || len > MAX_PACKET_LENGTH) {
                            fileLogger.error("TCP, Ignore since len is <=0 or too big, unlikely a valid msg.");
                            throw new InterruptedException("Msg Len abnormal exception.");
                        }

                        // header is(total 3 chars): |cryptoFormat(1 char)|
                        byte extraBytesCount = 3;
                        //start reading msg body.
                        fileLogger.trace("TCP, waiting read msg body (with " + internal_tcp_read_timedOut + "ms timeout)...");
                        byte[] msgBodyBytes = new byte[len + extraBytesCount];
                        previousTotalReadCount = 0;
                        do {
                            try {
                                fileLogger.trace("        reading...");
                                int currentReadCount = MessageRouterClient.this.clientSocket.getInputStream().read(msgBodyBytes,
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
                                fileLogger.error("TCP, waiting for read msg body timed out, will re-init the TCP connection");
                                throw new TimeoutException("read msg body timed out and exceed max retry times");
                            }
                        } while (previousTotalReadCount < msgBodyBytes.length);
                        // MsgRouter msg must start with a pipeline.
                        if (msgBodyBytes[0] != 0x7C) {
                            safeCloseSocketResource();
                            fileLogger.error("TCP, MsgRouter Msg is not started with a pipeline, must a wrong format msg, will re-init the TCP connection");
                            throw new FormatException("MsgRouter Msg is not started with a pipeline");
                        }

                        // Read crypto format
                        char crypt_format_from_incoming_msg = (char) msgBodyBytes[1];
                        fileLogger.info("cryptFormat in incoming msg is " + crypt_format_from_incoming_msg);
                        // MsgRouter msg must start with a pipeline.
                        if (msgBodyBytes[2] != 0x7C) {
                            safeCloseSocketResource();
                            fileLogger.error("TCP, MsgRouter Msg must have a pipeline after the cryptFormat char, will re-init the TCP connection");
                            throw new FormatException("MsgRouter Msg must have a pipeline after the cryptFormat char");
                        }

                        byte[] trueBody_extraBytesExcluded = new byte[msgBodyBytes.length - extraBytesCount];
                        for (int i = 0; i < trueBody_extraBytesExcluded.length; i++) {
                            trueBody_extraBytesExcluded[i] = msgBodyBytes[extraBytesCount + i];
                        }

                        // start parsing Xml data.
                        String msgData = new String(trueBody_extraBytesExcluded, "US-ASCII");
                        if (crypt_format_from_incoming_msg != '5') {
                            byte[] bytes = new byte[msgData.length() - 1];
                            System.arraycopy(trueBody_extraBytesExcluded, 0, bytes, 0, trueBody_extraBytesExcluded.length - 1);     // trim '^'
                            //string szKey = string.Format("{0:d5}|{1:c}|{2:d6}", p.buf.Length, cryptFormat, LocalPort);
                            String szKey = Util.pad(Integer.toString(trueBody_extraBytesExcluded.length), "left", 5, '0') + "|"
                                    + crypt_format_from_incoming_msg + "|"
                                    + Util.pad(Integer.toString(localPort), "left", 6, '0');
                            byte[] ba = SSFCrypt(bytes, szKey);
                            msgData = new String(ba, "US-ASCII");
                        }

                        String[] entries = msgData.split("\\|");

                        // Message Version (0)
                        // User ID         (1)
                        // Message Type    (2)
                        // Event Type      (3)
                        // Destination     (4)
                        // Origin          (5)
                        // Parameters in name=value format
                        // ^

                        if (entries.length < 8) {
                            // TODO: Handle malformed message dta
                        }

                        String msgType = entries[2];
                        String evtType = entries[3];
                        Hashtable<String, String> sd = new Hashtable<>();

                        for (int i = 6; i < entries.length - 1; i++) {
                            String param = entries[i];

                            // find the first index of '='
                            int pos = param.indexOf('=');
                            if (pos == -1)
                                continue;
                            String key = param.substring(0, pos);
                            String val = param.substring(pos + 1);
                            if (key == null || key.length() == 0)
                                continue;
                            sd.put(key, val);
                        }

                        // exclude the heartbeat msg logging here, too boring.
//                            if (!msgData.contains("MessageType=\"FDCHeartBeat\"")) {
//                                fileLogger.debug("<====TCP, read string data(hashed bytes for validation has been excluded): ");
//
//                                //there is a fixed size buffer in logcat for logs,
//                                //spit the xml data, otherwise it can not be shown on Android monitor
//                                for (String line : msgData.split("\r\n")) {
//                                    fileLogger.debug("    " + line);
//                                }
//                            }


                        try {
                            for (int i = 0; i < onMsgRouterMessageReceivedListeners.size(); i++)
                                if (onMsgRouterMessageReceivedListeners != null && onMsgRouterMessageReceivedListeners.size() > 0)
                                    onMsgRouterMessageReceivedListeners.get(i)
                                            .onMsgRouterMessageReceived(MessageRouterClient.this, msgType, evtType, sd);
//                                if (xmlData.contains("<ServiceResponse ")) {
//                                    for (int i = 0; i < onMsgRouterServiceResponseReceivedListeners.size(); i++)
//                                        if (onFdcServiceResponseReceivedListeners != null && onFdcServiceResponseReceivedListeners.size() > 0)
//                                            onFdcServiceResponseReceivedListeners.get(i)
//                                                    .onServiceResponseReceived(FdcClient.this, FdcClient.this.deserializeFdcServiceResponse(xmlData));
//                                } else if (xmlData.contains("<FDCMessage ")) {
//                                    for (int i = 0; i < onFdcMessageReceivedListeners.size(); i++)
//                                        if (onFdcMessageReceivedListeners != null && onFdcMessageReceivedListeners.size() > 0)
//                                            onFdcMessageReceivedListeners.get(i)
//                                                    .onFdcMessageReceived(FdcClient.this, FdcClient.this.deserializeFdcMessage(xmlData));
//                                }
                        } catch (Exception _) {
                            _.printStackTrace();
                            fileLogger.error("Fire event exceptioned, Ignore the exception");
                        }
                    } catch (IOException ee) {
                        ee.printStackTrace();
                        fileLogger.error("TCP, read bytes error, will re-init the whole Socket");
                        safeCloseSocketResource();
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        fileLogger.error("TCP, got InterruptedException, will re-init the whole Socket");
                        safeCloseSocketResource();
                        break;
                    } catch (Exception eee) {
                        eee.printStackTrace();
                        fileLogger.error("TCP, got generic Exception, will re-init the whole Socket");
                        safeCloseSocketResource();
                        break;
                    }
                }
            }

            isStarted.set(false);
            fileLogger.warn("MessageRouterClient quit listening");
        }
        );
        worker.start();
    }

    /*stop and start with previous argument. */
    public void restart() {
        stop();
        start(null);
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

    public void stopAsync(final OnMsgRouterClientStateChangedListener callback) {
        fileLogger.warn("Stopping the FdcClient with Async");
        this.shouldStopListening = true;
        safeCloseSocketResource();
        Thread _ = new Thread(() -> {
            // wait the worker thread to fully quit TCP listening.
            while (isStarted.get()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            fileLogger.warn("Stopped the MessageRouterClient with Async");
            if (callback != null)
                callback.onMsgRouterClientStateChanged(MessageRouterClient.this, MessageRouterClientState.Stopped);
        }
        );
        _.start();
    }

    private void changeMsgRouterClientStateAndNotifyAllListeners(MessageRouterClientState newState) {
        // only notify when state switched.
        if (this.state == newState) return;
        fileLogger.info("State changed from '" + this.state + "' to '" + newState + "'");
        this.state = newState;
        for (int i = 0; i < this.onMsgRouterClientStateChangedListeners.size(); i++) {
            this.onMsgRouterClientStateChangedListeners.get(i).onMsgRouterClientStateChanged(this, this.state);
        }
    }

    private byte[] SSFCrypt(String data, String key) throws UnsupportedEncodingException {
        //ASCIIEncoding enc = new ASCIIEncoding();
        byte[] ba = data.getBytes("US-ASCII");
        return SSFCrypt(ba, key);
    }

    private byte[] SSFCrypt(byte[] data, String key) {
        //we will consider size of sbox 256 bytes
        //(extra byte are only to prevent any mishep just in case)
        Byte[] Sbox = new Byte[257];
        Byte[] Sbox2 = new Byte[257];

        int i, j, t;
        Byte temp, k;

        i = j = t = 0;
        temp = k = 0x00;

        //always initialize the arrays with zero
        for (i = 0; i < 257; i++) {
            Sbox[i] = Sbox2[i] = 0x00;
        }

        //initialize sbox i
        for (i = 0; i < 256; i++) {
            Sbox[i] = (byte) i;
        }

        j = 0;
        //initialize the sbox2 with user key
        for (i = 0; i < 256; i++) {
            if (j == key.length()) {
                j = 0;
            }
            Sbox2[i] = (byte) key.charAt(j++);
        }

        j = 0;    //Initialize j
        //scramble sbox1 with sbox2
        for (i = 0; i < 256; i++) {
            j = (j + Util.toUnsignedInt(Sbox[i]) + Util.toUnsignedInt(Sbox2[i])) % 256;
            temp = Sbox[i];
            try {
                Sbox[i] = Sbox[j];
                Sbox[j] = temp;
            } catch (Exception exxx) {
                exxx.printStackTrace();
            }

        }

        i = j = 0;
        byte[] res = new byte[data.length];
        for (int x = 0; x < data.length; x++) {
            //increment i
            i = (i + 1) % 256;
            //increment j
            j = (j + Util.toUnsignedInt(Sbox[i])) % 256;

            //Scramble SBox #1 further so encryption routine will
            //will repeat itself at great interval
            temp = Sbox[i];
            Sbox[i] = Sbox[j];
            Sbox[j] = temp;

            //Get ready to create pseudo random  byte for encryption key
            t = (Util.toUnsignedInt(Sbox[i]) + Util.toUnsignedInt(Sbox[j])) % 256;

            //get the random byte
            k = Sbox[t];

            //xor with the data and done
            res[x] = (byte) (data[x] ^ k);
        }

        byte[] bytes = new byte[res.length + 1];

        System.arraycopy(res, 0, bytes, 0, res.length);
        bytes[res.length] = 0x5E; // '^'
        return bytes;
    }
}
