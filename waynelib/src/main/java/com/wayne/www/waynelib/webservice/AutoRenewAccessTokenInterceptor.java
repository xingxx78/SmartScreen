package com.wayne.www.waynelib.webservice;

import android.util.Log;

import com.wayne.www.waynelib.webservice.entity.AccessToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shawn on 6/1/2017.
 */

public class AutoRenewAccessTokenInterceptor implements Interceptor {
    private static String LOG_TAG = "CustomIntercept";
    private Logger fileLogger = LoggerFactory.getLogger(AutoRenewAccessTokenInterceptor.class);
    private static final int BLOCKING_RESPONSE_CODE = 401;

    @Override
    public Response intercept(Chain chain) throws IOException {
        fileLogger.trace("AutoRenewAccessTokenInterceptor, starting intercept()");
        Request request = chain.request();

        // try the request
        Response response = chain.proceed(request);

        // 401 indicates un-authorized, may never applying accessToken before, or previous accessToken expired,
        // start renew here.
        if (response != null && response.code() == BLOCKING_RESPONSE_CODE) {
            fileLogger.info("Intercepted a Request failed with response code " + response.code() + ", will renewToken...");
            AccessToken newToken = CloudServiceGenerator.RenewAccessToken();

            // retry the request if newToken retrieved, otherwise stop
            if (newToken != null && !newToken.isRejectedByServer()) {
                fileLogger.info("RenewAccessToken succeed and re-injecting to following Request...");
                request = AddAuthToHeaderInterceptor.appendAuthorizationHeader(request.newBuilder(), "bearer " + newToken.getAccessToken());
                response = chain.proceed(request);
            } else
                fileLogger.error("RenewAccessToken failed, will return the original failed http response");
        }
        fileLogger.trace("AutoRenewAccessTokenInterceptor, done intercept()");
        // otherwise just pass the original response on
        return response;
    }
}
