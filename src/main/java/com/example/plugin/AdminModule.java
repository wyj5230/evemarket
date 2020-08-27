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
            return "'" + key + "'��ӳɹ�";
        } catch (IOException ex) {
            ex.printStackTrace();
            Properties prop = new Properties();
            writeToProperties(key, value, prop);
            return "keyword.properties�����ڣ�����ѱ�������'" + key + "'��ӳɹ�";
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
            return result==null?"ָ�����":result;
        } catch (IOException ex) {
            ex.printStackTrace();
            return "�������ش��󣬷�������keyword.propertiesȱʧ";
        }
    }

    public static String getPropertiesFullList()
    {
        try (InputStream input =  new FileInputStream(new File("keyword.properties"))) {
            Properties prop = new Properties();
            prop.load(input);
            StringBuilder result = new StringBuilder("����.get + ��Ŀ����ȡ��Ϣ����������б���ѡ��Ŀ: \n");
            prop.keySet().stream().map(n -> n.toString() + "\n").forEach(result::append);
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "�������ش��󣬷�������keyword.propertiesȱʧ";
        }
    }

    public static String deletePropertyByKey(String key)
    {
        try (InputStream input =  new FileInputStream(new File("keyword.properties"))) {
            Properties prop = new Properties();
            prop.load(input);
            String result = "'" + key + "'ɾ���ɹ�";
            try (OutputStream output = new FileOutputStream("keyword.properties")) {
                Object removedObject = prop.remove(key);
                if (removedObject == null) {
                    result = "ɾ��ʧ�ܣ�����Ŀ������";
                }
                prop.store(output, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
            return "ɾ��ʧ�ܣ�keyword.properties�ļ�������";
        }
    }
}

