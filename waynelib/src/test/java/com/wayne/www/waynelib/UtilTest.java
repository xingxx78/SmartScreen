package com.wayne.www.waynelib;

import com.wayne.www.waynelib.fdc.MessageRouterClient;
import com.wayne.www.waynelib.ui.Tuple;

import org.junit.Test;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Think on 4/20/2016.
 */
@Root
public class UtilTest {
    @Test
    public void date_to_xmlDateString() throws Exception {
        String actual = Util.getNowTimeWithXmlFormat();
        assertTrue(actual, actual != null);
    }

    @Test
    public void convertTimeStrToJavaDate0() throws Exception {
        String rawFromWebService = "1983-10-04T08:18:28";
        Calendar expected = new GregorianCalendar(1983, 10, 4, 8, 18, 28);
        Calendar actual = Util.convertTimeStrToJavaDate(rawFromWebService, "yyyy-MM-dd'T'HH:mm:ss");
        int actualYear = actual.get(Calendar.YEAR);
        int actualMonth = actual.get(Calendar.MONTH) + 1;
        int actualDay = actual.get(Calendar.DAY_OF_MONTH);
        int actualHour = actual.get(Calendar.HOUR_OF_DAY);
        int actualMin = actual.get(Calendar.MINUTE);
        int actualSec = actual.get(Calendar.SECOND);
        assertTrue(actualYear == 1983);
        assertTrue(actualMonth == 10);
        assertTrue(actualDay == 4);
        assertTrue(actualHour == 8);
        assertTrue(actualMin == 18);
        assertTrue(actualSec == 28);
    }

    @Test
    public void convertTimeStrToJavaDate1() throws Exception {
        String rawFromWebService = "1983-10-04";
        Calendar actual = Util.convertTimeStrToJavaDate(rawFromWebService, "yyyy-MM-dd");
        int actualYear = actual.get(Calendar.YEAR);
        int actualMonth = actual.get(Calendar.MONTH) + 1;
        int actualDay = actual.get(Calendar.DAY_OF_MONTH);
        int actualHour = actual.get(Calendar.HOUR_OF_DAY);
        int actualMin = actual.get(Calendar.MINUTE);
        int actualSec = actual.get(Calendar.SECOND);
        assertTrue(actualYear == 1983);
        assertTrue(actualMonth == 10);
        assertTrue(actualDay == 4);
    }

    @Test
    public void convertTimeStrToJavaDate3() throws Exception {
        String rawFromWebService = "1983-10-04 12:01:02";
        Calendar actual = Util.convertTimeStrToJavaDate(rawFromWebService, "yyyy-MM-dd HH:mm:ss");
        int actualYear = actual.get(Calendar.YEAR);
        int actualMonth = actual.get(Calendar.MONTH) + 1;
        int actualDay = actual.get(Calendar.DAY_OF_MONTH);
        int actualHour = actual.get(Calendar.HOUR_OF_DAY);
        int actualMin = actual.get(Calendar.MINUTE);
        int actualSec = actual.get(Calendar.SECOND);
        assertTrue(actualYear == 1983);
        assertTrue(actualMonth == 10);
        assertTrue(actualDay == 4);
        assertTrue(actualHour == 12);
        assertTrue(actualMin == 1);
        assertTrue(actualSec == 2);
    }

    @Test
    public void convertTimeStrToJavaDate4() throws Exception {
        String rawFromWebService = "0001-01-01T00:00:00";
        Calendar actual = Util.convertTimeStrToJavaDate(rawFromWebService, "yyyy-MM-dd'T'HH:mm:ss");
        int actualYear = actual.get(Calendar.YEAR);
        int actualMonth = actual.get(Calendar.MONTH) + 1;
        int actualDay = actual.get(Calendar.DAY_OF_MONTH);
        int actualHour = actual.get(Calendar.HOUR_OF_DAY);
        int actualMin = actual.get(Calendar.MINUTE);
        int actualSec = actual.get(Calendar.SECOND);
        assertTrue(actualYear == 1983);
        assertTrue(actualMonth == 10);
        assertTrue(actualDay == 4);
        assertTrue(actualHour == 12);
        assertTrue(actualMin == 1);
        assertTrue(actualSec == 2);
    }


    @Test
    public void pad_String_test() throws Exception {
        String target = "1234";
        String expectedLeft = "00001234";
        String actualLeft = Util.pad(target, "left", 8, '0');
        assertTrue("failed for left padding, actual: " + actualLeft, expectedLeft.equals(actualLeft));

        String expectedRight = "12340000";
        String actualRight = Util.pad(target, "right", 8, '0');
        assertTrue("failed for left padding, actual: " + actualRight, expectedRight.equals(actualRight));
    }

    @Test
    public void msgRouter_test0() throws Exception {
        Tuple<Boolean, MessageRouterClient.MessageRouterClientState> state = Tuple.create(true, null);
        MessageRouterClient.getDefault().addOnMsgRouterClientStateChangedListeners((a, b) -> {
            state.second = b;
            state.first = false;
        });
        MessageRouterClient.getDefault().start("127.0.0.1");
        while (state.first) {
            Thread.sleep(1000);
        }

        assertTrue(state.second == MessageRouterClient.MessageRouterClientState.Connected);
    }
}
