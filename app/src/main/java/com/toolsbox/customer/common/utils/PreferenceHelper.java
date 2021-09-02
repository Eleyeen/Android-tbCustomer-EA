package com.toolsbox.customer.common.utils;

import com.toolsbox.customer.common.Constant;

public class PreferenceHelper {
    private static String TAG = "PreferenceHelper";

    //Preference
    public static void removePreference(){
        AppPreferenceManager.setInt(Constant.PRE_ID, 0);
        AppPreferenceManager.setInt(Constant.PRE_SIGN_TYPE, 0);
        AppPreferenceManager.setString(Constant.PRE_EMAIL, "");
        AppPreferenceManager.setString(Constant.PRE_PHONE, "");
        AppPreferenceManager.setString(Constant.PRE_NAME, "");
        AppPreferenceManager.setString(Constant.PRE_PASSWORD, "");
        AppPreferenceManager.setString(Constant.PRE_IMAGE_URL, "");
        AppPreferenceManager.setString(Constant.PRE_TOKEN, "");
    }

    public static void savePreference(int id, int sign_type, String email, String phone, String password, String name, String imageURL, String fcmToken, String token){
        AppPreferenceManager.setInt(Constant.PRE_ID, id);
        AppPreferenceManager.setInt(Constant.PRE_SIGN_TYPE, sign_type);
        AppPreferenceManager.setString(Constant.PRE_EMAIL, email);
        AppPreferenceManager.setString(Constant.PRE_PHONE, phone);
        AppPreferenceManager.setString(Constant.PRE_PASSWORD, password);
        AppPreferenceManager.setString(Constant.PRE_NAME, name);
        AppPreferenceManager.setString(Constant.PRE_IMAGE_URL, imageURL);
        AppPreferenceManager.setString(Constant.PRE_TOKEN, token);
    }

    public static boolean isLoginIn(){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        return !StringHelper.isEmpty(token);
    }


    public static String getName(){
        String name = AppPreferenceManager.getString(Constant.PRE_NAME, "");
        return name;
    }

    public static String getEmail(){
        String email = AppPreferenceManager.getString(Constant.PRE_EMAIL, "");
        return email;
    }

    public static String getPhone(){
        String phone = AppPreferenceManager.getString(Constant.PRE_PHONE, "");
        return phone;
    }

    public static String getImageURL(){
        String imageURL = AppPreferenceManager.getString(Constant.PRE_IMAGE_URL, "");
        return imageURL;
    }

    public static String getToken(){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        return token;
    }

}
