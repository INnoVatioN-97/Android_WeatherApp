package com.example.weatherapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Weather extends AsyncTask<String, Void, String> {
    Context mContext;
    private double lat, lon;
    String APIKEY;
    String weatherUrl;

//    String[] keywords = {"name", "temp", "temp_max", "temp_min", "main", "id"};
//    Object[] results = new Object[keywords.length];


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
    public String doInBackground(String... urls) {
        URL url;
        String str = null;
        try {
            setWeatherUrl();
            url = new URL(weatherUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
//                JSONObject jsonObject = new JSONObject(buffer.toString());
//                JSONObject jsonObject_Temp = (JSONObject) jsonObject.get("main");
//                JSONArray jsonObject_Weather = (JSONArray) jsonObject.get("weather");
//                JSONObject weatherJSONObject = (JSONObject) jsonObject_Weather.get(0);
//
//                results = new Object[keywords.length];
//
//                for (int i = 0; i < keywords.length; i++) {
//                    if (i == 0) results[i] = jsonObject.getString(keywords[i]);
//                    if (i > 0 && i < 4) results[i] = jsonObject_Temp.getDouble(keywords[i]);
//                    if (i >= 4) results[i] = weatherJSONObject.getString(keywords[i]);
//                }
                str = buffer.toString();
                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GYI", "JSON 파싱 오류");
        }
        return str;
    }

//    public String getResult(int i) {
//        return results[i].toString();
//    }
}