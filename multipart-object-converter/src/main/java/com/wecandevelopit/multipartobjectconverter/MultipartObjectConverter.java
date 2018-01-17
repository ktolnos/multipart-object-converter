package com.wecandevelopit.multipartobjectconverter;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartObjectConverter {

    private static final String tag = "MultipartConverter";

    private boolean includeNotAnnotatedFiles = false;
    private boolean includeFilesOfInnerClasses = true;
    private boolean serializeComplexObjectsToJson = true;
    private int maxFileSearchDepth = 10;
    private Gson gson;

    public MultipartObjectConverter(GsonBuilder gsonBuilder) { // Gson builder is used to configure field serialization parameters like field naming strategy
        gson = gsonBuilder.addSerializationExclusionStrategy(new AnnotationExclusionStrategy())
                .create();
    }

    public boolean isIncludeNotAnnotatedFiles() {
        return includeNotAnnotatedFiles;
    }

    public void setIncludeNotAnnotatedFiles(boolean includeNotAnnotatedFiles) {
        this.includeNotAnnotatedFiles = includeNotAnnotatedFiles;
    }

    public boolean isIncludeFilesOfInnerClasses() {
        return includeFilesOfInnerClasses;
    }

    public void setIncludeFilesOfInnerClasses(boolean includeFilesOfInnerClasses) {
        this.includeFilesOfInnerClasses = includeFilesOfInnerClasses;
    }

    public int getMaxFileSearchDepth() {
        return maxFileSearchDepth;
    }

    public void setMaxFileSearchDepth(int maxFileSearchDepth) {
        this.maxFileSearchDepth = maxFileSearchDepth;
    }

    public boolean isSerializeComplexObjectsToJson() {
        return serializeComplexObjectsToJson;
    }

    public void setSerializeComplexObjectsToJson(boolean serializeComplexObjectsToJson) {
        this.serializeComplexObjectsToJson = serializeComplexObjectsToJson;
    }

    public Map<String, RequestBody> convertToPartMap(Object o) {


        Map<String, RequestBody> partMap = new HashMap<>();
        JsonElement element = gson.toJsonTree(o);
        if (!element.isJsonObject()) {
            Log.e(tag, "Object is too primitive to convert. Use createPartFromString");
            return null;
        }

        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if (!serializeComplexObjectsToJson &&
                    !(entry.getValue().isJsonPrimitive() || entry.getValue().isJsonNull()))
                continue;
            String value = entry.getValue().toString();
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                value = value.substring(1, value.length() - 1);
            }
            partMap.put(entry.getKey(), createPartFromString(value));
        }

        return partMap;
    }

    public List<MultipartBody.Part> convertToMultipartFiles(Object o) {
        return convertToMultipartFiles(o, null, 0);
    }

    private List<MultipartBody.Part> convertToMultipartFiles(Object o, String arrayName, int depth) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        List<Field> fields = Arrays.asList(o.getClass().getDeclaredFields());
        for (Field field : fields) {
            if (((File.class.equals(field.getType()) && includeNotAnnotatedFiles)
                    || field.isAnnotationPresent(MultipartFile.class)) &&
                    (String.class.equals(field.getType()) || File.class.equals(field.getType()))
                    ) {
                field.setAccessible(true);
                try {
                    if (field.get(o) != null) {
                        String name = gson.fieldNamingStrategy().translateName(field);
                        if (arrayName != null && !arrayName.isEmpty()) {
                            name = arrayName + "[" + name + "]";
                        }
                        MultipartBody.Part part;
                        if (String.class.equals(field.getType())) {
                            part = getFilePartFromPathString((String) field.get(o),
                                    field.getAnnotation(MultipartFile.class).value(), name);
                        } else {
                            String fileType = null;
                            if (field.isAnnotationPresent(MultipartFile.class)) {
                                fileType = field.getAnnotation(MultipartFile.class).value();
                            }
                            part = getFilePartFromPathString((File) field.get(o),
                                    fileType, name);
                        }

                        if (part != null) {
                            parts.add(part);
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (
                    depth < maxFileSearchDepth &&
                            includeFilesOfInnerClasses &&
                            !field.getType().isPrimitive() &&
                            !field.getType().getName().startsWith("java.lang.") &&
                            !field.isAnnotationPresent(ExcludeMultipart.class)) {
                field.setAccessible(true);
                Log.d(tag, field.getName() + " " + depth);
                try {
                    Object ob = field.get(o);
                    if (ob != null) {
                        String name = gson.fieldNamingStrategy().translateName(field);
                        if (arrayName != null && !arrayName.isEmpty()) {
                            name = arrayName + "[" + name + "]";
                        }
                        parts.addAll(convertToMultipartFiles(field.get(o), name, ++depth));
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }

        return parts;
    }

    public MultipartBody.Part getFilePartFromPathString(String path, String fileType, String partName) {
        try {
            File file = new File(path);
            return getFilePartFromPathString(file, fileType, partName);
        } catch (Exception e) {
            return null;
        }
    }

    public MultipartBody.Part getFilePartFromPathString(File file, String fileType, String partName) {
        try {
            if (!file.exists()) {
                Log.d(tag, "File doesn't exist: " + file.getAbsolutePath());
                return null;
            }
            MediaType type;
            if (fileType != null && !fileType.isEmpty()) {
                type = MediaType.parse(fileType);
            } else {
                type = MediaType.parse(getMimeType(file));
            }
            RequestBody requestFile = RequestBody.create(type, file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            return null;
        }
    }

    public RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    String getMimeType(File file) {
        return getMimeType(file.getAbsolutePath());
    }

    String getMimeType(String path) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (type == null)
            return "";
        return type;
    }

    public static class Builder {

        private GsonBuilder gsonBuilder = new GsonBuilder();
        private boolean includeNotAnnotatedFiles = false;
        private boolean includeFilesOfInnerClasses = true;
        private boolean serializeComplexObjectsToJson = true;
        private int maxFileSearchDepth = 10;

        public Builder setGsonBuilder(GsonBuilder gsonBuilder) {
            this.gsonBuilder = gsonBuilder;
            return this;
        }

        public Builder setIncludeNotAnnotatedFiles(boolean include) {
            includeNotAnnotatedFiles = include;
            return this;
        }

        public Builder setIncludeFilesOfInnerClasses(boolean include) {
            includeFilesOfInnerClasses = include;
            return this;
        }

        public Builder setMaxFileSearchDepth(int maxDepth) {
            maxFileSearchDepth = maxDepth;
            return this;
        }

        public Builder setSerializeComplexObjectsToJson(boolean serialize) {
            serializeComplexObjectsToJson = serialize;
            return this;
        }


        public MultipartObjectConverter create() {
            MultipartObjectConverter res = new MultipartObjectConverter(gsonBuilder);
            res.includeNotAnnotatedFiles = includeNotAnnotatedFiles;
            res.includeFilesOfInnerClasses = includeFilesOfInnerClasses;
            res.maxFileSearchDepth = maxFileSearchDepth;
            res.serializeComplexObjectsToJson = serializeComplexObjectsToJson;
            return res;
        }
    }

    private static class AnnotationExclusionStrategy implements ExclusionStrategy {


        public AnnotationExclusionStrategy() {
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {

            return f.getAnnotation(ExcludeMultipart.class) != null ||
                    f.getAnnotation(MultipartFile.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}

