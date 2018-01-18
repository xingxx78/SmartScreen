package com.wayne.www.waynelib;

import android.util.Log;

import com.wayne.www.waynelib.fdc.message.MD5Crypter;

import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;


import static org.junit.Assert.assertTrue;

/**
 * Created by Think on 4/24/2016.
 */
public class Md5UnitTest {
    @Test
    public void md5_BetweenCShart_and_Java() throws Exception {
        String hexString = "3C3F786D6C2076657273696F6E3D22312E30223F3E0D0A3C536572766963655265717565737420786D6C6E733A7873643D22687474703A2F2F7777772E77332E6F72672F323030312F584D4C536368656D612220786D6C6E733A7873693D22687474703A2F2F7777772E77332E6F72672F323030312F584D4C536368656D612D696E7374616E6365222052657175657374547970653D224C6F674F6E22204170706C69636174696F6E53656E6465723D223130312220576F726B73746174696F6E49443D2231303122205265717565737449443D2231223E0D0A20203C504F53646174613E0D0A202020203C504F5354696D655374616D703E323031362D30342D32345432303A34383A35323C2F504F5354696D655374616D703E0D0A202020203C504F534E616D65202F3E0D0A202020203C526573706F6E7365506F72743E343731303C2F526573706F6E7365506F72743E0D0A202020203C556E736F6C696369746564506F72743E343731303C2F556E736F6C696369746564506F72743E0D0A202020203C696E7465726661636556657273696F6E3E30302E30373C2F696E7465726661636556657273696F6E3E0D0A20203C2F504F53646174613E0D0A3C2F53657276696365526571756573743E44726573736572467573696F6E7D313233";
        byte[] bytesFromHexString = Util.hexStringToByteArray(hexString);
        String moreTrueXml = new String(bytesFromHexString,StandardCharsets.UTF_8);
        //from vs2013 md5.
//        String originalXml = "<?xml version=\"1.0\"?>\n" +
//                "<ServiceRequest xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" RequestType=\"LogOn\" ApplicationSender=\"101\" WorkstationID=\"101\" RequestID=\"1\">\n" +
//                "  <POSdata>\n" +
//                "    <POSTimeStamp>2016-04-24T20:48:52</POSTimeStamp>\n" +
//                "    <POSName />\n" +
//                "    <ResponsePort>4710</ResponsePort>\n" +
//                "    <UnsolicitedPort>4710</UnsolicitedPort>\n" +
//                "    <interfaceVersion>00.07</interfaceVersion>\n" +
//                "  </POSdata>\n" +
//                "</ServiceRequest>DresserFusion}123";
        String vsMd5 = "C17A8B6F561942B5794F86963C2BDC53";
        MD5Crypter md5Crypter = new MD5Crypter();
        byte[] localMd5Bytes = md5Crypter.computeHash(moreTrueXml.getBytes(StandardCharsets.US_ASCII));
        String localMd5 = Util.bytesToHexString(localMd5Bytes);
        assertTrue("local: " + localMd5 + "   anotherFormat: " + new BigInteger(1, localMd5Bytes).toString(16)
                , vsMd5.equals(localMd5));
    }

    @Test
    public void simpleMd5_test0() throws Exception {
        //from vs2013 md5.
        String originalXml = "hello";
        String vsMd5 = "5D41402ABC4B2A76B9719D911017C592";
        MD5Crypter md5Crypter = new MD5Crypter();
        byte[] localMd5Bytes = md5Crypter.computeHash(originalXml.getBytes(StandardCharsets.US_ASCII));
        String localMd5 = Util.bytesToHexString(localMd5Bytes);
        assertTrue("local: " + localMd5 + "   anotherFormat: " + new BigInteger(1, localMd5Bytes).toString(16)
                , vsMd5.equals(localMd5));
    }
}
