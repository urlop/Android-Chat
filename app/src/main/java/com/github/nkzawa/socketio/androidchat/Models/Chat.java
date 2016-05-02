package com.github.nkzawa.socketio.androidchat.Models;

import com.orm.SugarRecord;

/**
 * Created by harry on 1/05/2016.
 */
public class Chat extends SugarRecord {
    Object receiver;
    String chatType;
    Message lastMessage;
}
