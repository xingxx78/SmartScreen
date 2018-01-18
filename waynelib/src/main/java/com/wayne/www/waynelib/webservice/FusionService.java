package com.wayne.www.waynelib.webservice;
//import com.wayne.www.waynelib.webservice.entity.PosItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by taylor.xie on 12/7/2017.
 */

public interface FusionService {
    @GET("api/TransactionService/commitPayment/{amount}")
    @Headers("Content-Type: application/json")
    Call<String> CommitPayment(@Path("amount")  float amount);

    /*@GET("api/products/{id}")
    @Headers("Content-Type: application/json")
    Call<PosItem> getPosItemById(@Path("id") int id);

    @GET("api/products/itemId/{itemId}")
    @Headers("Content-Type: application/json")
    Call<PosItem> getPosItemByItemId(@Path("itemId") String itemId);

    @GET("api/products/barcode/{barcode}")
    @Headers("Content-Type: application/json")
    Call<PosItem> getLatestPosItemByBarCode(@Path("barcode") String barcode);*/
}
