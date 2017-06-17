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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

public class ShareService {

    //连接服务器 获得JSON文件
    public static int getMyItems(String paths,int id, String time,String photo,String name,String words) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
            HttpPost httpRequst = new HttpPost(paths);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //传入参数-----------------------------------------------------------------

            params.add(new BasicNameValuePair("id", id+""));
            params.add(new BasicNameValuePair("time", time));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("photo", photo));
            params.add(new BasicNameValuePair("words", words));

            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpRequst);

            if (response.getStatusLine().getStatusCode() == 200) {  //连接成功返回码200
                StringBuilder builder = new StringBuilder();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    builder.append(s);
                }
                buffer.close();
               if(builder.toString().equals("success")){
                   Log.i(TAG, "getMyItems: 成功");
                   return 1;
               }else{
                   Log.i(TAG, "getMyItems: 失败");
                   return 0;
               }
            }
        } catch (Exception e) {

            return 0;
        }
        return 0;
    }
}
