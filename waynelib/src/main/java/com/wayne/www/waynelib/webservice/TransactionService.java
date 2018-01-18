package com.wayne.www.waynelib.webservice;

import com.wayne.www.waynelib.webservice.entity.ClientPosTrx;
import com.wayne.www.waynelib.webservice.entity.ClientRingUpPosItem;
import com.wayne.www.waynelib.webservice.entity.PosTrx;
import com.wayne.www.waynelib.webservice.entity.PosTrxMop;
import com.wayne.www.waynelib.webservice.entity.ShiftInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Shawn on 5/31/2017.
 */

public interface TransactionService {
    @POST("/api/transactions/")
    @Headers("Content-Type: application/json")
    Call<PosTrx> createTransaction(@Body ClientPosTrx trx);

    @POST("/api/transactions/{trxid}/products")
    @Headers("Content-Type: application/json")
    Call<PosTrx> RingupPosItemsToTransaction(@Path("trxid") String trxId, @Body ClientRingUpPosItem[] posTrxItems);

    @POST("/api/transactions/{trxid}/payment")
    @Headers("Content-Type: application/json")
    Call<Void> FinalizeTransaction(@Path("trxid") String trxId, @Body PosTrxMop posTrxMop);

    /**
     * Get a bunch of trx which paged in server side by determine the page count and page number.
     *
     * @param pageCount
     * @param pageNumber 0-based.
     * @return
     */
    @GET("/api/transactions/?searchType=paging")
    @Headers("Content-Type: application/json")
    Call<PosTrx[]> getTransactions(@Query("isDescending") boolean isDescending, @Query("pageCount") int pageCount, @Query("pageNumber") int pageNumber);

    /**
     * @param isDescending
     * @param fromDateStr  must be format: yyyy-MM-dd
     * @param toDateStr    must be format: yyyy-MM-dd
     * @param pageCount
     * @param pageNumber   0-based
     * @return
     */
    @GET("/api/transactions/?searchType=date")
    @Headers("Content-Type: application/json")
    Call<PosTrx[]> getTransactions(@Query("isDescending") boolean isDescending,
                                   @Query("fromDateStr") String fromDateStr,
                                   @Query("toDateStr") String toDateStr,
                                   @Query("pageCount") int pageCount,
                                   @Query("pageNumber") int pageNumber);

    @GET("/api/transactions/?searchType=id&searchValue0=''")
    @Headers("Content-Type: application/json")
    Call<PosTrx[]> getPosTrxById(@Query("searchValue1") String uniqueGuid);

    /**
     * Get all trx with its receiptId similar to the input param.  like input 12345, the trx with receipt Id
     * A12345, 12345B, AB12345, 12345DE will all be returned.
     *
     * @param receiptId could be part of a receiptId, the server side will search with `sql like` expression
     * @return all trx with its receiptId similar to the param.
     */
    @GET("/api/transactions/?searchType=receiptId&searchValue0=''")
    @Headers("Content-Type: application/json")
    Call<PosTrx[]> getPosTrxByReceiptId(@Query("searchValue1") String receiptId);

    @GET("/api/transactions/?searchType=lastOpenSaleTrx&searchValue0=userName")
    @Headers("Content-Type: application/json")
    Call<PosTrx> getLastOpenTrxByUserName(@Query("searchValue1") String userName);


    @GET("/api/transactions/shiftinfo")
    @Headers("Content-Type: application/json")
    Call<ShiftInfo[]> getShiftInfo(@Query("isDescending") boolean isDescending,
                                   @Query("pageCount") int pageCount,
                                   @Query("pageNumber") int pageNumber);

    @POST("/api/transactions/refund")
    @Headers("Content-Type: application/json")
    Call<PosTrxMop> refundTransaction(@Body String posTrxUniqueId);
}
