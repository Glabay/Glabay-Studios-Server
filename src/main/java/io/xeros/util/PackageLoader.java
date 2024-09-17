package io.xeros.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-16
 */
public class PackageLoader {

    public static Class[] load(String packageName, Class<?> ext) throws Exception {
        return load(packageName, ext, true);
    }

    public static Class[] load(String packageName, Class<?> ext, boolean allowAnonymous) throws Exception {
        Class[] classes = load(packageName);
        ArrayList<Class> list = new ArrayList<>();
        for(Class c : classes) {
            if(ext != c && ext.isAssignableFrom(c))
                list.add(c);
        }
        if(!allowAnonymous)
            list.removeIf(Class::isAnonymousClass);
        return list.toArray(new Class[list.size()]);
    }

    public static Class[] load(String packageName) throws Exception {
        var classLoader = Thread.currentThread().getContextClassLoader();
        var path = packageName.replace('.', '/');
        var resources = classLoader.getResources(path);
        var dirs = new ArrayList<File>();
        while(resources.hasMoreElements()) {
            var resource = resources.nextElement();
            var fileName = resource.getFile().replace("%20", " ").replace("%5c", File.separator);
            dirs.add(new File(fileName));
        }
        var classes = new ArrayList<Class>();
        for (var dir : dirs) {
            var loaded = findClasses(dir, packageName);
            if (loaded != null)
                classes.addAll(loaded);
        }
        return classes.toArray(new Class[0]);
    }

    private static ArrayList<Class> findClasses(File dir, String packageName) {
        var classes = new ArrayList<Class>();
            var files = dir.listFiles();
            if (files == null || files.length == 0)
                return null;
            for (var file : files) {
                var name = file.getName();
                if (file.isDirectory()) {
                    if (name.contains("."))
                        continue;
                    var loaded = findClasses(file, packageName + "." + name);
                    if (loaded != null)
                        classes.addAll(loaded);
                    continue;
                }
                if (name.endsWith(".class")) {
                    try {
                        classes.add(Class.forName(packageName + '.' + name.substring(0, name.length() - 6)));
                    }
                    catch(Throwable ignored) {}
                }
            }
            return classes;
    }
}
