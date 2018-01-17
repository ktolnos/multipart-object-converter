package com.wecandevelopit.samplemultipartdataconverter.data.example;

import com.wecandevelopit.multipartobjectconverter.ExcludeMultipart;
import com.wecandevelopit.multipartobjectconverter.MultipartFile;

import java.io.File;

/**
 * Created by evgeek on 1/14/18.
 */

public class RequestObject {
    @ExcludeMultipart
    private String propertyOne;
    private String propertyTwo;
    @MultipartFile
    private File fileOne;
    @MultipartFile("text/plain")
    private File fileTwo;
    @MultipartFile("image/png")
    private String pathToThirdFile;
    private ChildObject childObject;

    public RequestObject(String propertyOne, String propertyTwo, File fileOne) {
        this.propertyOne = propertyOne;
        this.propertyTwo = propertyTwo;
        this.fileOne = fileOne;
    }

    public String getPropertyOne() {
        return propertyOne;
    }

    public void setPropertyOne(String propertyOne) {
        this.propertyOne = propertyOne;
    }

    public String getPropertyTwo() {
        return propertyTwo;
    }

    public void setPropertyTwo(String propertyTwo) {
        this.propertyTwo = propertyTwo;
    }

    public File getFileOne() {
        return fileOne;
    }

    public void setFileOne(File fileOne) {
        this.fileOne = fileOne;
    }

    public File getFileTwo() {
        return fileTwo;
    }

    public void setFileTwo(File fileTwo) {
        this.fileTwo = fileTwo;
    }

    public String getPathToThirdFile() {
        return pathToThirdFile;
    }

    public void setPathToThirdFile(String pathToThirdFile) {
        this.pathToThirdFile = pathToThirdFile;
    }

    public void setPathToFileThree(String pathToThirdFile) {
        this.pathToThirdFile = pathToThirdFile;
    }

    public ChildObject getChildObject() {
        return childObject;
    }

    public void setChildObject(ChildObject childObject) {
        this.childObject = childObject;
    }
}
