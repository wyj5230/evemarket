package com.example.plugin;


import com.example.plugin.cevemarketDTO.Item;
import com.example.plugin.cevemarketDTO.MarketResult;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.plugin.AdminModule.*;
import static com.example.plugin.Constants.MAPPING_PATH;

public class EVEMapping
{
    public static String searchItemFromKeyword(String keyword) {
        if (keyword.isEmpty()) {
            return "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
        }
        keyword = keyword.toLowerCase();
        String id = getPropertyByKey(keyword,MAPPING_PATH);
        if (id != null) {
            //exact match
            MarketResult marketResult = getMarketDataFromId(id);
            if (marketResult == null) {
                return "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
            } else {
                return getMessageFromMarketResult(keyword, marketResult);
            }
        } else {
            // no exact match, entering elastic search
            String[] keywordList = keyword.split("\\s+");
            List<Item> keyList = getPropContainsAll(keywordList,MAPPING_PATH);
            // elastic search no result
            if (keyList == null || keyList.isEmpty()) {
                return "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
            }
            // elastic search only one result
            if (keyList.size() == 1) {
                System.out.println("Scenario 1, Nothing matched");
                MarketResult marketResult = getMarketDataFromId(keyList.get(0).getId());
                return getMessageFromMarketResult(keyList.get(0).getName(),marketResult);
            } else {
                return constuctBlurSearchResultFromCEVE(keyword, keyList);
            }
        }
    }

    public static String searchItemFromId(String id) {
        MarketResult marketResult = getMarketDataFromId(id);
        if (marketResult == null) {
            return "ID:'"+ id +"'没有返回匹配项。白面鸮这样说道。";
        }
        return getMessageFromMarketResult(id, marketResult);
    }

    public static MarketResult getMarketDataFromId(String id) {
        String detailItem;
        try {
            String urlString = "http://www.ceve-market.org/tqapi/market/region/10000002/system/30000142/type/"+ id +".json";
            detailItem = retrieveJsonFromAPI(urlString);
            if (detailItem == null) {
                return null;
            }
        } catch (IOException e) {
            System.out.println("Call eve market API failed, reason: " + e.getMessage());
            return null;
        }
        return new Gson().fromJson(detailItem, MarketResult.class);
    }

    public static String getMessageFromMarketResult(String keyword, MarketResult marketResult) {
        return constructPriceLine(keyword, marketResult.getBuy().getMax(), marketResult.getBuy().getVolume(),
                marketResult.getSell().getMin(), marketResult.getSell().getVolume());
    }

    public static String retrieveJsonFromAPI(String urlString) throws IOException
    {
        HttpClient client = HttpClients.custom().build();
        HttpGet searchItem = new HttpGet(urlString);
        searchItem.setHeader("User-Agent", "Mozilla/5.0");
        HttpResponse searchResponse = client.execute(searchItem);
        HttpEntity entity = searchResponse.getEntity();
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }
}
