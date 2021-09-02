package com.toolsbox.customer.view.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.TBApplication;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.LoadChannelListener;
import com.toolsbox.customer.common.interFace.TaskCompletionListener;
import com.toolsbox.customer.common.interFace.UnreadMsgListener;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.controller.chat.ChatClientManager;
import com.toolsbox.customer.controller.chat.channels.ChannelManager;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.fragment.MarketFragment;
import com.toolsbox.customer.view.fragment.HomeFragment;
import com.toolsbox.customer.view.fragment.MessageFragment;
import com.toolsbox.customer.view.fragment.ProfileFragment;
import com.toolsbox.customer.view.fragment.JobsFragment;
import com.twilio.chat.Channel;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;

import java.util.List;

import q.rorbin.badgeview.QBadgeView;

import static com.toolsbox.customer.common.Constant.FRAG_HOME;
import static com.toolsbox.customer.common.Constant.FRAG_JOBS;
import static com.toolsbox.customer.common.Constant.FRAG_MARKET;
import static com.toolsbox.customer.common.Constant.FRAG_MESSAGE;
import static com.toolsbox.customer.common.Constant.FRAG_PROFILE;

public class HomeActivity extends BaseActivity implements ChatClientListener, UnreadMsgListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";
    private Fragment m_fragment;

    private enum ACTIVITY_VIEW {HOME, MARKET, JOBS, MESSAGES, PROFILE}
    private SectionReceiver sectionReceiver;
    private BottomNavigationView bottomNavigationView;
    private ChatClientManager chatClientManager;
    private ChannelManager channelManager;
    private QBadgeView qBadgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initVariable();
        initUI();
        if (PreferenceHelper.isLoginIn()) {
            checkTwilioClient();
            TBApplication.setgLoggedIn(true);
        }

    }

    void initVariable() {
        sectionReceiver = new SectionReceiver();
        registerReceiver(sectionReceiver, new IntentFilter(Constant.ACTION_CHANGED_SECTION));
        chatClientManager = TBApplication.get().getChatClientManager();
        channelManager = ChannelManager.getInstance();
        channelManager.setChannelListener(this);
        channelManager.setUnreadMsgListener(this);

    }

    void initUI() {
        bottomNavigationView = findViewById(R.id.nav_bottom_view1);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        replaceFragment(ACTIVITY_VIEW.HOME);
        qBadgeView = new QBadgeView(this);
        qBadgeView.setBadgeBackgroundColor(getResources().getColor(R.color.colorRed));
        qBadgeView.setShowShadow(false);
    }


    void showBadge(int number){
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        if (number == 0){
            qBadgeView.bindTarget(v).hide(true);
        } else {
            qBadgeView.bindTarget(v).setBadgeNumber(number);
        }
    }

    void checkTwilioClient(){
        if (chatClientManager.getChatClient() == null){
            initializeChatClient();
        }
    }

    void initializeChatClient() {
        chatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Connect Twilio Client Successfully!");
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_bottom_home:
                replaceFragment(ACTIVITY_VIEW.HOME);
                break;

            case R.id.nav_bottom_contractor:
                replaceFragment(ACTIVITY_VIEW.MARKET);
                break;

            case R.id.nav_bottom_project:
                replaceFragment(ACTIVITY_VIEW.JOBS);
                break;

            case R.id.nav_bottom_news:
                replaceFragment(ACTIVITY_VIEW.MESSAGES);
                break;

            case R.id.nav_bottom_profile:
                replaceFragment(ACTIVITY_VIEW.PROFILE);
                break;

        }

        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sectionReceiver != null){
            unregisterReceiver(sectionReceiver);
            sectionReceiver = null;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                chatClientManager.shutdown();
                TBApplication.get().getChatClientManager().setChatClient(null);
            }
        });
    }

    void changeStatusBarColor(HomeActivity.ACTIVITY_VIEW nActive) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            if (nActive == ACTIVITY_VIEW.HOME){
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void replaceFragment(HomeActivity.ACTIVITY_VIEW nActive) {
        switch (nActive) {
            case HOME:
                m_fragment = new HomeFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction().addToBackStack(null).replace(R.id.frm_container, m_fragment).commit();
                break;


            case MARKET:
                m_fragment = new MarketFragment();
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                fragmentManager2.beginTransaction().addToBackStack(null).replace(R.id.frm_container, m_fragment).commit();
                break;

            case JOBS:
                m_fragment = new JobsFragment();
                FragmentManager fragmentManager3 = getSupportFragmentManager();
                fragmentManager3.beginTransaction().addToBackStack(null).replace(R.id.frm_container, m_fragment).commit();
                break;

            case MESSAGES:
                m_fragment = new MessageFragment();
                FragmentManager fragmentManager4 = getSupportFragmentManager();
                fragmentManager4.beginTransaction().addToBackStack(null).replace(R.id.frm_container, m_fragment).commit();
                break;

            case PROFILE:
                m_fragment = new ProfileFragment();
                FragmentManager fragmentManager5 = getSupportFragmentManager();
                fragmentManager5.beginTransaction().addToBackStack(null).replace(R.id.frm_container, m_fragment).commit();
                break;
        }
    }

    void moveNavigationItem(int section){
        switch (section){
            case FRAG_HOME:
                bottomNavigationView.setSelectedItemId(R.id.nav_bottom_home);
                break;
            case FRAG_MARKET:
                bottomNavigationView.setSelectedItemId(R.id.nav_bottom_contractor);
                break;
            case FRAG_JOBS:
                bottomNavigationView.setSelectedItemId(R.id.nav_bottom_project);
                break;
            case FRAG_MESSAGE:
                bottomNavigationView.setSelectedItemId(R.id.nav_bottom_news);
                break;
            case FRAG_PROFILE:
                bottomNavigationView.setSelectedItemId(R.id.nav_bottom_profile);
                break;
        }
    }

    private class SectionReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "called sectionReceiver");
            int section = intent.getIntExtra("Section", FRAG_HOME);
            moveNavigationItem(section);
        }
    }


    // ChatClientListener

    @Override
    public void onChannelJoined(Channel channel) {

    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onChannelAdded(Channel channel) {

    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {

    }

    @Override
    public void onChannelDeleted(Channel channel) {

    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {

    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {

    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {

    }

    @Override
    public void onAddedToChannelNotification(String s) {

    }

    @Override
    public void onInvitedToChannelNotification(String s) {

    }

    @Override
    public void onRemovedFromChannelNotification(String s) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }

    @Override
    public void onTokenExpired() {

    }

    @Override
    public void onTokenAboutToExpire() {

    }

    // Custom Listener for unread message number

    @Override
    public void onUpdateBadge(int total) {
        showBadge(total);
    }


}
