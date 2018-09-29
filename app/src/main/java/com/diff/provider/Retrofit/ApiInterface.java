package com.diff.provider.Retrofit;

import com.diff.provider.Models.CreditHistoryResponse;
import com.diff.provider.Models.GetBankResponse;
import com.diff.provider.Models.Response;
import com.diff.provider.Models.WalletHistoryResponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/api/provider/trip/{id}/calculate")
    Call<ResponseBody> getLiveTracking(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                       @Path("id") String id,
                                       @Field("latitude") String latitude, @Field("longitude") String longitude);


    @POST("/api/provider/trip/{id}")
    Call<ResponseBody> acceptAPI(@Path("id") String id, @Header("X-Requested-With") String xmlRequest,
                                 @Header("Authorization") String accesskey);

    @DELETE("/api/provider/trip/{id}")
    Call<ResponseBody> rejectAPI(@Path("id") String id, @Header("X-Requested-With") String xmlRequest,
                                 @Header("Authorization") String accesskey);

    @GET("/api/provider/bank/list")
    Call<GetBankResponse> getBank(@Header("X-Requested-With") String xmlRequest,
                                  @Header("Authorization") String accesskey);


    @FormUrlEncoded
    @POST("/api/provider/withdraw/wallet")
    Call<Response> withdrawWallet(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                  @FieldMap HashMap<String, Object> params);


    @FormUrlEncoded
    @POST("/api/provider/credit/add")
    Call<Response> reloadWallet(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                  @FieldMap HashMap<String, Object> params);

    @GET("/api/provider/wallet/history")
    Call<WalletHistoryResponse> walletHistory(@Header("X-Requested-With") String xmlRequest,
                                              @Header("Authorization") String accesskey);

    @GET("/api/provider/credit/history")
    Call<CreditHistoryResponse> creditHistory(@Header("X-Requested-With") String xmlRequest,
                                                       @Header("Authorization") String accesskey);
}
