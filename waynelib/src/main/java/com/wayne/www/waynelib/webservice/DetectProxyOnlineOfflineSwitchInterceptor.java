package com.wayne.www.waynelib.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Shawn on 5/31/2017.
 */

public class DetectProxyOnlineOfflineSwitchInterceptor implements Interceptor {
    private static Logger fileLogger = LoggerFactory.getLogger(CloudServiceGenerator.class);
    private static ArrayList<OnProxyOnlineOfflineSwitchListener> listeners = new ArrayList<>();
    private static final String IndicatorNameInResponseHeader = "ProxyOnline";
    private static boolean previousStatusOfIsOnline = true;

    public static boolean isOnline() {
        return previousStatusOfIsOnline;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // try the request
        Response response = chain.proceed(request);
        if (response.header(IndicatorNameInResponseHeader) != null
                && response.header(IndicatorNameInResponseHeader).equalsIgnoreCase("true")) {
            if (!previousStatusOfIsOnline) {
                for (int i = 0; i < listeners.size(); i++)
                    listeners.get(i).switchTo(OnProxyOnlineOfflineSwitchListener.State_Online);
                previousStatusOfIsOnline = true;
            }
        } else if (response.header(IndicatorNameInResponseHeader) != null
                && response.header(IndicatorNameInResponseHeader).equalsIgnoreCase("false")) {
            if (previousStatusOfIsOnline) {
                for (int i = 0; i < listeners.size(); i++)
                    listeners.get(i).switchTo(OnProxyOnlineOfflineSwitchListener.State_Offline);
                previousStatusOfIsOnline = false;
            }
        }

        return response;
    }

//    public static Request appendAuthorizationHeader(Request.Builder requestBuilder, String authToken) {
//        fileLogger.trace("AddAuthToHeaderInterceptor, starting appendAuthorizationHeader()");
//        Request.Builder builder = requestBuilder
//                .header("Authorization", authToken);
//        Request request = builder.build();
//        fileLogger.trace("AddAuthToHeaderInterceptor, done appendAuthorizationHeader()");
//        return request;
//    }

    public static void addOnProxyOnlineOfflineSwitchListener(OnProxyOnlineOfflineSwitchListener l) {
        listeners.add(l);
    }
}
