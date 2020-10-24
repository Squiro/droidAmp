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
    private long currentTime;

    public Event(String type_events, String description, long currentTime)
    {
        this.type_events = type_events;
        this.description = description;
        this.currentTime = currentTime;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (this.type_events != null)
            jsonObject.put("type_events", this.type_events);
        if (this.description != null)
            jsonObject.put("description", this.description);

        return  jsonObject;
    }

    @NonNull
    @Override
    public String toString() {
        return new String(this.type_events + ": " + this.description + " at " +
                String.format(Locale.ENGLISH, "%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(currentTime),
                TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime)))
        );
    }
}
