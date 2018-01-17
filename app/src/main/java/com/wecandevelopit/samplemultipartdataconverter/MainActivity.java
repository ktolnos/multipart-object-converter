package com.wecandevelopit.samplemultipartdataconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.wecandevelopit.multipartobjectconverter.MultipartObjectConverter;
import com.wecandevelopit.samplemultipartdataconverter.data.example.API;
import com.wecandevelopit.samplemultipartdataconverter.data.example.ChildObject;
import com.wecandevelopit.samplemultipartdataconverter.data.example.ChildOfChildObject;
import com.wecandevelopit.samplemultipartdataconverter.data.example.RequestObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GsonBuilder gsonBuilder = new GsonBuilder() // gson is used as serializer
                .setDateFormat("yyyy:MM:dd")        // so you can customize most serialization properties here
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        final MultipartObjectConverter converter = new MultipartObjectConverter.Builder()
                .setGsonBuilder(gsonBuilder)
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(this))  //chuck interceptor is used to see the result
                .build();                                            //we don't need real server this way
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create())) //use of the same builder is not required but seems to be logical
                .baseUrl(Constants.url).build();
        final API api = retrofit.create(API.class);

        Button uploadButton = findViewById(R.id.btn_upload_data);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestObject requestObject = createExampleRequestObject();

                api.testRequest(converter.convertToPartMap(requestObject),
                        converter.convertToMultipartFiles(requestObject))
                        .enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                Log.d(Constants.tag, "Response!");
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                Log.d(Constants.tag, "Failure!");
                            }
                        });
                Log.d(Constants.tag, "Uploading data");
            }
        });
    }

    RequestObject createExampleRequestObject() {
        File fileOne = new File(getCacheDir(), "fileOne.png");
        File fileTwo = new File(getCacheDir(), "fileTwo");
        File fileThree = new File(getCacheDir(), "fileThree");
        try {
            copyInputStreamToFile(
                    getAssets().open("ExampleImage.png"), fileOne);

            copyInputStreamToFile(
                    getAssets().open("ExampleFile.txt"), fileTwo);

            copyInputStreamToFile(
                    getAssets().open("ExampleImage.png"), fileThree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestObject requestObject = new RequestObject("example",
                "test",
                fileOne);
        requestObject.setFileTwo(fileTwo);
        requestObject.setPathToFileThree(fileThree.getAbsolutePath());

        ChildObject childObject = new ChildObject();
        childObject.setChildFieldOne("foo");
        childObject.setChildFieldTwo(4.815162342);
        childObject.setPathToChildFile(fileTwo.getAbsolutePath());

        ChildOfChildObject childOfChildObject = new ChildOfChildObject();
        childOfChildObject.setChildOfChildFieldOne("bar");
        childOfChildObject.setChildOfChildFieldTwo("kek");
        childOfChildObject.setChildOfChildFile(fileTwo);

        childObject.setChildOfChildObject(childOfChildObject);

        requestObject.setChildObject(childObject);

        return requestObject;
    }

    // Copy an InputStream to a File.
//
    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if (out != null) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
