package com.wayne.www.waynelib;

import android.os.Build;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Created by Think on 4/19/2016.
 */
public class Util {
    static String LOG_TAG = "com.wayne.ui.Util";
    private static Logger fileLogger = LoggerFactory.getLogger(Util.class);

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        for (int i = 0; i < first.length; i++) {
            result[i] = first[i];
        }

        for (int i = 0; i < second.length; i++) {
            result[first.length + i] = second[i];
        }

        return result;
    }

    public static int byteArray4ToInt(byte[] b) {
        if (b == null || b.length != 4) return -1;
        int result = (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + ((b[0] & 0xFF) << 24);
//        Log.v(LOG_TAG, "byteArray4ToInt, result is " + result
//                + ", byte[3]: " + b[3] + "(" + (b[3] & 0xFF) + ")"
//                + ", byte[2]: " + b[2] + "(" + ((b[2] & 0xFF) << 8) + ")"
//                + ", byte[1]: " + b[1] + "(" + ((b[1] & 0xFF) << 16) + ")"
//                + ", byte[0]: " + b[0] + "(" + ((b[0] & 0xFF) << 24) + ")");
        return result;
    }

    /* byte[0] is the low digit. */
    public static byte[] intToByteArray4(int a) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[2] = (byte) ((a >> 16) & 0xFF);
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    /* byte[0] is the high digit. */
    public static byte[] intToByteArray4Reverse(int a) {
        byte[] ret = new byte[4];
        ret[0] = (byte) ((a >> 24) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[3] = (byte) (a & 0xFF);
        return ret;
    }

    /* byte[0] is the low digit. */
    public static byte[] stringToBytesWith4BytesLenAhead(String value) {
        byte[] len = intToByteArray4(value.length());
        byte[] body = value.getBytes();
        byte[] result = new byte[4 + body.length];
        for (int i = 0; i < result.length; i++) {
            if (i <= 3)
                result[i] = len[i];
            else
                result[i] = body[i - 4];
        }

        return result;
    }

    /**
     * @param format "yyyy-MM-dd HH:mm:ss", or "yyyy-MM-dd"
     * @return
     */
    public static String getCurrentTimeString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }

    public static String toString(Document document) {
        String result = null;
        if (document != null) {
            StringWriter strWtr = new StringWriter();
            StreamResult strResult = new StreamResult(strWtr);
            TransformerFactory tfac = TransformerFactory.newInstance();
            try {
                Transformer t = tfac.newTransformer();
                t.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty(OutputKeys.METHOD, "xml"); // xml, html,// text
                t.setOutputProperty(
                        "{http://xml.apache.org/xslt}indent-amount", "4");
                t.transform(new DOMSource(document.getDocumentElement()),
                        strResult);
            } catch (Exception e) {
                System.err.println("XML.toString(Document): " + e);
            }
            result = strResult.getWriter().toString();
        }

        return result;
    }

    public static String convertJavaDateToXmlTime(Date date) {
        Calendar calendar = Calendar.getInstance();
    /* format time */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        StringBuffer buff = new StringBuffer();
        buff.append(sdf1.format(date));
        buff.append('T');
        buff.append(sdf2.format(date));
    /* calculate time zone */
        int offset = calendar.get(calendar.ZONE_OFFSET) / (1000 * 60);
        if (offset < 0) {
            buff.append('-');
            offset *= -1;
        } else {
            buff.append('+');
        }
        String s1 = String.valueOf(offset / 60);
        for (int i = s1.length(); i < 2; i++) {
            buff.append('0');
        }
        buff.append(s1);
        buff.append(':');
        String s2 = String.valueOf(offset % 60);
        for (int i = s2.length(); i < 2; i++) {
            buff.append('0');
        }
        buff.append(s2);
        System.out.println("in DateUtils: " + buff.toString());
        return buff.toString();
    }

    /* sample result is like: 2016-04-26T09:41:44 */
    public static String getNowTimeWithXmlFormat() {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String result = sdf.format(new Date());
        return result;
    }

    /**
     * input string like: 2016-04-29 11:20:16, and convert it to java.util.Date
     *
     * @param dateString   like: 2016-04-29 11:20:16 or 1983-10-04T08:18:28
     * @param formatString like: yyyy-MM-dd HH:mm:ss   or  yyyy-MM-dd'T'HH:mm:ss
     * @return for retrieve the year, month, day and etc. via return result(Calendar type),
     * use code like: result.get(Calendar.YEAR), result.get(Calendar.MONTH), NOTE, the MONTH is 0-based,
     * so return int 0 indicates month January, int 1 indicates month February.
     */
    public static Calendar convertTimeStrToJavaDate(String dateString, String formatString) {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        Calendar calendar = new GregorianCalendar();

        try {
            Date _ = sdf.parse(dateString);
            calendar.setTime(_);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public static String setGravity(String s, String gravity, int totalWidth, char paddingWith) {
        int strLen = s.length();
        if (strLen >= totalWidth)
            return s;

        if (gravity.equalsIgnoreCase("right"))
            return pad(s, "left", totalWidth, paddingWith);
        else if (gravity.equalsIgnoreCase("left"))
            return pad(s, "right", totalWidth, paddingWith);
        else {
            return pad(s, "left", strLen + (totalWidth - strLen) / 2, paddingWith);
        }
    }

    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    /*direction: "left", "right"*/
    public static String pad(String s, String direction, int totalWidth, char paddingWith) {
        if (s.length() >= totalWidth) return s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalWidth - s.length(); i++) {
            sb.append(paddingWith);
        }

        if (direction.equalsIgnoreCase("right"))
            return s + sb.toString();
        else if (direction.equalsIgnoreCase("left"))
            return sb.toString() + s;
        return "";
    }

    /* 0003 will equal with 3, 03 will equal with 3*/
    public static boolean isRequestIdEqual(String lid, String rid) {
        int zeroDigitCount = 0;
        for (int i = 0; i < lid.length(); i++) {
            if (Character.compare(lid.charAt(i), '0') == 0) {
                zeroDigitCount++;
            }
        }

        String newLid = lid.substring(zeroDigitCount);

        zeroDigitCount = 0;
        for (int i = 0; i < rid.length(); i++) {
            if (Character.compare(rid.charAt(i), '0') == 0) {
                zeroDigitCount++;
            }
        }

        String newRid = rid.substring(zeroDigitCount);
        return newLid.equals(newRid);
    }

    public static String floatTo2DigitsString(float value) {
        DecimalFormat myFormatter = new DecimalFormat("##0.00");
        return myFormatter.format(value);
    }

    /**
     * auto zip, and then upload.
     *
     * @param serverIp
     * @param ftpServerSubFolder without and slash, just the plain folder name is good.
     */
    public static void uploadTxtLogFileToServer(String serverIp, String ftpServerSubFolder, String[] simpleSearchPatterns) {
        FTPClient con;
        File targetFolder = new File("/data/data/com.wayne.www.xpos/log");
        try {
            con = new FTPClient();
            //con.connect("116.62.148.205");
            con.connect(serverIp);
            if (con.login("androidPos", "111111")) {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
//                con.setFileTransferMode(FTP.ASCII_FILE_TYPE);
                File[] logFiles = targetFolder.listFiles();
                for (int i = 0; i < logFiles.length; i++) {
                    Boolean matched = false;
                    for (int s = 0; s < simpleSearchPatterns.length; s++) {
                        if (logFiles[i].getName().contains(simpleSearchPatterns[s])
                                // ONLY check .txt file
                                && logFiles[i].getName().endsWith(".txt")) {
                            matched = true;
                            break;
                        }
                    }

                    if (!matched) continue;
                    fileLogger.debug("File: " + logFiles[i].getName() + " is selected for uploading...");
                    String zipTo_ShortFileName = logFiles[i].getName() + ".zip";
                    String zipTo_FullFileName = logFiles[i].getParent() + "/" + logFiles[i].getName() + ".zip";
                    fileLogger.debug("zip FileName will be set to: " + zipTo_FullFileName);
                    zip(logFiles[i].getPath(), zipTo_FullFileName);
                    FileInputStream in = new FileInputStream(zipTo_FullFileName);
                    // hardcode the default folder for no SN device
                    if (ftpServerSubFolder == null) ftpServerSubFolder = "Unknown_No_SN_Device";
                    fileLogger.info("Log file: " + zipTo_FullFileName + " is uploading to " + ftpServerSubFolder);
                    con.makeDirectory(ftpServerSubFolder);
                    boolean result = con.storeFile("\\" + ftpServerSubFolder + "\\" + zipTo_ShortFileName, in);
                    fileLogger.info("Log file: " + zipTo_ShortFileName + " is uploaded with result: " + result);
                    in.close();
                }

                con.logout();
                con.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int ZIP_BUFFER_SIZE = 1024;

    public static void zip(String sourceFileFullPath, String descFileFullPath) throws IOException {
        BufferedInputStream origin;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(descFileFullPath)));
        try {
            byte data[] = new byte[ZIP_BUFFER_SIZE];
            FileInputStream fi = new FileInputStream(sourceFileFullPath);
            origin = new BufferedInputStream(fi, ZIP_BUFFER_SIZE);
            try {
                ZipEntry entry = new ZipEntry(sourceFileFullPath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, ZIP_BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }

                out.closeEntry();
            } finally {
                origin.close();
            }
        } finally {
            out.close();
        }
    }

//    public static void zip(String fileFullPath, String zipToFileFullPath) {
//        // Input and OutputStreams are defined outside of the try/catch block
//        // to use them in the finally block
//        ZipOutputStream outputStream = null;
//        InputStream inputStream = null;
//
//        try {
//            // Prepare the files to be added
//            ArrayList filesToAdd = new ArrayList();
//            filesToAdd.add(new File(fileFullPath));
////            filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
////            filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));
//
//            //Initiate output stream with the path/file of the zip file
//            //Please note that ZipOutputStream will overwrite zip file if it already exists
//            outputStream = new ZipOutputStream(new FileOutputStream(new File(zipToFileFullPath)));
//
//            // Initiate Zip Parameters which define various properties such
//            // as compression method, etc. More parameters are explained in other
//            // examples
//
//            ZipParameters parameters = new ZipParameters();
//            //Deflate compression or store(no compression) can be set below
//            parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
//
//            // Set the compression level. This value has to be in between 0 to 9
//            // Several predefined compression levels are available
//            // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
//            // DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
//            // DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
//            // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
//            // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
//            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
//
//            //This flag defines if the files have to be encrypted.
//            //If this flag is set to false, setEncryptionMethod, as described below,
//            //will be ignored and the files won't be encrypted
//            parameters.setEncryptFiles(false);
//            //Zip4j supports AES or Standard Zip Encryption (also called ZipCrypto)
//            //If you choose to use Standard Zip Encryption, please have a look at example
//            //as some additional steps need to be done while using ZipOutputStreams with
//            //Standard Zip Encryption
//            //parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
//
//            //If AES encryption is used, this defines the key strength
//            //parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
//
//            //self descriptive
//            //parameters.setPassword("YourPassword");
//
//            //Now we loop through each file and read this file with an inputstream
//            //and write it to the ZipOutputStream.
//            for (int i = 0; i < filesToAdd.size(); i++) {
//                File file = (File) filesToAdd.get(i);
//
//                //This will initiate ZipOutputStream to include the file
//                //with the input parameters
//                outputStream.putNextEntry(file, parameters);
//
//                //If this file is a directory, then no further processing is required
//                //and we close the entry (Please note that we do not close the outputstream yet)
//                if (file.isDirectory()) {
//                    outputStream.closeEntry();
//                    continue;
//                }
//
//                //Initialize inputstream
//                inputStream = new FileInputStream(file);
//                byte[] readBuff = new byte[4096];
//                int readLen = -1;
//
//                //Read the file content and write it to the OutputStream
//                while ((readLen = inputStream.read(readBuff)) != -1) {
//                    outputStream.write(readBuff, 0, readLen);
//                }
//
//                //Once the content of the file is copied, this entry to the zip file
//                //needs to be closed. ZipOutputStream updates necessary header information
//                //for this file in this step
//                outputStream.closeEntry();
//
//                inputStream.close();
//            }
//
//            //ZipOutputStream now writes zip header information to the zip file
//            outputStream.finish();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}
