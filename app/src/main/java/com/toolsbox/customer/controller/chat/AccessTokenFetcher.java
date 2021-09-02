package com.toolsbox.customer.controller.chat;

import android.content.Context;
import android.util.Log;

import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.interFace.TaskCompletionListener;
import com.toolsbox.customer.common.model.api.TwilioAccessTokenData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.GlobalUtils;

import static com.toolsbox.customer.common.utils.DeviceUtil.getAndroidUniqueID;

public class AccessTokenFetcher {
  private static final String TAG = "AccessTokenFetcher";
  private Context context;

  public AccessTokenFetcher(Context context) {
    this.context = context;
  }

  public void fetch(final TaskCompletionListener<String, String> listener) {
    String androidID = getAndroidUniqueID(context);
    String identity = GlobalUtils.getChatIdentity();
    ApiUtils.fetchTwilioAccessToken(context, identity, androidID, new Notify() {
      @Override
      public void onSuccess(Object object) {
        TwilioAccessTokenData data = (TwilioAccessTokenData) object;
        if (data != null){
          if (data.status == 0){
            Log.e(TAG, "Twilio Access Token =" + data.info);
            String accessToken = data.info;
            listener.onSuccess(accessToken);
          } else {
            if (listener != null) {
              listener.onError(data.message);
            }
          }
        } else {
          if (listener != null) {
            listener.onError("Server Not response");
          }
        }
      }

      @Override
      public void onFail() {
        listener.onError("Check network connection");
      }
    });
  }

}
