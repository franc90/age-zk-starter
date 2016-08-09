package org.age.zk.services.communication.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageSerializer {

    public static String serialize(Message message) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(message);
    }

    public static Message deserialize(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, Message.class);
    }

}
