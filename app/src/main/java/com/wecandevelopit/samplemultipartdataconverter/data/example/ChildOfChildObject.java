package com.wecandevelopit.samplemultipartdataconverter.data.example;

import com.wecandevelopit.multipartobjectconverter.MultipartFile;

import java.io.File;

/**
 * Created by evgeek on 1/15/18.
 */

public class ChildOfChildObject {
    private String childOfChildFieldOne;
    private String childOfChildFieldTwo;
    @MultipartFile
    private File childOfChildFile;

    public String getChildOfChildFieldOne() {
        return childOfChildFieldOne;
    }

    public void setChildOfChildFieldOne(String childOfChildFieldOne) {
        this.childOfChildFieldOne = childOfChildFieldOne;
    }

    public String getChildOfChildFieldTwo() {
        return childOfChildFieldTwo;
    }

    public void setChildOfChildFieldTwo(String childOfChildFieldTwo) {
        this.childOfChildFieldTwo = childOfChildFieldTwo;
    }

    public File getChildOfChildFile() {
        return childOfChildFile;
    }

    public void setChildOfChildFile(File childOfChildFile) {
        this.childOfChildFile = childOfChildFile;
    }
}
