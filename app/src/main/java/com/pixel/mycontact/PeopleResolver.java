package com.pixel.mycontact;

import android.util.Base64;

import com.pixel.mycontact.beans.People;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PeopleResolver {
    static String urlHeader = "pixel://mct?";
    static String jsonQueryPara = "json=";
    static String b64QueryData = "b64=";

    static People resolveJson(String json) {
        People peopleFromJson;
        JSONObject j = null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            j = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (j != null) {
            peopleFromJson = new People(j.optString("f"), j.optString("l"),
                    j.optString("n1"), j.optString("n2"), j.optString("e"),
                    j.optInt("y"), j.optInt("m"), j.optInt("d"),
                    j.optString("no"), -72);
            return peopleFromJson;
        }
        return null;
    }

    static People resolveBase64Json(String b64Json){
        String decoded = null;
        try {
            decoded = new String(Base64.decode(b64Json.getBytes(),Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resolveJson(decoded);
    }




}
