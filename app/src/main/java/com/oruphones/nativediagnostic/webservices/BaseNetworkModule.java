package com.oruphones.nativediagnostic.webservices;


import androidx.annotation.NonNull;


import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseNetworkModule {

    private static String TAG = BaseNetworkModule.class.getSimpleName();
    private BaseApiInterface mBaseApiInterface;

    public void init(String url, String key) {
        mBaseApiInterface = APIClient.getClient(url, key).create(BaseApiInterface.class);
    }
    public void init(String url) {
        mBaseApiInterface = APIClient.getClient(url).create(BaseApiInterface.class);
    }


    //Upload logs
    private MultipartBody.Part createPart(@NonNull File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    }






    public void uploadLogs(final File file, String sessionId) {
        if (file == null || !file.exists())
            return;

        if (mBaseApiInterface != null)
            mBaseApiInterface.uploadLogs(RequestBody.create(MultipartBody.FORM, sessionId), createPart(file)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (file.delete()) {
                        DLog.d(TAG, "Logs uploaded  & File deleted successfully " + response);
                    } else {
                        DLog.d(TAG, "Logs uploaded & File not deleted" + response);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    DLog.d(TAG, "Logs not uploaded but have error :  " + t.getMessage());
                    t.printStackTrace();
                }
            });
    }





}
