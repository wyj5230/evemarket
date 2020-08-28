package com.example.plugin;

import com.example.plugin.evemarketDTO.MarketItem;
import com.example.plugin.evemarketDTO.SearchResult;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.plugin.AdminModule.*;
import static com.example.plugin.EVEMapping.retrieveJsonFromAPI;

public class MarketApi
{

  public static String callSearchKeyWordApi(String keyword) {
    String keyWordResult;
    try {
      keyWordResult = getSearchUrlByKeyword(keyword);
      if (keyWordResult == null) {
        return "白面鸮级 Error发生，Evemarket网站拒绝了我的请求";
      }
    } catch (IOException e) {
      System.out.println("Call eve market API failed, reason: " + e.getMessage());
      return "白面鸮级 Error发生，Evemarket网站拒绝了我的请求";
    }
    JSONArray searchResultArray = new JSONArray(keyWordResult);

    if (searchResultArray.length() == 0){
      //对应关键词没有返回任何结果的情况
      String[] keywordList = keyword.split("\\s+");
      if (keywordList.length <= 1) {
        System.out.println("Scenario 1, Nothing matched");
        return "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
      } else {
        //用第一个关键词再次匹配
        try {
          keyWordResult = getSearchUrlByKeyword(keywordList[0]);
          if (keyWordResult == null) {
            return "白面鸮级 Error发生，Evemarket网站拒绝了我的请求";
          }
        } catch (IOException e) {
          System.out.println("Call eve market API failed, reason: " + e.getMessage());
          return "白面鸮级 Error发生，Evemarket网站拒绝了我的请求";
        }
        searchResultArray = new JSONArray(keyWordResult);
        if (searchResultArray.length() <= 1){
          return "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
        } else {
          String[] keywordListWithoutFirstElement = Arrays.copyOfRange(keywordList, 1, keywordList.length);
          List<SearchResult> secondSearchResults = getSearchResultsByFilter(searchResultArray,keywordListWithoutFirstElement,100);
          if (secondSearchResults.size() == 0 ) {
            return "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
          } else if (secondSearchResults.size() == 1 ) {
            return callDetailItemApi(secondSearchResults.get(0).getId());
          }
          return constuctBlurSearchResultFromEveMarket(keyword, secondSearchResults);
        }
      }
    } else if (searchResultArray.length() == 1 ||
            keyword.equals(new Gson().fromJson(searchResultArray.get(0).toString(), SearchResult.class).getName())){
      //对应关键词返回唯一结果的情况
      SearchResult searchResultJava = new Gson().fromJson(searchResultArray.get(0).toString(), SearchResult.class);
      System.out.println("Scenario 2, Returned only 1 item, id: " + searchResultJava.getId());
      return callDetailItemApi(searchResultJava.getId());
      } else {
      //对应返回多个结果的情况
      List<SearchResult> searchResults = getSearchResults(searchResultArray,20);
      System.out.println("Scenario 3, returning list with "+ searchResults.size() +" items.");
      return constuctBlurSearchResultFromEveMarket(keyword, searchResults);
    }

  }

  private static List<SearchResult> getSearchResults(JSONArray searchResultArray, int limit)
  {
    List<SearchResult> searchResults = new ArrayList<SearchResult>();
    for(Object searchResultJson: searchResultArray){
      SearchResult searchResultJava = new Gson().fromJson(searchResultJson.toString(), SearchResult.class);
      if (searchResults.size() <= limit ) {
        searchResults.add(searchResultJava);
      } else {
        break;
      }
    }
    return searchResults;
  }

  private static List<SearchResult> getSearchResultsByFilter(JSONArray searchResultArray, String[] matchWords, int limit)
  {
    List<SearchResult> searchResults = new ArrayList<SearchResult>();
    for(Object searchResultJson: searchResultArray){
      SearchResult searchResultJava = new Gson().fromJson(searchResultJson.toString(), SearchResult.class);
      if (searchResults.size() <= limit && stringContainsAllItemFromArray(searchResultJava.getName(), matchWords) ) {
        searchResults.add(searchResultJava);
      }
    }
    return searchResults;
  }

  private static String getSearchUrlByKeyword(String keyword) throws IOException
  {
    String keyWordResult;
    String urlString = "http://evemarketer.com/api/v1/types/search?q="+ URLEncoder.encode(keyword, "UTF-8") +
          "&language=zh&important_names=false";
    keyWordResult = retrieveJsonFromAPI(urlString);
    return keyWordResult;
  }

  public static String callDetailItemApi(String id){
    String detailItem;
    try {
      String urlString = "http://evemarketer.com/api/v1/markets/types/"+ URLEncoder.encode(id, "UTF-8") +"?region_id=10000002&language=zh&important_names=false";
      detailItem = retrieveJsonFromAPI(urlString);
      if (detailItem == null) {
        return "关键字:'"+ id +"'没有返回匹配项。白面鸮这样说道。";
      }
    } catch (IOException e) {
      System.out.println("Call eve market API failed, reason: " + e.getMessage());
      return "关键字:'"+ id +"'没有返回匹配项。白面鸮这样说道。";
    }
    MarketItem marketItem = new Gson().fromJson(detailItem, MarketItem.class);
    return constructPriceLine(marketItem.getType().getName(),marketItem.getBuy_stats().getFive_percent(),
            marketItem.getBuy_stats().getOrder_count(), marketItem.getSell_stats().getFive_percent(),marketItem.getSell_stats().getOrder_count() );
  }


//    String USER_AGENT = "Mozilla/5.0";
//    URL url = new URL(urlString);
//    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//    conn.setRequestMethod("GET");
//    conn.setRequestProperty("User-Agent", USER_AGENT);
//    conn.setRequestProperty("Host", USER_AGENT);
//    conn.connect();
//    int responsecode = conn.getResponseCode();
//    System.out.println(responsecode);
//    if (responsecode == HttpURLConnection.HTTP_OK) {
//      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//      StringBuilder sb = new StringBuilder();
//      String line;
//      while ((line = br.readLine()) != null) {
//        sb.append(line).append("\n");
//      }
//      br.close();
//      String responseBody = sb.toString();
//      System.out.println(responseBody);
//      return responseBody;
//    }
//    return null;

  public static String getHelpMessage() {
    return "【.jita + 中文】搜索物品列表或报价\n【.id + 数字】精准查找物品报价\n【.get 列表】获取可供查阅的资讯";
  }

}
