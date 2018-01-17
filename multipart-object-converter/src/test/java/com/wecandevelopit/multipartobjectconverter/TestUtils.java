package com.wecandevelopit.multipartobjectconverter;

import com.wecandevelopit.multipartobjectconverter.data.ChildObject;
import com.wecandevelopit.multipartobjectconverter.data.ChildOfChildObject;
import com.wecandevelopit.multipartobjectconverter.data.RequestObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by evgeek on 1/16/18.
 */

public class TestUtils {

    public static RequestObject createDefaultRequestObject() throws IOException {
        File fileOne = File.createTempFile("file", "One");
        File fileTwo = File.createTempFile("file", "Two");
        File fileThree = File.createTempFile("file", "Three");

        PrintWriter pw = new PrintWriter(fileOne);
        pw.write("first file");
        pw.close();
        pw = new PrintWriter(fileTwo);
        pw.write("second file");
        pw.close();
        pw = new PrintWriter(fileThree);
        pw.write("third file");
        pw.close();
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
}
