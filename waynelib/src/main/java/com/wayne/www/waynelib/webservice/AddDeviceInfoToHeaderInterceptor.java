package com.wayne.www.waynelib.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Shawn on 5/31/2017.
 */

public class AddDeviceInfoToHeaderInterceptor implements Interceptor {
    private static Logger fileLogger = LoggerFactory.getLogger(CloudServiceGenerator.class);
    private static String deviceBrand;
    private static String deviceModel;
    public static String deviceSN;
    private static String deviceManufacturer;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        if (deviceBrand != null)
            builder.header("DeviceBrand", deviceBrand);
        if (deviceModel != null)
            builder.header("DeviceModel", deviceModel);
        if (deviceSN != null)
            builder.header("DeviceSN", deviceSN);
        if (deviceManufacturer != null)
            builder.header("DeviceManufacturer", deviceManufacturer);

        Request request = builder.build();
        return chain.proceed(request);
    }

//    public static Request appendAuthorizationHeader(Request.Builder requestBuilder, String authToken) {
//        fileLogger.trace("AddAuthToHeaderInterceptor, starting appendAuthorizationHeader()");
//        Request.Builder builder = requestBuilder
//                .header("Authorization", authToken);
//        Request request = builder.build();
//        fileLogger.trace("AddAuthToHeaderInterceptor, done appendAuthorizationHeader()");
//        return request;
//    }

    public static void enableAttachPosDeviceInfo(String posDeviceBrand, String posDeviceModel, String posDeviceSN, String posDeviceManufacturer) {
        deviceBrand = posDeviceBrand;
        deviceModel = posDeviceModel;
        deviceSN = posDeviceSN;
        deviceManufacturer = posDeviceManufacturer;
    }
}
