package com.unlam.droidamp.models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Event {

    /** Events types */
    public static final String TYPE_LOGIN = "Login";
    public static final String TYPE_REGISTER = "Register";
    public static final String TYPE_BROADCAST = "Broadcast";
    public static final String TYPE_BACKGROUND = "Background";
    public static final String TYPE_SENSOR = "Sensor";

    private String type_events;
    private String description;

    public Event(String type_events, String description)
    {
        this.type_events = type_events;
        this.description = description;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (this.type_events != null)
            jsonObject.put("type_events", this.type_events);
        if (this.description != null)
            jsonObject.put("description", this.description);

        return  jsonObject;
    }
}
