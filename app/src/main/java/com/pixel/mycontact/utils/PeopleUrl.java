package com.pixel.mycontact.utils;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.result.URLTOResultParser;
import com.pixel.mycontact.beans.People;

import java.net.URL;

public class PeopleUrl {

    public static final int TYPE_FLAT_JSON = 1;
    public static final int TYPE_B64 = 2;
    public static final int TYPE_AES_PSK = 3;

    public static final String URL_HEADER = "pixelw://mct?";
    public static final String JSON_QUERY_PARA = "json=";
    public static final String B64_QUERY_PARA = "b64=";

    public static People deserializePeopleObj(String json) {
        People people = null;
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd H:mm:ss")
                    .create();
            people= gson.fromJson(json,People.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return people;
    }

    public static People parseUrl (String url){

        if (url.startsWith(URL_HEADER)){
            if (url.substring(URL_HEADER.length()).startsWith(JSON_QUERY_PARA)){
                Uri uri = Uri.parse(url);
                return deserializePeopleObj(uri.getQueryParameter("json"));
            }else if (url.substring(URL_HEADER.length()).startsWith(B64_QUERY_PARA)){
                Uri uri = Uri.parse(url);
                return resolveBase64Json(uri.getQueryParameter("b64"));
            }
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
        return deserializePeopleObj(decoded);
    }

    public static String generateUrl(People people, int format) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd H:mm:ss")
                .create();
        String jsonPeople = gson.toJson(people);
        LogUtil.d("GsonPeople",jsonPeople);
        String stringContent = "";
        switch (format) {
            case TYPE_FLAT_JSON:
                stringContent = PeopleUrl.URL_HEADER + PeopleUrl.JSON_QUERY_PARA + jsonPeople;
                break;
            case TYPE_B64:
                stringContent = PeopleUrl.URL_HEADER + PeopleUrl.B64_QUERY_PARA +
                        Base64.encodeToString(jsonPeople.getBytes(), Base64.DEFAULT);
                break;
            case TYPE_AES_PSK:
                Log.e("PeopleURL", "generateUrl: AES haven't implement yet");
                break;
            default:
                break;

        }
        LogUtil.d("generateQR: length", String.valueOf(stringContent.length()));
        return stringContent;
    }

}
