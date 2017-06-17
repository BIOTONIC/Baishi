package com.lovejoy.baishi.utils;

import android.net.Uri;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

public class MainService {
    //连接服务器 获得JSON文件
    public static ArrayList<MyItem> getMyItems(String keys, String paths) {
        try {
            String path = paths;
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
            HttpPost httpRequst = new HttpPost(path);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //传入参数-----------------------------------------------------------------
            if (keys == "" || keys == null) {
                params.add(new BasicNameValuePair("UID", "1"));
            } else {
                params.add(new BasicNameValuePair("Name", keys));
            }
            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpRequst);

            if (response.getStatusLine().getStatusCode() == 200) {  //连接成功返回码200
                StringBuilder builder = new StringBuilder();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    builder.append(s);
                }
                buffer.close();
                return MainService.parseJSON(builder.toString());
            }
        } catch (Exception e) {
            ArrayList<MyItem> myItems = new ArrayList<>();
            MyItem myItem = new MyItem(-1, e.toString(), "http://www.iconpng.com/png/simple-icons/116.png", "");
            myItems.add(myItem);
            return myItems;
        }
        return null;
    }

    //解析JSON文件 存到myItems中
    private static ArrayList<MyItem> parseJSON(String json) {
        ArrayList<MyItem> myItems = new ArrayList<>();
        MyItem myItem;
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String imgUrl = jsonObject.getString("imgurl");
                String info = jsonObject.getString("info");
                myItem = new MyItem(id, name, imgUrl, info);
                myItems.add(myItem);
                myItem = null;
            }
        } catch (Exception e) {
            myItem = new MyItem(-1, e.getMessage(), "http://www.iconpng.com/png/simple-icons/116.png", "");
            myItems.add(myItem);
            myItem = null;
        }
        return myItems;
    }

    //获取图片
    //path:图片路径 cacheDir:本地缓存路径
    public static Uri getImage(String path, File cacheDir) throws Exception {
        File localFile = new File(cacheDir, MD5.getMD5(path) + path.substring(path.lastIndexOf(".")));
        //如果图片存在于缓存中，就返回该图片
        if (localFile.exists()) {
            Uri uri = Uri.fromFile(localFile);
            Log.i("LOVEJOY", uri.getPath());
            return uri;
        } else {//否则从网络中加载该图片并缓存起来
            HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                FileOutputStream outStream = new FileOutputStream(localFile);
                InputStream inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                inputStream.close();
                outStream.close();
                Uri uri = Uri.fromFile(localFile);
                Log.i("LOVEJOY", uri.getEncodedPath());
                return uri;
            }
        }
        return null;
    }
}
