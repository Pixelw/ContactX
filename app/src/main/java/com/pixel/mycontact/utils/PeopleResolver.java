package com.pixel.mycontact.utils;

import android.util.Base64;

import com.pixel.mycontact.beans.People;

import org.json.JSONArray;
import org.json.JSONObject;

public class PeopleResolver {
    public static String urlHeader = "pixel://mct?";
    public static String jsonQueryPara = "json=";
    public static String b64QueryData = "b64=";

    public static People resolveJson(String json) {
        People peopleFromJson;
        JSONObject jsonObject;
        try {
            JSONArray jsonArray = new JSONArray(json);
            jsonObject = jsonArray.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (jsonObject != null) {
            peopleFromJson = new People(jsonObject.optString("f"), jsonObject.optString("l"),
                    jsonObject.optString("n1"), jsonObject.optString("n2"), jsonObject.optString("e"),
                    jsonObject.optInt("y"), jsonObject.optInt("m"), jsonObject.optInt("d"),
                    jsonObject.optString("no"), -72);
            return peopleFromJson;
        }
        return null;
    }

    public static People resolveBase64Json(String b64Json) {
        String decoded = null;
        try {
            decoded = new String(Base64.decode(b64Json.getBytes(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resolveJson(decoded);
    }


}
