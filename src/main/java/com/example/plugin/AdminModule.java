package com.example.plugin;

import com.example.plugin.cevemarketDTO.Item;
import com.example.plugin.evemarketDTO.SearchResult;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class AdminModule
{
    public static String setProperties(String key, String value, String filePath)
    {
        try (InputStream input =  new FileInputStream(new File(filePath))) {
            Properties prop = new Properties();
            prop.load(input);
            writeToProperties(key, value, prop, filePath);
            return "'" + key + "'��ӳɹ�";
        } catch (IOException ex) {
            ex.printStackTrace();
            Properties prop = new Properties();
            writeToProperties(key, value, prop, filePath);
            return filePath + "�����ڣ�����ѱ�������'" + key + "'��ӳɹ�";
        }
    }

    private static void writeToProperties(String key, String value, Properties prop, String filePath)
    {
        try (OutputStream output = new FileOutputStream(filePath)) {
            prop.setProperty(key,value);
            prop.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getPropertyByKey(String key, String filePath)
    {
        try (InputStream input =  new FileInputStream(new File(filePath))) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getPropertiesFullList(String filePath)
    {
        try (InputStream input =  new FileInputStream(new File(filePath))) {
            Properties prop = new Properties();
            prop.load(input);
            StringBuilder result = new StringBuilder("����.get + ��Ŀ����ȡ��Ϣ����������б���ѡ��Ŀ: \n");
            prop.keySet().stream().map(n -> n.toString() + "\n").forEach(result::append);
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "�������ش��󣬷�������" + filePath + "ȱʧ";
        }
    }

    public static String deletePropertyByKey(String key,String filePath)
    {
        try (InputStream input =  new FileInputStream(new File(filePath))) {
            Properties prop = new Properties();
            prop.load(input);
            String result = "'" + key + "'ɾ���ɹ�";
            try (OutputStream output = new FileOutputStream(filePath)) {
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
            return "ɾ��ʧ�ܣ�"+ filePath +"�ļ�������";
        }
    }

    public static List<Item> getPropContainsAll(String[] matchers, String filePath)
    {
        try (InputStream input =  new FileInputStream(new File(filePath))) {
            Properties prop = new Properties();
            prop.load(input);
            List<Item> result = new ArrayList<>();
            for (Object key : prop.keySet()) {
                String keyString = key.toString();
                if (stringContainsAllItemFromArray(keyString, matchers))
                {
                    if (result.size()<=20)
                    {
                        Item item = new Item(keyString,prop.getProperty(keyString));
                        result.add(item);
                    } else {
                        break;
                    }
                }
            }
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static boolean stringContainsAllItemFromArray(String inputStr, String[] items) {
        return Arrays.stream(items).allMatch(inputStr::contains);
    }

    public static String constructPriceLine(String itemName, double buyPrice, int buyOrder, double sellPrice, int sellOrder){
        return itemName +"\n"
                + "������ۣ� " + NumberFormat.getNumberInstance(Locale.US).format(Math.round(buyPrice)) + "ISK"
                + " ������ " + buyOrder +"\n"
                + "�����ۼۣ� "+ NumberFormat.getNumberInstance(Locale.US).format(Math.round(sellPrice)) + "ISK"
                + " �������� "+ sellOrder +"\n";
    }

    public static String constuctBlurSearchResultFromEveMarket(String keyword, List<SearchResult> searchResults){
        StringBuilder result = new StringBuilder("�ؼ���'" + keyword + "'�����˶��ƥ�����������б���ѡ: \n");
        for (SearchResult searchResult : searchResults) {
            result.append(searchResult.getName()).append(", id ").append(searchResult.getId()).append("\n");
        }
        result.append("��ʹ��'.jita + ������Ʒ��ȫ��' ���� '.id + ����id' ���в�ѯ").append("\n");
        return result.toString();
    }

    public static String constuctBlurSearchResultFromCEVE(String keyword, List<Item> items){
        StringBuilder result = new StringBuilder("�ؼ���'" + keyword + "'�����˶��ƥ�����������б���ѡ: \n");
        for (Item item : items) {
            result.append(item.getName()).append(", id ").append(item.getId()).append("\n");
        }
        result.append("��ʹ��'.jita + ������Ʒ��ȫ��' ���� '.id + ����id' ���в�ѯ").append("\n");
        return result.toString();
    }


}

