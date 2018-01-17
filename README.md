# multipart-object-converter
Multipart Object Converter is an android library for fast and convenient conversion of any POJO to part map and file list.
It allows to customize format of outputted parts like GSON. Library has dependencies on okhttp3 and was originally created for usage with [retrofit](https://github.com/square/retrofit).

### Installation:
For now you can import `multipart-object-converter-release.aar` file in this repo. Library is uploading to jCenter to be available through gradle and maven.

### Example:
```java
GsonBuilder gsonBuilder = new GsonBuilder() // gson is used as serializer
        .setDateFormat("yyyy:MM:dd")        // so you can customize most serialization properties here
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

MultipartObjectConverter converter = new MultipartObjectConverter.Builder()
        .setGsonBuilder(gsonBuilder)
        .create();

Map<String, RequestBody> partMap = converter.convertToPartMap(requestObject);
List<MultipartBody.Part> files = converter.convertToMultipartFiles(requestObject);

api.makeRequest(partmap, files)
```
In this example api is an instance of retrofit-created class from interface, that contained 
```java
@Multipart
@POST("foo")
Call<Object> testRequest(
        @PartMap() Map<String, RequestBody> partMap,
        @Part List<MultipartBody.Part> files);
```
See? Simple! In fact, you can send object of any complexity with any number of files in three lines of code.
There are more usage examples in sample project and jUnit tests.

In sample project [Chuck Interceptor](https://github.com/jgilfelt/chuck) is used to inercept all http traffic, so when you click on `UPLOAD DATA` button a notification should appear. You can click it to inspect sent data. We don't need server for testing this way. To open sample project you need to clone or download this repository, and just open it with Android Studio.
