package com.lovejoy.baishi.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

public class DetialRestaurantService {
    //连接服务器 获得JSON文件
    public static ArrayList<DetailRestaurantItem> getMyItems(int id, String paths) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
            HttpPost httpRequst = new HttpPost(paths);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //传入参数-----------------------------------------------------------------
            params.add(new BasicNameValuePair("Rid", id + ""));

            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpRequst);

            if (response.getStatusLine().getStatusCode() == 200) {  //连接成功返回码200
                StringBuilder builder = new StringBuilder();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    builder.append(s);
                }
                buffer.close();
                Log.i("LOVEJOY", builder.toString());
                return DetialRestaurantService.parseJSON(builder.toString());
            }
        } catch (Exception e) {
            ArrayList<DetailRestaurantItem> myItems = new ArrayList<>();
            DetailRestaurantItem myItem = new DetailRestaurantItem(-1, "http://www.iconpng.com/png/simple-icons/116.png", e.getMessage(), -1, "", "", "");
            //Log.i("WEI",e.getMessage());
            myItems.add(myItem);
            return myItems;
        }
        return null;
    }

    //解析JSON文件 存到myItems中
    private static ArrayList<DetailRestaurantItem> parseJSON(String json) {
        ArrayList<DetailRestaurantItem> myItems = new ArrayList<>();
        DetailRestaurantItem myItem;
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("rid");
                String name = jsonObject.getString("name");
                String imgUrl = jsonObject.getString("imgurl");
                int monetum = jsonObject.getInt("monetum");
                String location = jsonObject.getString("loaction");
                String phone = jsonObject.getString("phone");
                String info = jsonObject.getString("information");


                myItem = new DetailRestaurantItem(id, imgUrl, name, monetum, location, phone, info);
                myItems.add(myItem);
                myItem = null;
            }
        } catch (Exception e) {
            myItem = new DetailRestaurantItem(-1, "http://www.iconpng.com/png/simple-icons/116.png", e.getMessage(), -1, "", "", "");
            myItems.add(myItem);
            myItem = null;
        }
        return myItems;
    }
}