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
            return "'" + key + "'添加成功";
        } catch (IOException ex) {
            ex.printStackTrace();
            Properties prop = new Properties();
            writeToProperties(key, value, prop, filePath);
            return filePath + "不存在，因此已被创建。'" + key + "'添加成功";
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
            StringBuilder result = new StringBuilder("请用.get + 项目名获取信息，请从以下列表挑选项目: \n");
            prop.keySet().stream().map(n -> n.toString() + "\n").forEach(result::append);
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "出现严重错误，服务器端" + filePath + "缺失";
        }
    }

    public static String deletePropertyByKey(String key,String filePath)
    {
        try (InputStream input =  new FileInputStream(new File(filePath))) {
            Properties prop = new Properties();
            prop.load(input);
            String result = "'" + key + "'删除成功";
            try (OutputStream output = new FileOutputStream(filePath)) {
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
            return "删除失败，"+ filePath +"文件不存在";
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
                + "吉他买价： " + NumberFormat.getNumberInstance(Locale.US).format(Math.round(buyPrice)) + "ISK"
                + " 买单数： " + buyOrder +"\n"
                + "吉他售价： "+ NumberFormat.getNumberInstance(Locale.US).format(Math.round(sellPrice)) + "ISK"
                + " 卖单数： "+ sellOrder +"\n";
    }

    public static String constuctBlurSearchResultFromEveMarket(String keyword, List<SearchResult> searchResults){
        StringBuilder result = new StringBuilder("关键词'" + keyword + "'返回了多个匹配项，请从以下列表挑选: \n");
        for (SearchResult searchResult : searchResults) {
            result.append(searchResult.getName()).append(", id ").append(searchResult.getId()).append("\n");
        }
        result.append("请使用'.jita + 中文物品名全称' 或者 '.id + 数字id' 进行查询").append("\n");
        return result.toString();
    }

    public static String constuctBlurSearchResultFromCEVE(String keyword, List<Item> items){
        StringBuilder result = new StringBuilder("关键词'" + keyword + "'返回了多个匹配项，请从以下列表挑选: \n");
        for (Item item : items) {
            result.append(item.getName()).append(", id ").append(item.getId()).append("\n");
        }
        result.append("请使用'.jita + 中文物品名全称' 或者 '.id + 数字id' 进行查询").append("\n");
        return result.toString();
    }


}

