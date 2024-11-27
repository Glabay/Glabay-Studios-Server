package io.xeros.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import io.xeros.util.JsonUtil;
import org.apache.commons.io.FileUtils;

public abstract class AttributesSerializable extends Attributes {

    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("attributes-serializable-%d").build());

    public static <T extends AttributesSerializable> T getFromFile(String file, T attributesSerializable) throws IOException {
        if (new File(file).exists()) {
            T attributes = new Gson().fromJson(FileUtils.readFileToString(new File(file)), attributesSerializable.getType());
            if (attributes == null) {
                return attributesSerializable;
            }
            attributes.setFile(file);
            return attributes;
        } else {
            return attributesSerializable;
        }
    }

    public abstract Type getType();

    private transient String file;

    public AttributesSerializable(String file) {
        this.file = file;
    }

    void setFile(String file) {
        this.file = file;
    }

    public void write() {
        SERVICE.submit(() -> JsonUtil.toJson(this, file));
    }

    public void set(String key, Object value) {
        super.set(key, value);
        write();
    }

    public void remove(Object key) {
        super.remove(key);
        write();
    }

    public void setInt(String key, int set) {
        super.setInt(key, set);
        write();
    }

    public void removeInt(String key) {
        super.removeInt(key);
        write();
    }

    public void removeDouble(String key) {
        super.removeDouble(key);
        write();
    }

    public void setBoolean(String key, boolean set) {
        super.setBoolean(key, set);
        write();
    }

    public void removeBoolean(String key) {
        super.removeBoolean(key);
        write();
    }

    public void setLong(String key, long set) {
        super.setLong(key, set);
        write();
    }

    public void removeLong(String key) {
        super.removeLong(key);
        write();
    }

    public void setString(String key, String set) {
        super.setString(key, set);
        write();
    }

    public void removeString(String key) {
        super.removeString(key);
        write();
    }

    public void setList(String key, List list) {
        super.setList(key, list);
        write();
    }

}
