package com.toolsbox.customer.common.utils;

import android.database.Cursor;

import com.twilio.chat.Message;

public class ChatMessageHelper {
    private static final String TAG = "ChatMessageHelper";

    public static final int TYPE_INCOMING_PLAIN_TEXT = 1;
    public static final int TYPE_OUTGOING_PLAIN_TEXT = 2;
    public static final int TYPE_INCOMING_IMAGE = 3;
    public static final int TYPE_OUTGOING_IMAGE = 4;
    public static final int TYPE_INCOMING_VIDEO = 5;
    public static final int TYPE_OUTGOING_VIDEO = 6;

    public static boolean isPlainTextMessage(Message object) {
        return !object.hasMedia();
    }

    public static boolean isImageMessage(Message object) {
        if (object.hasMedia()) {
            if (object.getMedia().getType().equals("image/jpeg") || object.getMedia().getType().equals("image/png")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoMessage(Message object) {
        if (object.hasMedia()) {
            if (object.getMedia().getType().equals("video/mp4")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlainTextMessage(int type) {
        return type == TYPE_INCOMING_PLAIN_TEXT || type == TYPE_OUTGOING_PLAIN_TEXT;
    }

}
