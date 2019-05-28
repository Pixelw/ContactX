package com.pixel.mycontact;

import com.pixel.mycontact.beans.People;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PeopleResolver {
    public static String urlHeader = "pixel://mct?";
    public static String jsonQueryPara = "json=";
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
}
