package com.toolsbox.customer;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.libraries.places.api.Places;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.controller.chat.ChatClientManager;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import static com.toolsbox.customer.common.Constant.GOOGLE_API_KEY;


/**
 * Created by LS on 9/13/2017.
 */

public class TBApplication extends MultiDexApplication {

    private static final String TAG = "TBApplication";

    private ChatClientManager basicClient;
    private static TBApplication instance;
    public static Context gContext;
    private static String gActiveChannelSid = "";
    private static boolean gLoggedIn = false;

    /**
     * Multi Dex supporting for some version below KitKat
     * @param base
     */

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (gContext == null)
            gContext = getApplicationContext();
        AppPreferenceManager.initializePreferenceManager(gContext);
        TBApplication.instance = this;
        basicClient = new ChatClientManager(getApplicationContext());

        Places.initialize(gContext, GOOGLE_API_KEY);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)//enable debug mode
                .build();

        //finally initialize twitter with created configs
        Twitter.initialize(config);
        MultiDex.install(this);


    }


    public static TBApplication get() {
        return instance;
    }

    public ChatClientManager getChatClientManager() {
        return this.basicClient;
    }

    public static void setActiveChannelSid(String sid){
        gActiveChannelSid = sid;
    }

    public static String getActiveChannelSid() {
        return gActiveChannelSid;
    }

    public static boolean isgLoggedIn() {
        return gLoggedIn;
    }

    public static void setgLoggedIn(boolean gLoggedIn) {
        TBApplication.gLoggedIn = gLoggedIn;
    }

}
