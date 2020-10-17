package com.unlam.droidamp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private String lastname;
    private Integer dni;
    private String email;
    private String password;
    private Integer commission;

    public User(String name, String lastname, int dni, String email, String password, int commission)
    {
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.commission = commission;
    }

    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (this.email != null)
            jsonObject.put("email", this.email);
        if (this.password != null)
            jsonObject.put("password", this.password);
        if (this.name != null)
            jsonObject.put("name", this.name);
        if (this.lastname != null)
            jsonObject.put("lastname", this.lastname);
        if (this.commission != null)
            jsonObject.put("commission", this.commission);
        if (this.dni != null)
            jsonObject.put("dni", this.dni);

        return  jsonObject;
    }
}
