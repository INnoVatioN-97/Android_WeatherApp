package com.example.weatherapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Weather extends AsyncTask<String, Void, String> {
    private Context mContext;
    private double lat, lon;
    private String APIKEY;
    private String weatherUrl;

    public Weather(Context mContext, double lat, double lon) {
        this.mContext = mContext;
        APIKEY = mContext.getResources().getString(R.string.API_KEY);
        setLocation(lat, lon);
    }

    public Weather(Context mContext) {
        this.mContext = mContext;
        APIKEY = mContext.getResources().getString(R.string.API_KEY);
        lat = lon = 0.0;
    }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        setWeatherUrl(lat, lon);
    }

    public void setWeatherUrl(double lat, double lon) {
        weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + APIKEY + "&units=metric";
    }


    @Override
    public String doInBackground(String... urls) {

        URL url;
        String str = null;
        try {
            setWeatherUrl(lat, lon);
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
}