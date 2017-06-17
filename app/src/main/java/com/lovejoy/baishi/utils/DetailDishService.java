package com.lovejoy.baishi.utils;

/**
 * Created by Administrator on 2016/7/12.
 */

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

public class DetailDishService {
    //连接服务器 获得JSON文件
    public static ArrayList<DetailDishItem> getMyItems(int id, String paths) {
        Log.i("WEI", "GG");
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
            HttpPost httpRequst = new HttpPost(paths);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //传入参数-----------------------------------------------------------------
            params.add(new BasicNameValuePair("Did", Integer.toString(id)));
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
                return DetailDishService.parseJSON(builder.toString());
            }
        } catch (Exception e) {
            ArrayList<DetailDishItem> myItems = new ArrayList<>();
            DetailDishItem myItem = new DetailDishItem(-1, e.getMessage(), "http://www.iconpng.com/png/simple-icons/116.png", -1, "", "", "", "", "");
            //Log.i("WEI",e.getMessage());
            myItems.add(myItem);
            return myItems;
        }
        return null;
    }

    //解析JSON文件 存到myItems中
    private static ArrayList<DetailDishItem> parseJSON(String json) {
        ArrayList<DetailDishItem> myItems = new ArrayList<>();
        DetailDishItem myItem;
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("did");
                String name = jsonObject.getString("name");
                String imgUrl = jsonObject.getString("imgurl");
                String special = jsonObject.getString("charactertices");
                int heat = jsonObject.getInt("heat");
                String upLoader = jsonObject.getString("owner");
                String taboo = jsonObject.getString("taboo");
                String nurtrition = jsonObject.getString("nurtrition");
                String step = jsonObject.getString("steps");

                myItem = new DetailDishItem(id, name, imgUrl, heat, special, upLoader, taboo, nurtrition, step);
                myItems.add(myItem);
                myItem = null;
            }
        } catch (Exception e) {
            myItem = new DetailDishItem(-1, e.getMessage(), "http://www.iconpng.com/png/simple-icons/116.png", -1, "", "", "", "", "");
            myItems.add(myItem);
            myItem = null;
        }
        return myItems;
    }
}