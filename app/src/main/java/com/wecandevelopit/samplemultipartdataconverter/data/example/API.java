package com.wecandevelopit.samplemultipartdataconverter.data.example;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface API {

    @Multipart
    @POST("foo")
    Call<Object> testRequest(
            @PartMap() Map<String, RequestBody> partMap,
            @Part List<MultipartBody.Part> files);
}
