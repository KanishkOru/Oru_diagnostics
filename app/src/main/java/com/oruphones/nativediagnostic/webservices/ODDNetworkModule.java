package com.oruphones.nativediagnostic.webservices;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.ReportAProblem;
import com.oruphones.nativediagnostic.models.CSATData;
import com.oruphones.nativediagnostic.models.ResponseDto;
import com.oruphones.nativediagnostic.models.StoreConfig;
import com.oruphones.nativediagnostic.models.history.HistoryRequestDto;
import com.oruphones.nativediagnostic.models.history.HistoryResponseDto;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ODDNetworkModule {

    private static String TAG = ODDNetworkModule.class.getSimpleName();
    private static ODDNetworkModule oddNetworkModule;
    private ODDApiInterface mODDApiInterface;
    private ODDApiInterface centralServerApiInterface ;

    public static ODDNetworkModule getInstance() {
        if (oddNetworkModule == null) {
            oddNetworkModule = new ODDNetworkModule();
        }
        return oddNetworkModule;
    }

    public void init(String url, String key) {
        mODDApiInterface = APIClient.getClient(url).create(ODDApiInterface.class);
    }
    public void init(String url) {
        mODDApiInterface = APIClient.getClient(url).create(ODDApiInterface.class);
    }

    public ODDApiInterface getDiagServerApiInterface() {
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        if(mODDApiInterface == null && !TextUtils.isEmpty(globalConfig.getServerUrl())) {
            mODDApiInterface = APIClient.getClient(globalConfig.getServerUrl()).create(ODDApiInterface.class);
        }
        return mODDApiInterface;
    }

    public ODDApiInterface getCentralServerApiInterface() {
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        if(centralServerApiInterface == null && !TextUtils.isEmpty(globalConfig.getCentralUrl())) {
            centralServerApiInterface = APIClient.getClient(globalConfig.getCentralUrl()).create(ODDApiInterface.class);
        }
        return centralServerApiInterface;
    }

    public static ODDApiInterface getNewApiInterface(String serverUrl, String key){
        if(!TextUtils.isEmpty(serverUrl) && !TextUtils.isEmpty(key)){
            return APIClient.getClient(serverUrl).create(ODDApiInterface.class);
        }
        return null;
    }

    public void getHistoryDetails(HistoryRequestDto historyDto, final NetworkResponseListener<ResponseDto<List<HistoryResponseDto>>> listener) {
        ODDApiInterface diagApiInterface = getDiagServerApiInterface();
        if (diagApiInterface != null)
            diagApiInterface.getDiagnosticsHistory(historyDto).enqueue(new Callback<ResponseDto<List<HistoryResponseDto>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<HistoryResponseDto>>> call, Response<ResponseDto<List<HistoryResponseDto>>> response) {

                if(response.isSuccessful() && response.body() !=null){
                    listener.onResponseReceived(response.body());
                }else{
                    listener.onError();
                }

            }

            @Override
            public void onFailure(Call<ResponseDto<List<HistoryResponseDto>>> call, Throwable t) {
                listener.onError();
            }
        });
    }


    public void getCCStoreInfo(String pinOrStoreId, final NetworkResponseListener<StoreConfig> listener) {
        ODDApiInterface centralApiInterface = getCentralServerApiInterface();
        if (centralApiInterface != null)
            centralApiInterface.getStoreConfig(pinOrStoreId).enqueue(new Callback<StoreConfig>() {
                @Override
                public void onResponse(Call<StoreConfig> call, Response<StoreConfig> response) {
                    if(response.isSuccessful() && response.body() !=null){
                        listener.onResponseReceived(response.body());
                    }else{
                        listener.onError();
                    }
                }
                @Override
                public void onFailure(Call<StoreConfig> call, Throwable t) {
                    listener.onError();
                }
            });
    }


    public class NetworkResponse<ResponseType> implements Callback<ResponseType> {

        private NetworkResponseListener<ResponseType> listener;

        public NetworkResponse(NetworkResponseListener<ResponseType> listener) {
            this.listener = listener;
        }

        @Override
        public void onResponse(@NonNull Call<ResponseType> call, @NonNull Response<ResponseType> response) {
            if (listener != null ) {
                listener.onResponseReceived(response.body());
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseType> call, @NonNull Throwable t) {
            DLog.e(TAG, " onFailure" + t.getMessage());
            if (listener != null) {
                listener.onError();
            }
        }
    }

    public void callStoreIdConfig(final NetworkResponseListener listener, String storeId, String product) {
        ODDApiInterface centralApiInterface = getCentralServerApiInterface();
        if (centralApiInterface != null)
            centralApiInterface.getStoreInfo(storeId, product).enqueue(new NetworkResponse(listener));
    }

        public void uploadLogFile(File file, FileUploadResposnseListener listener) {


//          if (file.exists())
//          {
//              DLog.d("LOG_FILE_UPLOAD",file.getName());
//              DLog.d("LOG_FILE_UPLOAD",DLog.retrieveLogsLocally(file.getName()));
//
//          }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalConfig.getInstance().getDevBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ODDApiInterface oddApiInterface = retrofit.create(ODDApiInterface.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
            MultipartBody.Part logFile = MultipartBody.Part.createFormData("logFile", file.getName(),requestFile);

            Call<ResponseBody> call = oddApiInterface.uploadFile(logFile,file.getName());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Log the full server response
                    DLog.d("LOG_FILE_UPLOAD", "Server Response Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            DLog.d("LOG_FILE_UPLOAD", "Server Response Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Handle successful response
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            DLog.d("LOG_FILE_UPLOAD", "LOG FILE UPLOADED SUCCESSFULLY");
                            DLog.d("LOG_FILE_UPLOAD", "Response Body: " + responseBody);

                            // Now you have the response body as a JSON string (responseBody)
                            // Parse it to a JSONObject
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            // Check if the "data" object is not empty
                            if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                                // Handle the data object
                                JSONObject dataObject = jsonResponse.getJSONObject("data");
                                // Now you can access filePath and fileKey
                                String filePath = dataObject.getString("filePath");
                                String fileKey = dataObject.getString("fileKey");

                                // Check if filePath and fileKey are not empty
                                if (!filePath.isEmpty() && !fileKey.isEmpty()) {
                                    DLog.w("LOG_FILE_UPLOAD", "BOOM");
                                } else {
                                    DLog.e("LOG_FILE_UPLOAD", "LOG FILE UPLOAD FAILED: filePath or fileKey is empty");
                                    listener.onError();
                                }
                            } else {
                                DLog.e("LOG_FILE_UPLOAD", "LOG FILE UPLOAD FAILED: Data object is empty");
                                listener.onError();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            listener.onError();
                        }
                    } else {
                        DLog.e("LOG_FILE_UPLOAD", "LOG FILE UPLOAD FAILED: " + response.message());
                        listener.onError();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listener.onError();
                }
            });
        }

    /*Report A Problem */
    public void reportProblem(ReportAProblem feedBackData, final NetworkResponseListener<ResponseBody> listener) {
        ODDApiInterface diagApiInterface = getDiagServerApiInterface();
        if (diagApiInterface != null)
            diagApiInterface.reportProblem(feedBackData).enqueue(new NetworkResponse<>(listener));
    }

    public void generateRAN(final NetworkResponseListener<String> listener) {
        ODDApiInterface diagApiInterface = getDiagServerApiInterface();
        if (diagApiInterface != null)
            diagApiInterface.getRAN().enqueue(new NetworkResponse<>(listener));
    }

    public void submitCSAT(CSATData csatData, final NetworkResponseListener<JsonObject> listener) {
        ODDApiInterface diagApiInterface = getDiagServerApiInterface();
        if (diagApiInterface != null)
            diagApiInterface.sendCSATFeedback(csatData).enqueue(new NetworkResponse<>(listener));
    }

    public void fetchImeiStatus(String imei, final NetworkResponseListener listener) {
        ODDApiInterface diagApiInterface = getDiagServerApiInterface();
        if (diagApiInterface != null)
            diagApiInterface.getIMEIStatus(imei).enqueue(new NetworkResponse(listener));
    }
}
