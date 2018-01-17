package com.wecandevelopit.multipartobjectconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by evgeek on 11/22/17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MultipartFile {
    String value() default ""; // specify Content-Type here. By default it will be calculated from file extension
}
