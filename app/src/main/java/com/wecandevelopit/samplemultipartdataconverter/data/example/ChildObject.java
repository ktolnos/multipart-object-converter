package com.wecandevelopit.samplemultipartdataconverter.data.example;

import com.wecandevelopit.multipartobjectconverter.MultipartFile;

/**
 * Created by evgeek on 1/15/18.
 */

public class ChildObject {
    private String childFieldOne;
    private double childFieldTwo;
    @MultipartFile
    private String pathToChildFile;
    private ChildOfChildObject childOfChildObject;

    public String getChildFieldOne() {
        return childFieldOne;
    }

    public void setChildFieldOne(String childFieldOne) {
        this.childFieldOne = childFieldOne;
    }

    public double getChildFieldTwo() {
        return childFieldTwo;
    }

    public void setChildFieldTwo(double childFieldTwo) {
        this.childFieldTwo = childFieldTwo;
    }

    public String getPathToChildFile() {
        return pathToChildFile;
    }

    public void setPathToChildFile(String pathToChildFile) {
        this.pathToChildFile = pathToChildFile;
    }

    public ChildOfChildObject getChildOfChildObject() {
        return childOfChildObject;
    }

    public void setChildOfChildObject(ChildOfChildObject childOfChildObject) {
        this.childOfChildObject = childOfChildObject;
    }
}
