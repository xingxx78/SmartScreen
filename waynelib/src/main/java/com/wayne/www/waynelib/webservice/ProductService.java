package com.wayne.www.waynelib.webservice;

import com.wayne.www.waynelib.webservice.entity.FuelPriceChangeItem;
import com.wayne.www.waynelib.webservice.entity.PosItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Body;

/**
 * Created by Shawn on 5/31/2017.
 */

public interface ProductService {
    @GET("api/products/popular")
    @Headers("Content-Type: application/json")
    Call<PosItem[]> getPopularPosItems();

    @GET("api/products/{id}")
    @Headers("Content-Type: application/json")
    Call<PosItem> getPosItemById(@Path("id") int id);

    @GET("api/products/itemId/{itemId}")
    @Headers("Content-Type: application/json")
    Call<PosItem> getPosItemByItemId(@Path("itemId") String itemId);

    @GET("api/products/barcode/{barcode}")
    @Headers("Content-Type: application/json")
    Call<PosItem> getLatestPosItemByBarCode(@Path("barcode") String barcode);

    @POST("/api/products/fuelgrades")
    @Headers("Content-Type: application/json")
    Call<PosItem> sendFuelPriceChange(@Body PosItem fuelPriceItem);
}
