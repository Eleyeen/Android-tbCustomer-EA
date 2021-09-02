package com.toolsbox.customer.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.toolsbox.customer.R;
import com.toolsbox.customer.TBApplication;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.ChannelChangeListener;
import com.toolsbox.customer.common.interFace.LoadChannelListener;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.interFace.TaskCompletionListener;
import com.toolsbox.customer.common.model.ChatContactInfo;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.controller.chat.ChatClientManager;
import com.toolsbox.customer.controller.chat.channels.ChannelManager;
import com.toolsbox.customer.view.activity.basic.BaseFragment;
import com.toolsbox.customer.view.activity.login.EnterActivity;
import com.toolsbox.customer.view.activity.main.messages.ChatActivity;
import com.toolsbox.customer.view.adapter.ChannelListAdapter;
import com.twilio.chat.Channel;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;

import java.util.ArrayList;
import java.util.List;

import static com.toolsbox.customer.common.Constant.FRAG_HOME;


public class MessageFragment extends BaseFragment implements ChatClientListener, ChannelChangeListener, SwipeRefreshLayout.OnRefreshListener, ChannelListAdapter.OnItemClickListener {
    private static String TAG = "MessageFragment";
    private ChatClientManager chatClientManager;
    private ChannelManager channelManager;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ChannelListAdapter adapter;
    private ProgressBar pbLoading;


    public MessageFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        initVariable();
        initUI(view);
        return view;
    }

    void initVariable() {
        if (!PreferenceHelper.isLoginIn()) {
            MessageUtils.showCustomAlertDialogNoCancel(getActivity(), getResources().getString(R.string.note), getResources().getString(R.string.please_signup_to_chat), new Notify() {
                @Override
                public void onSuccess(Object object) {
                    Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                    intentBroadcast.putExtra("Section", FRAG_HOME);
                    getActivity().sendBroadcast(intentBroadcast);
                    Intent intent = new Intent(getActivity(), EnterActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFail() {

                }
            });
            return;
        }
        chatClientManager = TBApplication.get().getChatClientManager();
        channelManager = ChannelManager.getInstance();
        channelManager.setChannelListener(this);
        channelManager.setChannelChangeListener(this);
    }


    void initUI(View view) {
        swipeRefresh = view.findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.recycler_channel);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        pbLoading = view.findViewById(R.id.pb_loading);

        adapter = new ChannelListAdapter(getActivity(), new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);

        if (!ChannelManager.getInstance().isSyncedChannels())
            showProgressbar();
        else
            refreshChannels();
    }

    void showProgressbar(){
        pbLoading.setVisibility(View.VISIBLE);
    }

    void hideProgressbar(){
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    void populateChatContact() {
        adapter.clear();
        if (channelManager.getContactInfo() == null)
            return;
        adapter.addAll(channelManager.getContactInfo());
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        refreshChannels();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    void refreshChannels() {
        if (chatClientManager.getChatClient() == null) {
            Log.e(TAG, "chatClient == null");
            swipeRefresh.setRefreshing(false);
            return;
        }
        populateChatContact();
        swipeRefresh.setRefreshing(false);
    }

    void sendChatClientEvent(String userIdentity, boolean status) {
        Intent intentBroadcast = new Intent(Constant.ACTION_CHAT_USER_REACHABILITY);
        intentBroadcast.putExtra("identity", userIdentity);
        intentBroadcast.putExtra("status", status);
        getActivity().sendBroadcast(intentBroadcast);
    }

    // OnTap Channel Item, then move to channel activity

    @Override
    public void onTapItem(ChatContactInfo item) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("sid", item.sid);
        startActivity(intent);
    }

    // ChatClient Listener

    @Override
    public void onChannelJoined(Channel channel) {
        Log.e(TAG, "onChannelJoined:");
        onRefresh();
    }

    @Override
    public void onChannelInvited(Channel channel) {
        Log.e(TAG, "onChannelInvited:");
        onRefresh();
    }

    @Override
    public void onChannelAdded(Channel channel) {
        Log.e(TAG, "onChannelAdded:");
        onRefresh();
    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
        Log.e(TAG, "onChannelUpdated:");
        onRefresh();
    }

    @Override
    public void onChannelDeleted(Channel channel) {
        Log.e(TAG, "onChannelDeleted:");
        onRefresh();
    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {
        Log.e(TAG, "onChannelSynchronizationChange:");
        onRefresh();
    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {
        Log.e(TAG, "onUserUpdated");
        adapter.notifyDataSetChanged();
        if (updateReason == User.UpdateReason.REACHABILITY_ONLINE) {
            sendChatClientEvent(user.getIdentity(), user.isOnline());
        }
    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {
        Log.e(TAG, "onClientSynchronization");
        if (synchronizationStatus == ChatClient.SynchronizationStatus.COMPLETED)
            adapter.notifyDataSetChanged();
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


    // Channel Loading custom Listener

    @Override
    public void onUpdateChannel(int index) {
        Log.e(TAG, "updated index" + index);
        adapter.updateChannel(index);
    }

    @Override
    public void onReloadChannel() {
        hideProgressbar();
        populateChatContact();
    }


}
