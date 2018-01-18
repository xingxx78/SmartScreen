package com.wayne.www.waynelib.webservice;

import android.net.Proxy;
import android.text.TextUtils;
import android.util.Log;

import com.wayne.www.waynelib.webservice.entity.AccessToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Shawn on 5/31/2017.
 */

public class CloudServiceGenerator {
    private static Logger fileLogger = LoggerFactory.getLogger(CloudServiceGenerator.class);
    private static String LOG_TAG = "CloudServiceGenerator";
    private static String API_BASE_URL = "http://1.1.1.1";
    static HttpLoggingInterceptor logging;

    private static OkHttpClient.Builder httpClient;
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
    private static String userName;
    private static String userPassword;
    public static Retrofit retrofit;

    private static ArrayList<OnApplyAccessTokenFailedListener> accessTokenFailedListeners
            = new ArrayList<>();

    public static void addOnApplyAccessTokenFailedListener(OnApplyAccessTokenFailedListener l) {
        accessTokenFailedListeners.add(l);
    }

    public static void removeOnApplyAccessTokenFailedListener(OnApplyAccessTokenFailedListener l) {
        accessTokenFailedListeners.remove(l);
    }

    /**
     * change the service endpoint address to switch service.
     *
     * @param fullUrl must start with http or https, e.g.:http://1.1.1.1:80
     */
    public static void setApiBaseUrl(String fullUrl) {
        if (API_BASE_URL.equals(fullUrl)) return;
        API_BASE_URL = fullUrl;
        builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
    }

    public static String getApiBaseUrl() {
        return API_BASE_URL;
    }

    /**
     * set the user name and password which be used in following apply the access token.
     *
     * @param name
     * @param pwd
     */
    public static void setUserNameAndPassword(String name, String pwd) {
        userName = name;
        userPassword = pwd;
    }

    static {
        logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(40_000, TimeUnit.MILLISECONDS);
        httpClient.addInterceptor(logging);

//        SocketAddress proxyServerAddress = new InetSocketAddress("10.0.2.2", 6666);
//        java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, proxyServerAddress);
//        httpClient.proxy(proxy);
    }

    interface AccessTokenService {
        @FormUrlEncoded
        @POST("/token")
        @Headers("Content-Type: application/json")
        Call<AccessToken> getAccessToken(
                @Field("username") String userName,
                @Field("password") String passWord,
                @Field("grant_type") String grantType);
    }

    private static AccessToken accessToken;

    public static AccessToken getUserGenericInfo() {
        return accessToken;
    }

    /**
     * start retrieving AccessToken from auth service with userName and password which
     * pass in via function setUserNameAndPassword(..., ...), it has a build-in auto-retry
     * for connection layer error.
     *
     * @return null for connection layer error, otherwise an AccessToken object which need further
     * detect the inner state.
     */
    public static AccessToken RenewAccessToken() {
        fileLogger.info("start RenewAccessToken...");
        if (!httpClient.interceptors().contains(addDeviceInfoToHeaderInterceptor)) {
            fileLogger.warn("adding addDeviceInfoToHeaderInterceptor, httpClient.interceptors() count: " + httpClient.interceptors().size());
            httpClient.addInterceptor(addDeviceInfoToHeaderInterceptor);
        }

        Retrofit tempRetrofit =
                new Retrofit.Builder()
                        .baseUrl(CloudServiceAddressBook.AuthServiceAddress)
                        .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Call<AccessToken> call;
        fileLogger.trace("start RenewAccessToken, step 0");
//        if (accessTokenGrantType.length > 0)
//            call = tempRetrofit.create(AccessTokenService.class).getAccessToken(userName, userPassword, accessTokenGrantType[0]);
//        else
        call = tempRetrofit.create(AccessTokenService.class).getAccessToken(userName, userPassword, "password");
        fileLogger.trace("start RenewAccessToken, step 1");
        int maxRetryTimes = 0;
        int triedTimes = 0;
        // connection layer error will pop up here
        Response<AccessToken> response = null;
        try {
            while (triedTimes <= maxRetryTimes) {
                try {
                    fileLogger.trace("start RenewAccessToken, step 2");
                    response = call.clone().execute();
                    fileLogger.trace("start RenewAccessToken, step 3");
                    break;
                } catch (Exception eeex) {
                    if (triedTimes >= maxRetryTimes)
                        throw new Exception(eeex);
                    fileLogger.error("RenewAccessToken failed in execute(): " + eeex.getMessage() + ", will retry...");
                    triedTimes++;
                }
            }
        } catch (Exception ex) {
            fileLogger.error("RenewAccessToken failed in execute() and maxRetry times reached.");
            for (OnApplyAccessTokenFailedListener l :
                    accessTokenFailedListeners) {
                l.OnError(ex);
            }

            return null;
        }


        AccessToken errorAccessToken = null;
        if (response.code() != 200) {
            fileLogger.info("RenewAccessToken processed with response code: " + response.code() + ", abort");
            try {
                if (response.errorBody().contentLength() == 0) {
                    errorAccessToken = new AccessToken();
                    errorAccessToken.setIsRejectedByServer("UnknownError", "NoErrorBody");
                    throw new IllegalArgumentException();
                }

                errorAccessToken = (AccessToken) tempRetrofit.responseBodyConverter(
                        AccessToken.class, AccessToken.class.getAnnotations())
                        .convert(response.errorBody());
                throw new HttpException(response);
            } catch (Exception ex) {
                for (OnApplyAccessTokenFailedListener l :
                        accessTokenFailedListeners) {
                    l.OnError(new HttpException(response));
                }

                return errorAccessToken;
            }
        } else {
            accessToken = response.body();
            fileLogger.info("RenewAccessToken is done with success");
            return accessToken;
        }
    }

    /**
     * create the service with automatically put in the authorization header.
     *
     * @param serviceClass
     * @param <S>
     * @return
     */

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, accessToken.getAccessToken());
    }

    public static <S> S createService(
            Class<S> serviceClass, String clientId, String clientSecret) {
        if (!TextUtils.isEmpty(clientId)
                && !TextUtils.isEmpty(clientSecret)) {
            String authToken = Credentials.basic(clientId, clientSecret);
            return createService(serviceClass, authToken);
        }

        return createService(serviceClass, null, null);
    }

    private static AddAuthToHeaderInterceptor addAuthToHeaderInterceptor = new AddAuthToHeaderInterceptor();
    private static AddDeviceInfoToHeaderInterceptor addDeviceInfoToHeaderInterceptor = new AddDeviceInfoToHeaderInterceptor();
    private static DetectProxyOnlineOfflineSwitchInterceptor detectProxyOnlineOfflineSwitchInterceptor = new DetectProxyOnlineOfflineSwitchInterceptor();
    private static AutoRenewAccessTokenInterceptor renewAccessTokenInterceptor =
            new AutoRenewAccessTokenInterceptor();

    private static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            addAuthToHeaderInterceptor.setAuthToken("bearer " + authToken);
            if (!httpClient.interceptors().contains(addAuthToHeaderInterceptor)) {
                fileLogger.warn("adding AddAuthToHeaderInterceptor, httpClient.interceptors() count: " + httpClient.interceptors().size());
                httpClient.addInterceptor(addAuthToHeaderInterceptor);
            }
        }

        if (!httpClient.interceptors().contains(renewAccessTokenInterceptor)) {
            fileLogger.warn("adding AutoRenewAccessTokenInterceptor, httpClient.interceptors() count: " + httpClient.interceptors().size());
            httpClient.addInterceptor(renewAccessTokenInterceptor);
        }

        if (!httpClient.interceptors().contains(addDeviceInfoToHeaderInterceptor)) {
            fileLogger.warn("adding addDeviceInfoToHeaderInterceptor, httpClient.interceptors() count: " + httpClient.interceptors().size());
            httpClient.addInterceptor(addDeviceInfoToHeaderInterceptor);
        }

        if (!httpClient.interceptors().contains(detectProxyOnlineOfflineSwitchInterceptor)) {
            fileLogger.warn("adding detectProxyOnlineOfflineSwitchInterceptor, httpClient.interceptors() count: " + httpClient.interceptors().size());
            httpClient.addInterceptor(detectProxyOnlineOfflineSwitchInterceptor);
        }

        retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
