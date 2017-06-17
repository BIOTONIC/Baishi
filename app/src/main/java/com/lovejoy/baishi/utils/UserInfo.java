package com.lovejoy.baishi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class UserInfo {
    private static int id;
    private static String name;
    private static String phone;
    private static String imgUrl;
    private static String labels;

    private static void getUserInfo(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        UserInfo.id = pref.getInt("id", 0);
        UserInfo.name = pref.getString("name", null);
        UserInfo.phone = pref.getString("phone", null);
        UserInfo.imgUrl = pref.getString("imgUrl", null);
        UserInfo.labels = pref.getString("labels", null);
    }

    public static int getId(Context context) {
        UserInfo.getUserInfo(context);
        return UserInfo.id;
    }

    public static String getName(Context context) {
        UserInfo.getUserInfo(context);
        return UserInfo.name;
    }

    public static String getPhone(Context context) {
        UserInfo.getUserInfo(context);
        return UserInfo.phone;
    }

    public static String getImgUrl(Context context) {
        UserInfo.getUserInfo(context);
        return UserInfo.imgUrl;
    }

    public static String[] getLabels(Context context) {
        UserInfo.getUserInfo(context);
        if (UserInfo.labels != null && !UserInfo.labels.equals("") && !UserInfo.labels.equals("none")) {
            String[] array = UserInfo.labels.split(",");
            return array;
        } else {
            return null;
        }
    }
}
