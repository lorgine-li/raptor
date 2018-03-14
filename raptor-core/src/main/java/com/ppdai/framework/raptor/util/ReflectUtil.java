package com.ppdai.framework.raptor.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectUtil {
    public static final String PARAM_CLASS_SPLIT = ",";
    public static final String EMPTY_PARAM = "void";
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    private static final ConcurrentMap<String, Class<?>> NAME_CLASS_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, String> CLASS_NAME_CACHE = new ConcurrentHashMap<>();

    private static final String[] PRIMITIVE_NAMES = new String[]{"boolean", "byte", "char", "double", "float", "int", "long", "short",
            "void"};

    private static final Class<?>[] PRIMITIVE_CLASSES = new Class[]{boolean.class, byte.class, char.class, double.class, float.class,
            int.class, long.class, short.class, Void.TYPE};

    private static final int PRIMITIVE_CLASS_NAME_MAX_LENGTH = 7;

    public static String getMethodSignature(Method method) {
        return getMethodSignature(method.getName(), method.getParameterTypes());
    }

    public static String getMethodSignature(String methodName, Class<?>[] parameterTypes) {
        return getMethodSignature(methodName, getMethodParameterTypes(parameterTypes));
    }

    public static String getMethodSignature(String methodName, String[] parameterTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append(methodName);
        if (parameterTypes != null) {
            builder.append("(");
            builder.append(StringUtils.join(parameterTypes));
            builder.append(")");
        }
        return builder.toString();
    }

    public static String[] getMethodParameterTypes(Method method) {
        return getMethodParameterTypes(method.getParameterTypes());
    }

    public static String[] getMethodParameterTypes(Class<?>[] parameterTypes) {
        String[] parameterTypeStrings = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypeStrings[i] = parameterTypes[i].getName();
        }
        return parameterTypeStrings;
    }

    public static Class<?>[] forNames(String classList) throws ClassNotFoundException {
        if (classList == null || "".equals(classList) || EMPTY_PARAM.equals(classList)) {
            return EMPTY_CLASS_ARRAY;
        }

        String[] classNames = classList.split(PARAM_CLASS_SPLIT);
        Class<?>[] classTypes = new Class<?>[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];

            classTypes[i] = forName(className);
        }

        return classTypes;
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        if (null == className || "".equals(className)) {
            return null;
        }

        Class<?> clz = NAME_CLASS_CACHE.get(className);

        if (clz != null) {
            return clz;
        }

        clz = forNameWithoutCache(className);

        // 应该没有内存消耗过多的可能，除非有些代码很恶心，创建特别多的类
        NAME_CLASS_CACHE.putIfAbsent(className, clz);

        return clz;
    }

    private static Class<?> forNameWithoutCache(String className) throws ClassNotFoundException {
        if (!className.endsWith("[]")) {
            // not array
            Class<?> clz = getPrimitiveClass(className);
            clz = (clz != null) ? clz : Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            return clz;
        }
        int dimensionSiz = 0;
        while (className.endsWith("[]")) {
            dimensionSiz++;
            className = className.substring(0, className.length() - 2);
        }
        int[] dimensions = new int[dimensionSiz];
        Class<?> clz = getPrimitiveClass(className);
        if (clz == null) {
            clz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }
        return Array.newInstance(clz, dimensions).getClass();
    }

    /**
     * 需要支持一维数组、二维数组等
     */
    public static String getName(Class<?> clz) {
        if (clz == null) {
            return null;
        }
        String className = CLASS_NAME_CACHE.get(clz);
        if (className != null) {
            return className;
        }
        className = getNameWithoutCache(clz);
        // 与name2ClassCache同样道理，如果没有恶心的代码，这块内存大小应该可控
        CLASS_NAME_CACHE.putIfAbsent(clz, className);
        return className;
    }

    private static String getNameWithoutCache(Class<?> clz) {
        if (!clz.isArray()) {
            return clz.getName();
        }

        StringBuilder sb = new StringBuilder();
        while (clz.isArray()) {
            sb.append("[]");
            clz = clz.getComponentType();
        }

        return clz.getName() + sb.toString();
    }

    public static Class<?> getPrimitiveClass(String name) {
        // check if is primitive class
        if (name.length() <= PRIMITIVE_CLASS_NAME_MAX_LENGTH) {
            int index = Arrays.binarySearch(PRIMITIVE_NAMES, name);
            if (index >= 0) {
                return PRIMITIVE_CLASSES[index];
            }
        }
        return null;
    }

    /**
     * 获取clz public method
     * <p>
     * <pre>
     *      1）不包含构造函数
     *      2）不包含Object.class
     *      3）包含该clz的父类的所有public方法
     * </pre>
     *
     * @param clz
     * @return
     */
    public static List<Method> getPublicMethod(Class<?> clz) {
        Method[] methods = clz.getMethods();
        List<Method> ret = new ArrayList<Method>();

        for (Method method : methods) {

            boolean isPublic = Modifier.isPublic(method.getModifiers());
            boolean isNotObjectClass = method.getDeclaringClass() != Object.class;

            if (isPublic && isNotObjectClass) {
                ret.add(method);
            }
        }

        return ret;
    }

    private static Object getEmptyObject(Class<?> returnType, Map<Class<?>, Object> emptyInstances, int level) {
        if (level > 2) {
            return null;
        }
        if (returnType == null) {
            return null;
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return false;
        } else if (returnType == char.class || returnType == Character.class) {
            return '\0';
        } else if (returnType == byte.class || returnType == Byte.class) {
            return (byte) 0;
        } else if (returnType == short.class || returnType == Short.class) {
            return (short) 0;
        } else if (returnType == int.class || returnType == Integer.class) {
            return 0;
        } else if (returnType == long.class || returnType == Long.class) {
            return 0L;
        } else if (returnType == float.class || returnType == Float.class) {
            return 0F;
        } else if (returnType == double.class || returnType == Double.class) {
            return 0D;
        } else if (returnType.isArray()) {
            return Array.newInstance(returnType.getComponentType(), 0);
        } else if (returnType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>(0);
        } else if (returnType.isAssignableFrom(HashSet.class)) {
            return new HashSet<>(0);
        } else if (returnType.isAssignableFrom(HashMap.class)) {
            return new HashMap<>(0);
        } else if (String.class.equals(returnType)) {
            return "";
        } else if (!returnType.isInterface()) {
            try {
                Object value = emptyInstances.get(returnType);
                if (value == null) {
                    value = returnType.newInstance();
                    emptyInstances.put(returnType, value);
                }
                Class<?> cls = value.getClass();
                while (cls != null && cls != Object.class) {
                    Field[] fields = cls.getDeclaredFields();
                    for (Field field : fields) {
                        Object property = getEmptyObject(field.getType(), emptyInstances, level + 1);
                        if (property != null) {
                            try {
                                if (!field.isAccessible()) {
                                    field.setAccessible(true);
                                }
                                field.set(value, property);
                            } catch (Throwable e) {
                            }
                        }
                    }
                    cls = cls.getSuperclass();
                }
                return value;
            } catch (Throwable e) {
                return null;
            }
        } else {
            return null;
        }
    }

}
