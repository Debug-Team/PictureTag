package com.debugTeam.dao.impl;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

/**
 * @Author: Cauchy-Ny
 * @Description:
 * @Data: Create in 13:56 2018/4/1
 * @Modified By:
 */
public class IOHelper {

    /**
     * 反序列化对象
     * @param path 文件目录
     * @return 反序列化后的对象
     */
    public static Object readProject(String path) {
        Object result = null;

        try (ObjectInputStream is = new ObjectInputStream(
                new FileInputStream(path))) {
            result = is.readObject();// 从流中读取User的数据
            is.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 序列化对象
     * @param object 对象
     * @param path 存储路径
     */
    public static void writeProject(Object object, String path) {
        try (ObjectOutputStream os = new ObjectOutputStream(
                new FileOutputStream(path))) {
            os.writeObject(object);  //将User对象写进文件
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Tag的json字符串信息
     * @param path tag所在目录
     * @return tag的json字符串
     */
    public static String readTag(String path) throws IOException{
        File file = new File(path);
        String result = null;

        if(!file.exists())
            throw new IOException();
        BufferedReader rd = new BufferedReader(new FileReader(file));
        System.out.println(file.getAbsolutePath());
        result = rd.readLine();

        return result;
    }

    /**
     * 写入Tag的json字符串信息
     * @param data tag的字符串json数据
     * @param path 存储路径
     * @throws FileAlreadyExistsException 文件重复异常
     */
    public static void writeTag(String data, String path) throws FileAlreadyExistsException {
        File file = new File(path);
        if(!file.exists())
            throw new FileAlreadyExistsException(path);

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(file))) {
            wr.write(data);
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
