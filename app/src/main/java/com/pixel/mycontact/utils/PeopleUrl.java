package com.pixel.mycontact.utils;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixel.mycontact.beans.People;

import org.json.JSONArray;
import org.json.JSONObject;

public class PeopleUrl {

    public static final int TYPE_FLAT_JSON = 1;
    public static final int TYPE_B64 = 2;
    public static final int TYPE_AES_PSK = 3;

    public static final String URL_HEADER = "pixel://mct?";
    public static final String JSON_QUERY_PARA = "json=";
    public static final String B64_QUERY_PARA = "b64=";

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

    public static String generateUrl(People people, int format) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd H:mm:ss")
                .create();

        String stringContent = "";
        switch (format) {
            case TYPE_FLAT_JSON:
                stringContent = PeopleUrl.URL_HEADER + PeopleUrl.JSON_QUERY_PARA + people.toJSON();
                break;
            case TYPE_B64:
                stringContent = PeopleUrl.URL_HEADER + PeopleUrl.B64_QUERY_PARA +
                        Base64.encodeToString(people.toJSON().getBytes(), Base64.DEFAULT);
                break;
            case TYPE_AES_PSK:
                Log.e("PeopleURL", "generateUrl: AES haven't implement yet");
                break;
            default:
                break;

        }
        Log.d("generateQR: length", String.valueOf(stringContent.length()));
        return stringContent;
    }

}
