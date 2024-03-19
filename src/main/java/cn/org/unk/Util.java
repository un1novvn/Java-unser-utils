package cn.org.unk;

import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Arrays;
import java.util.Base64;

public class Util {
    public static void setFieldValue(Object obj,String fieldname,Object value) throws Exception, IllegalAccessException {
        Field field= getField(obj.getClass(),fieldname);

        if(field == null) throw new Exception("field "+fieldname + "not found!");
        field.setAccessible(true);
        field.set(obj,value);
    }

    public static Method getMethod(final Class<?> clazz, final String methodName,Class[] paramClass) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName,paramClass);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null){
                method = getMethod(clazz.getSuperclass(), methodName,paramClass);
            }else{
                throw new RuntimeException(e);
            }
        }
        return method;
    }
    public static Object invokeStaticMethod(Class clazz, final String methodName,Class[] paramClass,Object[] params) {
        Method method = getMethod(clazz,methodName,paramClass);
        try {
            return method.invoke(null, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object invokeMethod(Object obj, final String methodName,Class[] paramClass,Object[] params) {
        Method method = getMethod(obj.getClass(),methodName,paramClass);
        try {
            return method.invoke(obj, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(Object obj,String fieldname) throws Exception{
        Field field = getField(obj.getClass(), fieldname);
        Object o = field.get(obj);
        return o;
    }
    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(obj);
        return bytes.toByteArray();
    }


    public static void serialize(Object obj,String filename) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(obj);
    }
    public static Object unserialize(String filename) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
        Object o = in.readObject();
        return o;
    }

    public static Object unserialize(byte[] bytes) throws Exception{
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return in.readObject();
    }

    public static ObjectInputStream getInput(String fileName) throws Exception{
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
        return objectInputStream;
    }

    public static ObjectOutputStream getOutput(String fileName) throws Exception{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
        return objectOutputStream;
    }

    public static String base64Encode(byte[] bytes){
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static byte[] base64Decode(String base64){
        return Base64.getDecoder().decode(base64);
    }

    public static String URLEncode(String text) throws Exception{
        return URLEncoder.encode(text,"UTF-8");
    }

    public static String byte2Hex(byte[] bytes){
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        String hex = hexString.toString();

        return hex;
    }
    public static byte[][] splitByteArray(byte[] byteArray, int chunkSize) {
        int numOfChunks = (int) Math.ceil((double) byteArray.length / chunkSize);
        byte[][] result = new byte[numOfChunks][];

        for (int i = 0; i < numOfChunks; i++) {
            int start = i * chunkSize;
            int length = Math.min(byteArray.length - start, chunkSize);
            byte[] chunk = Arrays.copyOfRange(byteArray, start, start + length);
            result[i] = chunk;
        }
        return result;
    }
    public static String URLDecode(String text) throws Exception{
        return URLDecoder.decode(text,"UTF-8");
    }


    public static byte[] file2ByteArray(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();

        return data;
    }

    public static void byteArray2File(byte[] bytes,String filePath) throws IOException {

        OutputStream out = new FileOutputStream(filePath);
        out.write(bytes);
    }

    //From Boogipop
    //https://boogipop.com/2023/03/21/TCTF2022%20_%20Hessian-onlyJdk/
    public static <T> T createWithoutConstructor(Class<T> classToInstantiate) throws Exception{
        return createWithConstructor(classToInstantiate, Object.class, new Class[0], new Object[0]);
    }

    public static <T> T createWithConstructor(Class<T> classToInstantiate, Class<? super T> constructorClass, Class<?>[] consArgTypes, Object[] consArgs) throws Exception {
        Constructor<? super T> objCons = constructorClass.getDeclaredConstructor(consArgTypes);
        objCons.setAccessible(true);
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(classToInstantiate, objCons);
        sc.setAccessible(true);
        return (T) sc.newInstance(consArgs);
    }


    public static byte[] inputStream2ByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }





}
