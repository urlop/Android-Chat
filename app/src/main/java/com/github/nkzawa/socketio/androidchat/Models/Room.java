package com.github.nkzawa.socketio.androidchat.Models;

import com.google.gson.JsonObject;
import com.orm.SugarRecord;

/**
 * Created by rubymobile on 22/04/16.
 */
public class Room extends SugarRecord {

    int roomId;
    String name;
    String socked_id;
    int owner_id;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getSocked_id() {
        return socked_id;
    }

    public void setSocked_id(String socked_id) {
        this.socked_id = socked_id;
    }


    public static class Builder {
        private int mId;
        private String mName;
        private String mSocked_id;
        private int mOwner_id;


        public Builder(int id) {
            mId = id;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder socket(String socked_id) {
            mSocked_id = socked_id;
            return this;
        }

        public Builder owner(int owner_id) {
            mOwner_id = owner_id;
            return this;
        }

        public Room build() {
            Room room = new Room();
            room.setRoomId(mId);
            room.setName(mName);
            room.setSocked_id(mSocked_id);
            room.setOwner_id(mOwner_id);
            return room;
        }
    }

    public static Room parseGroup(JsonObject responseObject) {

        Builder roomBuilder;
        roomBuilder = new Builder(responseObject.get("id").getAsInt());

        if (responseObject.has("name") && !responseObject.get("name").isJsonNull()) {
            roomBuilder.name(responseObject.get("name").getAsString());
        }

        if (responseObject.has("socked_id") && !responseObject.get("socked_id").isJsonNull()) {
            roomBuilder.socket(responseObject.get("socked_id").getAsString());
        }

        if (responseObject.has("owner_id") && !responseObject.get("owner_id").isJsonNull()) {
            roomBuilder.owner(responseObject.get("owner_id").getAsInt());
        }


        return roomBuilder.build();
    }
}
