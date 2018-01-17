package com.wecandevelopit.multipartobjectconverter;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.GsonBuilder;
import com.wecandevelopit.multipartobjectconverter.data.RequestObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import okhttp3.MultipartBody;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, MimeTypeMap.class})
public class FileUnitTest {

    private MultipartObjectConverter converter;

    @Before
    public void setUp() throws Exception {
        GsonBuilder gsonBuilder = new GsonBuilder();
        converter = new MultipartObjectConverter.Builder()
                .setMaxFileSearchDepth(3)
                .create();
    }

    @Test
    public void regularFilesTest() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(MimeTypeMap.class);

        MimeTypeMap mtmap = PowerMockito.mock(MimeTypeMap.class);

        when(MimeTypeMap.getFileExtensionFromUrl(any(String.class))).thenReturn("");
        when(MimeTypeMap.getSingleton()).thenReturn(mtmap);
        when(MimeTypeMap.getSingleton().getMimeTypeFromExtension((any(String.class)))).thenReturn("text/plain");

        RequestObject requestObject = TestUtils.createDefaultRequestObject();
        List<MultipartBody.Part> parts = converter.convertToMultipartFiles(requestObject);

        assertEquals(4, parts.size());

        converter.setIncludeFilesOfInnerClasses(false);
        parts = converter.convertToMultipartFiles(requestObject);
        converter.setIncludeFilesOfInnerClasses(true);
        assertEquals(2, parts.size());

        converter.setIncludeNotAnnotatedFiles(true);
        parts = converter.convertToMultipartFiles(requestObject);
        converter.setIncludeNotAnnotatedFiles(false);
        assertEquals(5, parts.size());

        converter.setMaxFileSearchDepth(2);
        parts = converter.convertToMultipartFiles(requestObject);
        converter.setMaxFileSearchDepth(3);
        assertEquals(3, parts.size());
    }
}