package com.wayne.www.waynelib.webservice;

import com.wayne.www.waynelib.webservice.entity.PosItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Shawn on 5/31/2017.
 */

public interface ReceiptService {
    @GET("/api/receipt/{posTrxId}/maxChar/{receiptPaperMaxCharPerLine}")
    @Headers("Content-Type: application/json")
    Call<String> getReceiptByPosTrxId(@Path("posTrxId") String posTrxId, @Path("receiptPaperMaxCharPerLine") int receiptPaperMaxCharPerLine);

    @GET("/api/shiftReceipt/{shiftId}/maxChar/{receiptPaperMaxCharPerLine}")
    @Headers("Content-Type: application/json")
    Call<String> getShiftReceiptByShiftId(@Path("shiftId") int shiftId, @Path("receiptPaperMaxCharPerLine") int receiptPaperMaxCharPerLine);

}
