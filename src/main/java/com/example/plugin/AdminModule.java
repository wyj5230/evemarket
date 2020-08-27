package com.example.plugin;

import java.io.*;
import java.util.Properties;

public class AdminModule
{
    public static String setProperties(String key, String value)
    {
        try (InputStream input =  new FileInputStream(new File("keyword.properties"))) {
            Properties prop = new Properties();
            prop.load(input);
            writeToProperties(key, value, prop);
            return "'" + key + "'添加成功";
        } catch (IOException ex) {
            ex.printStackTrace();
            Properties prop = new Properties();
            writeToProperties(key, value, prop);
            return "keyword.properties不存在，因此已被创建。'" + key + "'添加成功";
        }
    }

    private static void writeToProperties(String key, String value, Properties prop)
    {
        try (OutputStream output = new FileOutputStream("keyword.properties")) {
            prop.setProperty(key,value);
            prop.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getPropertyByKey(String key)
    {
        try (InputStream input =  new FileInputStream(new File("keyword.properties"))) {
            Properties prop = new Properties();
            prop.load(input);
            String result = prop.getProperty(key);
            return result==null?"指令错误":result;
        } catch (IOException ex) {
            ex.printStackTrace();
            return "出现严重错误，服务器端keyword.properties缺失";
        }
    }

    public static String getPropertiesFullList()
    {
        try (InputStream input =  new FileInputStream(new File("keyword.properties"))) {
            Properties prop = new Properties();
            prop.load(input);
            StringBuilder result = new StringBuilder("请用.get + 项目名获取信息，请从以下列表挑选项目: \n");
            prop.keySet().stream().map(n -> n.toString() + "\n").forEach(result::append);
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "出现严重错误，服务器端keyword.properties缺失";
        }
    }

    public static String deletePropertyByKey(String key)
    {
        try (InputStream input =  new FileInputStream(new File("keyword.properties"))) {
            Properties prop = new Properties();
            prop.load(input);
            String result = "'" + key + "'删除成功";
            try (OutputStream output = new FileOutputStream("keyword.properties")) {
                Object removedObject = prop.remove(key);
                if (removedObject == null) {
                    result = "删除失败，该项目不存在";
                }
                prop.store(output, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
            return "删除失败，keyword.properties文件不存在";
        }
    }
}

