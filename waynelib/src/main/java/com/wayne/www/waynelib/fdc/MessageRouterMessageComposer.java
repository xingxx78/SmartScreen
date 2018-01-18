package com.wayne.www.waynelib.fdc;

import com.wayne.www.waynelib.Util;

/**
 * Created by Shawn on 10/30/2017.
 */

public class MessageRouterMessageComposer {
    private static class SingleInstanceLoader {
        private static final MessageRouterMessageComposer INSTANCE = new MessageRouterMessageComposer();
    }

    public static int MAX_PACKET_LENGTH = 1024 * 1024;
    public static int CRYPT_FORMAT_LENGTH = 1;
    public static int PIPE_LENGTH = 1;

    /*
    * get default singleton instance of msg Composer
    */
//    public static MessageRouterMessageComposer getDefault() {
//        return MessageRouterMessageComposer.SingleInstanceLoader.INSTANCE;
//    }

    public static String getLoginMessage(String userid, String password) {
        String paddingUserName = Util.pad(MessageRouterClient.DEFAULT_FUSION_USER_NAME, "right", 20, ' ');
        String paddingPwd = Util.pad(MessageRouterClient.DEFAULT_FUSION_PASSWORD, "right", 25, ' ');
        char[] cryptedPwd = MessageRouterMessageComposer.Crypt(paddingPwd.toCharArray(), 25, paddingUserName.toCharArray(), 20);
        String finalPwd = MessageRouterMessageComposer.binToHexString(cryptedPwd, 25);
        return String.format("2|GUEST|POST|REQ_SECU_LOGIN|||US=%s|PW=%s|^", userid, finalPwd);
    }

    public static String getSubscribePumpStatusChangeMessage() {
        return String.format("2|GUEST|SUBSCRIBE|EVT_PUMP_STATUS_CHANGE_ID_*||||^");
    }

    public static String getSubscribePumpDeliveryProgressMessage() {
        return String.format("2|GUEST|SUBSCRIBE|EVT_PUMP_DELIVERY_PROGRESS_ID_*||||^");
    }

    public static String getEmergencyStopMessage(int fpId) {
        String paddingFpId = Util.pad(Integer.toString(fpId), "left", 3, '0');
        return String.format("2|GUEST|POST|REQ_PUMP_STOP_ID_%s|||PA=%s|^", paddingFpId, "1");
    }

    /// <summary>
    /// Clear stop message
    /// </summary>
    /// <param name="fpId"></param>
    public static String getClearStopMessage(int fpId) {
        String paddingFpId = Util.pad(Integer.toString(fpId), "left", 3, '0');
        return String.format("2|GUEST|POST|REQ_PUMP_CLEAR_STOP_ID_%s||||^", paddingFpId);
    }


    public static String getSubscribeStartingToApplyNewPriceChangeMessage() {
        return String.format("2|GUEST|SUBSCRIBE|EVT_STARTING_TO_APPLY_NEW_PRICE_CHANGE||||^");
    }

    public static String getSubscribeNewPriceChangeAppliedMessage() {
        return "2|GUEST|SUBSCRIBE|EVT_NEW_PRICE_CHANGE_APPLIED||||^";
    }

    public static String getSubscribeSetNewPriceChangeMessage() {
        return String.format("2|GUEST|SUBSCRIBE|RES_PRICES_SET_NEW_PRICE_CHANGE||||^");
    }

    public static String getSubscribeReceivePresetFromForecourtMessage() {
        return String.format("2|GUEST|SUBSCRIBE|REQ_RECEIVE_PRESET_FROM_FORECOURT_ID_*||||^");
    }

    public static String getSubscribeRefreshPriceChangeTblMessage() {
        return String.format("2|GUEST|SUBSCRIBE|RES_PRICES_REFRESH_PRICE_CHANGE_TBL||||^");
    }

    public static String getLoadNewPricesMessage() {
        return String.format("2|GUEST|POST|REQ_LOAD_NEW_PRICES||||^");
    }

    /**
     * @param effectiveDate Date to be processed, YYYYMMDD
     * @param effectiveTime Time to be pricessed, HHMMSS
     * @param gradeNumber   Grade number
     * @param newPrice      Grade price
     * @return
     */
    public static String getSetNewPriceChangeMessage(String effectiveDate, String effectiveTime, String gradeNumber, String newPrice) {
        if (effectiveDate.length() == 0 && effectiveTime.length() == 0)
            return String.format("2|GUEST|POST|REQ_PRICES_SET_NEW_PRICE_CHANGE|||QTY=1|G01NR=%s|G01LV=1|G01PR=%s|^",
                    gradeNumber, newPrice);
        else
            return String.format("2|GUEST|POST|REQ_PRICES_SET_NEW_PRICE_CHANGE|||DA=%s|TI=%s|QTY=1|G01NR=%s|G01LV=1|G01PR=%s|^",
                    effectiveDate, effectiveTime, gradeNumber, newPrice);
    }

    public static String getRefreshPriceChangeTableMessage() {
        return String.format("2|GUEST|POST|REQ_PRICES_REFRESH_PRICE_CHANGE_TBL||||^");
    }


    public static String getRefreshPumpStatusMessage() {
        return String.format("2|GUEST|POST|REQ_PUMP_STATUS_ID_000||||^");
    }

    public static String getSubscribeReceiveOutdoorTrxAirlockFromForecourtMessage() {
        return String.format("2|GUEST|SUBSCRIBE|REQ_RECEIVE_OUTDOOR_AIRLOCK_ID_*||||^");
    }

    private static char[] Crypt(char[] inp, int inplen, char[] key, int keylen) {
        //we will consider size of sbox 256 chars
        //(extra char are only to prevent any mishep just in case)
        char[] Sbox = new char[257];
        char[] Sbox2 = new char[257];
        int i, j, t, x;
        char temp, k = '\0';
        i = j = t = x = 0;
        temp = '\0';

        for (i = 0; i < 256; i++) {
            Sbox[i] = (char) 0;
            Sbox2[i] = (char) 0;
        }

        //initialize sbox i
        for (i = 0; i < 256; i++) {
            Sbox[i] = (char) i;
        }

        j = 0;
        //initialize the sbox2 with user key
        for (i = 0; i < 256; i++) {
            if (j == keylen) {
                j = 0;
            }
            Sbox2[i] = key[j++];
        }

        j = 0; //Initialize j

        //scramble sbox1 with sbox2
        for (i = 0; i < 256; i++) {
            j = (j + Sbox[i] + Sbox2[i]) % 256;
            temp = Sbox[i];
            Sbox[i] = Sbox[j];
            Sbox[j] = temp;
        }
        i = j = 0;
        for (x = 0; x < inplen; x++) {
            //increment i
            i = (i + 1) % 256;
            //increment j
            j = (j + Sbox[i]) % 256;

            //Scramble SBox #1 further so encryption routine will
            //will repeat itself at great interval
            temp = Sbox[i];
            Sbox[i] = Sbox[j];
            Sbox[j] = temp;

            //Get ready to create pseudo random  char for encryption key
            t = (Sbox[i] + Sbox[j]) % 256;
            //get the random char
            k = Sbox[t];
            //xor with the data and done
            inp[x] = (char) (inp[x] ^ k);
        }
        return inp;
    }

    private static String binToHexString(char[] binStr, int len) {
        char[] BINTOCHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int i;
        String retStr = "";
        for (i = 0; i < len; i++) {
            retStr += "" + BINTOCHAR[(binStr[i] & 0xf0) >> 4] + "" + BINTOCHAR[binStr[i] & 0x0f];
        }
        return retStr;
    }
}
