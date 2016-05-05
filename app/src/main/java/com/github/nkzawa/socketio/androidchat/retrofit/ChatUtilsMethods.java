package com.github.nkzawa.socketio.androidchat.retrofit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;

import com.github.nkzawa.socketio.androidchat.Chat.ChatActivity;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Message;
import com.github.nkzawa.socketio.androidchat.R;
import com.google.gson.JsonObject;

/**
 * Created by rubymobile on 5/05/16.
 */
public class ChatUtilsMethods {

    public static boolean isUserInsideChat(JsonObject gsonObject, Chat chat){
        boolean isUserInsideChat = false;

        if(chat.getChatType().equals(Constants.ROOM_CHAT)){
            if(gsonObject.has("room")){
                int roomId = gsonObject.get("room").getAsInt();
                if(roomId == chat.getReceiverId()){
                    isUserInsideChat = true;
                }
            }
        }else{
            if(gsonObject.has("user")){
                JsonObject jsonObjectSender = gsonObject.get("user").getAsJsonObject();
                int userId = jsonObjectSender.get("id").getAsInt();
                if(userId == chat.getReceiverId()){
                    isUserInsideChat = true;
                }
            }
        }

        return isUserInsideChat;
    }

    public static Chat getChatFromNewMessage(JsonObject gsonObject){

        Chat newChat = null;

        if(gsonObject.has("room")){
            int roomId = gsonObject.get("room").getAsInt();
            newChat = Chat.createChat(roomId,Constants.ROOM_CHAT);

        }else{
            if(gsonObject.has("user")){
                JsonObject jsonObjectSender = gsonObject.get("user").getAsJsonObject();
                int userId = jsonObjectSender.get("id").getAsInt();
                newChat = Chat.createChat(userId,Constants.USER_CHAT);

            }
        }
        return newChat;
    }


    public static void createNewMessageNotification(Context context, Chat chat, Message message){
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("receiverId", chat.getReceiverId());
        intent.putExtra("typeChat", chat.getChatType());

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        int countNotifiation = 0;

        PendingIntent contentIntent = PendingIntent.getActivity(context, countNotifiation, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("New Message")
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(getNotificationIcon())).getBitmap())
                .setSmallIcon(getNotificationIcon() )
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message.getMessage()))
                //  .setWhen(System.currentTimeMillis())
                .setContentText(message.getMessage());


        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(countNotifiation, notification);

    }

    private static int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_launcher : R.drawable.ic_launcher;
    }
}
