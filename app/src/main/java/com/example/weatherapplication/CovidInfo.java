package com.example.weatherapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CovidInfo {
    private Context mContext;
    private double lon = 0, lat = 0;
    private Geocoder geocoder;
    private String API_KEY;

    public CovidInfo(Context mContext) {
        this.mContext = mContext;
        API_KEY = mContext.getResources().getString(R.string.COVID19_API_KEY);
        geocoder = new Geocoder(mContext);
    }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getCity(String cityName) {
        switch (cityName) {
            case "서울특별시":
                cityName = "서울";
                break;
            case "충청남도":
                cityName = "충남";
                break;
            case "충청북도":
                cityName = "충북";
                break;
            case "전라북도":
                cityName = "전북";
                break;
            case "전라남도":
                cityName = "전남";
                break;
            case "경상남도":
                cityName = "경남";
                break;
            case "경상북도":
                cityName = "경북";
                break;
            case "강원도":
                cityName = "강원";
                break;
            case "부산광역시":
                cityName = "부산";
                break;
            case "인천광역시":
                cityName = "인천";
                break;
            case "경기도":
                cityName = "경기";
                break;
            case "대구광역시":
                cityName = "대구";
                break;
        }
        return cityName;
    }

    public Item getCovidInfo() { // {"위치1(도 / 특별(광역)시)", "오늘의 확진자", "어제의 확진자"}
        List<Address> listA = null;
        Item myItem;
        String[] strs = null;
        GetCOVIDAsync getCOVIDAsync = new GetCOVIDAsync(API_KEY);
        try {
            listA = geocoder.getFromLocation(lat, lon, 10);
//            Log.d("GYI", listA.get(0).getAdminArea());
            String city = getCity(listA.get(0).getAdminArea());
//            Log.d("GYI", city);
            List<Item> itemList = getCOVIDAsync.execute().get();

//            Log.d("GYI", "리스트 크기: "+itemList.size());
            for (Item i : itemList) {
                if (i.getGubun().equals(city)) {
//                    Log.d("GYI", i.getGubun() + "찾음");
                    myItem = i;
                    return myItem;
                }
            }
//            if(itemList.get(0).getGubun())
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class GetCOVIDAsync extends AsyncTask<String, String, List<Item>> {
    String API_KEY;

    public GetCOVIDAsync(String API) {
        API_KEY = API;
    }

    @Override
    protected List<Item> doInBackground(String... strings) {
        ArrayList<Item> list = null;
        Item covid = null;
        boolean item = false,
                gubun = false,
                deathCnt = false,
                incDec = false,
                defCnt = false,
                isolIngCnt = false,
                localOccCnt = false,
                url_ = false;
        //xml 파싱을 시작한다.
        //xml 파싱은 시작태그, 종료태그, 내용태그를 구분하는 것이 기본이다.
        //다음 태그로 넘어갈 때 while문을 한번 돌게 된다.
        //원하는 시작태그가 나오면 그 태그에 맞는 boolean을 true로 해준다.
        //목표한 태그가 true값을 가지게 되면 내용태그(내용)를 백터에 추가한다.
        try {
            Date currentTime = Calendar.getInstance().getTime();
            String startDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentTime);
            StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode(API_KEY, "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("startCreateDt", "UTF-8") + "=" + URLEncoder.encode(startDate, "UTF-8")); /*검색할 생성일 범위의 시작*/
            urlBuilder.append("&" + URLEncoder.encode("endCreateDt", "UTF-8") + "=" + URLEncoder.encode(startDate, "UTF-8")); /*검색할 생성일 범위의 종료*/
            URL url = new URL(urlBuilder.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            InputStream input;

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                input = conn.getInputStream();
            } else {
                input = conn.getErrorStream();
            }

            conn.disconnect();

            XmlPullParserFactory parsers = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parsers.newPullParser();
            parser.setInput(input, "UTF-8");

            int type = parser.getEventType();
//            Log.d("GYI", "파싱을 시작.");
            //데이터 분석 시작, 한번에 한 개의 태그를 분석한다.
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                //파싱한 데이터의 타입 변수를 저장한다. 시작태그, 텍스트태그, 종료태그를 구분한다.

                //조건에 맞는 데이터가 발견되면 각 데이터에 맞게 대입한다.
                switch (type) {

                    case XmlPullParser.START_DOCUMENT:
                        list = new ArrayList<>();

                        break;
                    case XmlPullParser.END_DOCUMENT:

                        break;

                    case XmlPullParser.END_TAG:

                        if (parser.getName().equals("item")
                                && covid != null) {
                            list.add(covid);
                        }
                        break;

                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("item"))
                            covid = new Item();
                        if (parser.getName().equals("gubun"))
                            gubun = true;
                        if (parser.getName().equals("deathCnt"))
                            deathCnt = true;
                        if (parser.getName().equals("incDec"))
                            incDec = true;
                        if (parser.getName().equals("isolIngCnt"))
                            isolIngCnt = true;
                        if (parser.getName().equals("localOccCnt"))
                            localOccCnt = true;
                        if (parser.getName().equals("defCnt"))
                            defCnt = true;
                        break;


                    case XmlPullParser.TEXT:
                        if (gubun) {
                            covid.setGubun(parser.getText());
                            gubun = false;
                        } else if (deathCnt) {
                            covid.setDeathCnt(parser.getText());
                            deathCnt = false;
                        } else if (incDec) {
                            covid.setIncDec(parser.getText());
                            incDec = false;
                        } else if (isolIngCnt) {
                            covid.setIsolIngCnt(parser.getText());
                            isolIngCnt = false;
                        } else if (localOccCnt) {
                            covid.setLocalOccCnt(parser.getText());
                            localOccCnt = false;
                        } else if (defCnt) {
                            covid.setDefCnt(parser.getText());
                            defCnt = false;
                        }
                        break;
                }

                type = parser.next();
            }
        } catch (XmlPullParserException ex) {
            Log.d("GYI", "XmlPullParserException");
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            Log.d("GYI", "MalformedURLException");
            ex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}