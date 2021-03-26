package com.example.weatherapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class Weather implements Runnable {
    Context mContext;
    private double lat, lon;
    String APIKEY;
    String weatherUrl;

    String[] keywords = {"name", "temp", "temp_max", "temp_min", "main", "id"};
    Object[] results = new Object[keywords.length];


    public Weather(Context mContext, double lat, double lon) {
        this.mContext = mContext;
        APIKEY = mContext.getResources().getString(R.string.API_KEY);
        setLocation(lat, lon);
        setWeatherUrl();
    }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void setWeatherUrl() {
        weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + APIKEY + "&units=metric";
    }


    @Override
    public void run() {
        URL url;
        InputStream inputStream = null;
        try {
            setWeatherUrl();
            url = new URL(weatherUrl);

            inputStream = url.openStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream,
                            "UTF-8"));
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
            JSONObject jsonObject = new JSONObject(buffer.toString());
            JSONObject jsonObject_Temp = (JSONObject) jsonObject.get("main");
            JSONArray jsonObject_Weather = (JSONArray) jsonObject.get("weather");
            JSONObject weatherJSONObject = (JSONObject) jsonObject_Weather.get(0);

            results = new Object[keywords.length];
            Thread.sleep(50);

            for (int i = 0; i < keywords.length; i++) {
                if (i == 0) results[i] = jsonObject.getString(keywords[i]);
                if (i > 0 && i < 4) results[i] = jsonObject_Temp.getDouble(keywords[i]);
                if (i >= 4) results[i] = weatherJSONObject.getString(keywords[i]);
            }


        } catch (Exception e) {
            Log.d("GYI", "JSON 파싱 오류");
            e.printStackTrace();
        }
    }

    public String getResult(int i) {
        return results[i].toString();
    }
}