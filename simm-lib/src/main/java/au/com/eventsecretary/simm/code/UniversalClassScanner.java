package au.com.eventsecretary.simm.code;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UniversalClassScanner {

    public static List<Class<?>> findImplementations(Class<?> targetInterface, String basePackage) throws Exception {
        List<Class<?>> implementations = new ArrayList<>();
        String packagePath = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            if (resource.getProtocol().equals("jar")) {
                // Handle packaged JAR environments
                processJarResource(resource, packagePath, targetInterface, implementations);
            } else {
                // Handle local IDE / file system environments
                File directory = new File(resource.toURI());
                if (directory.exists()) {
                    processDirectoryResource(directory, basePackage, targetInterface, implementations);
                }
            }
        }
        return implementations;
    }

    private static void processDirectoryResource(File directory, String currentPackage, Class<?> targetInterface, List<Class<?>> result) throws Exception {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectoryResource(file, currentPackage + "." + file.getName(), targetInterface, result);
            } else if (file.getName().endsWith(".class")) {
                String className = currentPackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                checkAndAddClass(className, targetInterface, result);
            }
        }
    }

    private static void processJarResource(URL resource, String packagePath, Class<?> targetInterface, List<Class<?>> result) throws Exception {
        JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
        try (JarFile jarFile = jarConn.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // Check if the entry is a .class file inside our target package path
                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                    // Convert file path format back to dot-separated package format
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    checkAndAddClass(className, targetInterface, result);
                }
            }
        }
    }

    private static void checkAndAddClass(String className, Class<?> targetInterface, List<Class<?>> result) {
        try {
            Class<?> clazz = Class.forName(className);
            if (targetInterface.isAssignableFrom(clazz) && !clazz.isInterface()) {
                result.add(clazz);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Safe to ignore classes that can't be loaded or linked in this context
        }
    }
}
