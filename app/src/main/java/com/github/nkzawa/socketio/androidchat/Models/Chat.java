package com.github.nkzawa.socketio.androidchat.Models;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.R;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by harry on 1/05/2016.
 */
public class Chat extends SugarRecord {
    int receiverId;
    String chatType;
    String lastMessage;

    // No savedData
    @Ignore
    private boolean isTyping= false;

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }


    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }


    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public static Chat getChat(int receiverId, String chatType){
        List<Chat> chats = Chat.find(Chat.class, "receiver_id = ? and chat_type = ?", ""+receiverId, chatType);
        if(chats.isEmpty()){
            return null;
        }else{
            return chats.get(0);
        }
    }

    public static Chat createChat(int receiverId, String chatType){
        Chat chat = getChat(receiverId, chatType);
        if(chat == null){
            chat = new Chat();
            chat.setReceiverId(receiverId);
            chat.setChatType(chatType);
            chat.save();
        }
        return chat;
    }

    public Object getReceiver(){
        Object object = null;
        if(chatType.equals(Constants.USER_CHAT)){
            List<User> users = User.find(User.class, "user_id = ?", ""+receiverId);
            object = users.get(0);
        }else{
            List<Room> rooms = Room.find(Room.class, "room_id = ?", ""+receiverId);
            object = rooms.get(0);
        }

        return object;
    }

    public List<Message> getMessages() {
        return Message.find(Message.class, "chat = ?", ""+getId());
    }


}
