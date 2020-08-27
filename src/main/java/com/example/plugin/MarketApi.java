package com.example.plugin;

import com.example.plugin.evemarketDTO.MarketItem;
import com.example.plugin.evemarketDTO.SearchResult;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
          return constuctBlurSearchResult(keyword, secondSearchResults);
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
      return constuctBlurSearchResult(keyword, searchResults);
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
      if (searchResults.size() <= limit && stringContainsItemFromArray(searchResultJava.getName(), matchWords) ) {
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

  private static String retrieveJsonFromAPI(String urlString) throws IOException {
    HttpClient client = HttpClients.custom().build();
    HttpGet searchItem = new HttpGet(urlString);
    searchItem.setHeader("User-Agent", "Mozilla/5.0");
    HttpResponse searchResponse = client.execute(searchItem);
    HttpEntity entity = searchResponse.getEntity();
    String jsonResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
    return jsonResponse;
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
  }

  private static String constructPriceLine(String itemName, double buyPrice, int buyOrder, double sellPrice, int sellOrder){
    return itemName +"\n"
              + "吉他买价： " + NumberFormat.getNumberInstance(Locale.US).format(Math.round(buyPrice)) + "ISK"
              + " 买单数： " + buyOrder +"\n"
              + "吉他售价： "+ NumberFormat.getNumberInstance(Locale.US).format(Math.round(sellPrice)) + "ISK"
              + " 卖单数： "+ sellOrder +"\n";
  }

  private static String constuctBlurSearchResult(String keyword, List<SearchResult> searchResults){
    StringBuilder result = new StringBuilder("关键词'" + keyword + "'返回了多个匹配项，请从以下列表挑选: \n");
    for (SearchResult searchResult : searchResults) {
      result.append(searchResult.getName()).append(", id ").append(searchResult.getId()).append("\n");
    }
    result.append("请使用'.jita + 中文物品名全称' 或者 '.id + 数字id' 进行查询").append("\n");
    return result.toString();
  }

  public static String getHelpMessage() {
    return "【.jita + 中文】搜索物品列表或报价\n【.id + 数字】精准查找物品报价\n【.get 列表】获取可供查阅的资讯";
  }
  public static boolean stringContainsItemFromArray(String inputStr, String[] items) {
    return Arrays.stream(items).allMatch(inputStr::contains);
  }
}
