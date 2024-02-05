package com.oruphones.nativediagnostic.webservices;

import com.google.gson.JsonObject;
import com.oruphones.nativediagnostic.api.AppDetails;
import com.oruphones.nativediagnostic.api.ReportAProblem;
import com.oruphones.nativediagnostic.api.ServerConfig;
import com.oruphones.nativediagnostic.api.StoreIdConfig;
import com.oruphones.nativediagnostic.api.TransactionResponse;
import com.oruphones.nativediagnostic.communication.api.SummaryType;
import com.oruphones.nativediagnostic.communication.api.WebrootAppsDetails;
import com.oruphones.nativediagnostic.models.CSATData;
import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.models.ImeiStatusResponce;
import com.oruphones.nativediagnostic.models.LogTransactionResp;
import com.oruphones.nativediagnostic.models.PDDiagLogging;
import com.oruphones.nativediagnostic.models.ResponseDto;
import com.oruphones.nativediagnostic.models.StoreConfig;
import com.oruphones.nativediagnostic.models.history.HistoryRequestDto;
import com.oruphones.nativediagnostic.models.history.HistoryResponseDto;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ODDApiInterface {

    @Multipart
    @POST("uploadLogFile")
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part logFile, @Query("fileName") String filename);

    @POST("api/device/getDiagnosticsHistory")
    Call<ResponseDto<List<HistoryResponseDto>>> getDiagnosticsHistory(@Body HistoryRequestDto historyRequestDto);

    @GET("/pervacioappservices/api/cacheservice/data/getFromCache/{storeId}")
    Call<StoreConfig> getStoreConfig(@Path("storeId") String storeId);

    /*SSD APIs*/
    @POST("api/diagConfig")
    Call<ServerConfig> getDeviceConfig(@Body DeviceInformation deviceInformation);

    @POST("/api/device/getAppDetails")
    Call<List<AppDetails>> getAppDetailsFromWebroot(@Body WebrootAppsDetails webrootAppsDetails);

    @POST("api/logDiagTransaction")
    Call<LogTransactionResp> logTransaction(@Body PDDiagLogging data);


    @POST("/api/device/pushSummary1")
    Call<TransactionResponse> getSummaryImage(@Body SummaryType data);

   /* @POST("/api/device/getDiagnosticsHistory")
    Call<ResponseDto<List<HistoryResponseDto>>> getDiagnosticsHistory(@Body HistoryRequestDto firmwareData);

    @GET("/pervaciocacheservice/api/cacheservice/data/getFromCache/{storeId}")
    Call<StoreIdConfig> getStoreConfig(@Path("storeId") String storeId);*/

    @GET("/pervacioappservices/api/data/getStoreInfo/{storeId}/{product}")
    Call<StoreIdConfig> getStoreInfo(@Path("storeId") String storeId, @Path("product") String product);

    @POST("/api/device/emailSummaryPDF")
    Call<TransactionResponse> sendSummaryEmail(@Body SummaryType data);


    @POST("/api/device/reportAProblem")
    Call<ResponseBody> reportProblem(@Body ReportAProblem problem);

    @GET("/api/device/generateRAN")
    Call<String> getRAN();

    @POST("/api/device/logCSAT")
    Call<JsonObject> sendCSATFeedback(@Body CSATData data);

    @POST("/api/device/getIMEIStolenStatus")
    Call<ImeiStatusResponce> getIMEIStatus(@Query("imei") String imei);



}
