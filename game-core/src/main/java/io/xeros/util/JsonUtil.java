package io.xeros.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

public class JsonUtil {

    private static ObjectMapper jsonMapper = new ObjectMapper();
    private static ObjectWriter jsonWriter = null;
    private static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    private static ObjectWriter yamlWriter = null;

    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    static {
        // Grab values from fields only instead of methods
        Lists.newArrayList(jsonMapper, yamlMapper).stream().forEach(it -> {
            it.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
            it.setVisibility(it.getSerializationConfig().
                    getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        });

        jsonWriter = jsonMapper.writerWithDefaultPrettyPrinter();
        yamlWriter = yamlMapper.writerWithDefaultPrettyPrinter();
    }

    @Deprecated
    public static <T> void toJson(T t, String filePath) {
        Gson prettyGson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String prettyJson = prettyGson.toJson(t);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(new File(filePath)));
            bw.write(prettyJson);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static <T> String toJson(T t) {
        return prettyGson.toJson(t);
    }

    @Deprecated
    public static <T> T fromJson(String filePath) throws IOException {
        return fromJson(filePath, new TypeToken<>(){});
    }

    @Deprecated
    public static <T> T fromJson(String filePath, TypeToken<T> typeToken) throws IOException {
        return new Gson().fromJson(FileUtils.readFileToString(new File(filePath)), typeToken.getType());
    }

    @Deprecated
    public static <T> T fromJsonOrDefault(String filePath, TypeToken<T> typeToken, T defaultObject) throws IOException {
        if (!new File(filePath).exists()) {
            return defaultObject;
        }

        return fromJson(filePath, typeToken);
    }

    /**
     * Serialize an object to json with Jackson.
     * Note: it's supposed to only use fields but some methods are still
     * used. For instance if you have no flag field
     * and there's a method called getFlag() it will call it during serialization.
     * So be sure to check and mark the methods with {@link com.fasterxml.jackson.annotation.JsonIgnore}
     * if it needs to be ignored.
     */
    public static <T> void toJacksonJson(T t, String filePath) throws IOException {
        jsonWriter.writeValue(new File(filePath), t);
    }

    public static JsonNode fromJacksonJson(File file) throws IOException {
        return jsonMapper.readTree(file);
    }

    public static <T> T fromJacksonJson(File file, TypeReference<T> clazz) throws IOException {
        return fromJacksonJson(file.getPath(), clazz);
    }

    public static <T> T fromJacksonJson(String filePath, TypeReference<T> clazz) throws IOException {
        return jsonMapper.readValue(new File(filePath), clazz);
    }

    /**
     * Serialize an object to yaml with Jackson.
     * Note: it's supposed to only use fields but some methods are still
     * used. For instance if you have no flag field
     * and there's a method called getFlag() it will call it during serialization.
     * So be sure to check and mark the methods with {@link com.fasterxml.jackson.annotation.JsonIgnore}
     * if it needs to be ignored.
     */
    public static <T> void toYaml(T t, String filePath) throws IOException {
        yamlWriter.writeValue(new File(filePath), t);
    }

    public static <T> T fromYaml(File file, Class<T> clazz) throws IOException {
        return (T) yamlMapper.readValue(file, clazz);
    }

    public static <T> T fromYaml(String filePath, TypeReference<T> clazz) throws IOException {
        return (T) yamlMapper.readValue(new File(filePath), clazz);
    }
}
