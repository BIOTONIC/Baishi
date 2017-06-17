package com.lovejoy.baishi.utils;

import com.lovejoy.baishi.utils.ShareItem;
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

public class ShowService {
    //连接服务器 获得JSON文件
    public static ArrayList<ShareItem> getMyItems(String paths) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
            HttpPost httpRequst = new HttpPost(paths);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //传入参数-----------------------------------------------------------------

            params.add(new BasicNameValuePair("sid", "1"));

            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpRequst);

            if (response.getStatusLine().getStatusCode() == 200) {  //连接成功返回码200
                StringBuilder builder = new StringBuilder();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    builder.append(s);
                }
                buffer.close();
                return parseJSON(builder.toString());
            }
        } catch (Exception e) {
            ArrayList<ShareItem> myItems = new ArrayList<>();
            ShareItem myItem = new ShareItem(-1, e.getMessage(), "http://www.iconpng.com/png/simple-icons/116.png", "", "","http://www.iconpng.com/png/simple-icons/116.png");
            //Log.i("WEI",e.getMessage());
            myItems.add(myItem);
            return myItems;
        }
        return null;
    }

    //解析JSON文件 存到myItems中
    private static ArrayList<ShareItem> parseJSON(String json) {
        ArrayList<ShareItem> myItems = new ArrayList<>();
        ShareItem myItem;
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("sid");
                String name = jsonObject.getString("name");
                String image = jsonObject.getString("image");
                String time = jsonObject.getString("time");
                String words = jsonObject.getString("words");
                String photo = jsonObject.getString("photo");

                myItem = new ShareItem(id,name,image,time,words,photo);
                myItems.add(myItem);
                myItem = null;
            }
        } catch (Exception e) {
            myItem = new ShareItem(-1, e.getMessage(), "http://www.iconpng.com/png/simple-icons/116.png", "", "","http://www.iconpng.com/png/simple-icons/116.png");
            myItems.add(myItem);
            myItem = null;
        }
        return myItems;
    }



}

