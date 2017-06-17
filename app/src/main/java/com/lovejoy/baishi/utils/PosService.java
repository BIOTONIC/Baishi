package com.lovejoy.baishi.utils;

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

public class PosService {
    public static ArrayList<RestaurantPos> getRestaurantsPos() {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
            //----------------------------------------------------------------
            HttpPost httpRequst = new HttpPost("http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/get_rest_location");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("UID", "1"));
            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpRequst);

            if (response.getStatusLine().getStatusCode() == 200) {  //连接成功返回码200
                StringBuilder builder = new StringBuilder();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    builder.append(s);
                }
                buffer.close();
                return PosService.parseJSON(builder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static ArrayList<RestaurantPos> parseJSON(String json) {
        ArrayList<RestaurantPos> list = new ArrayList<>();
        RestaurantPos pos;
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(jsonObject.getString("rid"));
                String name = jsonObject.getString("name");
                String imgUrl = jsonObject.getString("imgurl");
                double posX = Double.parseDouble(jsonObject.getString("locationx"));
                double posY = Double.parseDouble(jsonObject.getString("locationy"));
                if (posX > 0 && posY > 0) {
                    pos = new RestaurantPos(id, name, imgUrl, posX, posY);
                    list.add(pos);
                }
                pos = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
}
