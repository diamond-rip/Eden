package io.github.epicgo.sconey.reflection;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class that simplifies reflection in Bukkit plugins
 */
public class Reflection {

    /**
     * Returns is a native 1.7 version
     * @return If is native 1.7 version
     */
    public boolean isNative17() {
        return getBuildVersion().contains("v1_7");
    }

    /**
     * The build version the server is running
     * @return the version of build R- of the server
     */
    public static String getBuildVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    /**
     * Returns a specified class from its full name
     *
     * @param clazz the class name
     * @return the looked up class
     */
    @SneakyThrows
    public static Class<?> getClass(final String clazz) {
        return Class.forName(clazz);
    }

    /**
     * Returns a class in the net.minecraft.server.VERSION.* package
     *
     * @param clazz the name of the class, excluding the package
     * @return the looked up nms class
     */
    public static Class<?> getNMSClass(final String clazz) {
       return getClass("net.minecraft.server." + getBuildVersion() + "." + clazz);
    }

    /**
     * Returns a class in the org.bukkit.craftbukkit.VERSION.* package
     *
     * @param clazz the name of the class, excluding the package
     * @return the looked up obc class
     */
    public static Class<?> getOBCClass(final String clazz) {
        return getClass("org.bukkit.craftbukkit." + getBuildVersion() + "." + clazz);
    }

    /**
     * Returns a field object for a specific field  name
     *
     * @param target the class instance
     * @param fieldName the name of the field
     * @return the field object
     */
    @SneakyThrows
    public Object getField(final Object target, final String fieldName) {
        final Field field = target.getClass().getField(fieldName);
        field.setAccessible(true);

        return field.get(target);
    }

    /**
     * Returns a declared field object for a specific field  name
     *
     * @param target the class instance
     * @param fieldName the name of the field
     * @return the field object
     */
    @SneakyThrows
    public Object getDeclaredField(final Object target, final String fieldName) {
        final Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field.get(target);
    }

    /**
     * Sets a field object for a specific field objectValue
     *
     * @param target the class instance
     * @param fieldName the name of the field
     * @param objectValue the value to set
     */
    @SneakyThrows
    public void setField(final Object target, final String fieldName, final Object objectValue) {
        final Field field = target.getClass().getField(fieldName);
        field.setAccessible(true);
        field.set(target, objectValue);
    }

    /**
     * Sets a declared field object for a specific field objectValue
     *
     * @param target the class instance
     * @param fieldName the name of the field
     * @param objectValue the value to set
     */
    public void setDeclaredField(final Object target, final String fieldName, final Object objectValue) {
        try {
            final Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, objectValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Invoke a method on a specific target object
     *
     * @param target the target object
     * @param methodName the method name
     * @param params the params to pass to the method
     * @return An object that invokes this specific method
     */
    @SneakyThrows
    public static Object method(final Object target, final String methodName, final Object... params) {
        final List<Method> methodList = Arrays.stream(target.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> m.getParameters().length == params.length)
                .collect(Collectors.toList());

        return methodList.get(0).invoke(target, params);
    }

    /**
     * Invoke a method on a specific target object
     *
     * @param target the target class
     * @param methodName the method name
     * @param params the params to pass to the method
     * @return An object that invokes this specific method
     */
    @SneakyThrows
    public static Object method(final Class<?> target, final String methodName, final Object... params) {
        final List<Method> methodList = Arrays.stream(target.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .collect(Collectors.toList());

        return methodList.get(0).invoke(target, params);
    }


    /**
     * Invoke a method on a specific target object
     *
     * @param target the target class
     * @param methodName the method name
     * @param classes the expected parameters
     * @param params - the params to pass to the method
     * @return An object that invokes this specific method
     */
    @SneakyThrows
    public static Object method(final Object target, final String methodName, final Class<?>[] classes, final Object... params) {
        return target.getClass().getMethod(methodName, classes).invoke(target, params);
    }

    /**
     * Invoke a constructor for a specific class
     *
     * @param target the target class
     * @param params the expected parameters
     * @return An object that invokes this constructor
     */
    @SneakyThrows
    public static Object constructor(final Class<?> target, final Object... params) {
        final List<Constructor<?>> constructorList = Arrays.stream(target.getConstructors())
                .filter(constructor -> constructor.getParameters().length == params.length)
                .collect(Collectors.toList());

        return constructorList.get(0).newInstance(params);
    }
}
