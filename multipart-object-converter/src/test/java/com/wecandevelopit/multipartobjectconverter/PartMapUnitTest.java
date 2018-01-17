package com.wecandevelopit.multipartobjectconverter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.wecandevelopit.multipartobjectconverter.data.ChildObject;
import com.wecandevelopit.multipartobjectconverter.data.RequestObject;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import okhttp3.RequestBody;

import static org.junit.Assert.assertEquals;

public class PartMapUnitTest {

    private MultipartObjectConverter converter;

    @Before
    public void setUp() throws Exception {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
        converter = new MultipartObjectConverter.Builder()
                .setGsonBuilder(gsonBuilder)
                .setIncludeFilesOfInnerClasses(true)
                .setIncludeNotAnnotatedFiles(false)
                .setMaxFileSearchDepth(3)
                .create();
    }


    @Test
    public void serialization_isCorrect() throws Exception {
        RequestObject requestObject = new RequestObject("example",
                "test",
                null);

        Map<String, RequestBody> partMap = converter.convertToPartMap(requestObject);

        assertEquals(1, partMap.size());
        assertEquals("property-two", partMap.keySet().toArray()[0]);

        ChildObject childObject = new ChildObject();
        childObject.setChildFieldOne("bla");
        childObject.setChildFieldTwo(10.4);
        requestObject.setChildObject(childObject);

        partMap = converter.convertToPartMap(requestObject);

        assertEquals(2, partMap.size());

        converter.setSerializeComplexObjectsToJson(false);
        partMap = converter.convertToPartMap(requestObject);
        converter.setSerializeComplexObjectsToJson(false);
        assertEquals(1, partMap.size());
    }
}