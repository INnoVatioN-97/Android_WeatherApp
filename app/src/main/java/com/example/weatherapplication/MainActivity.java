package com.example.weatherapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private GpsTracker gpsTracker = null;
    private CovidInfo covidInfo = null;
    private ImageView ivWeather;
    private TextView tvWeather, tvCityName, tvMin_MaxTemp, tvCovidInfo;
    private double latitude, longitude;
    private FrameLayout mainLayout;
    private Item item; //내 위치의 코로나 정보가 담김.
    Weather weatherTask;

    private static final int GPS_ENABLE_REQUEST_CODE = 1997;
    private static final int PERMISSIONS_REQUEST_CODE = 603;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};

    public void getLocation() {
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        weatherTask.setLocation(latitude, longitude);
        covidInfo.setLocation(latitude, longitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //먼저 권한 확인
        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }

        ivWeather = findViewById(R.id.ivWeather);
        tvWeather = findViewById(R.id.tvWeather);
        tvCityName = findViewById(R.id.tvCityName);
        tvCovidInfo = findViewById(R.id.tvCovidInfo);
        tvMin_MaxTemp = findViewById(R.id.tvMin_MaxTemp);

        mainLayout = findViewById(R.id.mainLayout); //activity_main
        longitude = latitude = 0.0; //초기 값

        weatherTask = new Weather(this);
        covidInfo = new CovidInfo(this);
        gpsTracker = new GpsTracker(MainActivity.this);

        if (gpsTracker != null) getLocation();
//        item = covidInfo.getCovidInfo();
        item = new Item();
        getWeather();
//        Log.d("GYI", String.valueOf(str));
    }

    public void getWeather() {
//        Log.d("GYI", item.toString());
        getLocation();
        try {
            String result = weatherTask.execute().get();
            String[] keywords = {"name", "temp", "temp_max", "temp_min", "main", "id"};
            Object[] results;

            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonObject_Temp = (JSONObject) jsonObject.get("main");
            JSONArray jsonObject_Weather = (JSONArray) jsonObject.get("weather");
            JSONObject weatherJSONObject = (JSONObject) jsonObject_Weather.get(0);

            results = new Object[keywords.length];

            for (int i = 0; i < keywords.length; i++) {
                if (i == 0) results[i] = jsonObject.getString(keywords[i]);
                if (i > 0 && i < 4) results[i] = jsonObject_Temp.getDouble(keywords[i]);
                if (i >= 4) results[i] = weatherJSONObject.getString(keywords[i]);
            }
//            if(covidInfo.getCovidInfo() != null)
//                item = covidInfo.getCovidInfo();
//            else item = null;
            item = covidInfo.getCovidInfo();

            msgCreator(Integer.parseInt(results[5].toString()), results);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GYI", "메인에서 받기 오류");
        }
    }

    /*
     * 날씨 상황에 맞게 ImageView를 Vector 이미지로 바꿔준다.
     * 비상업적이나 혹시몰라 gitignore에 추가해뒀습니다.
     */
    public void msgCreator(int code, Object[] results) {
        String weatherName = "";
        if (code == 800) {
            weatherName = "티없이 맑네요.";
            ivWeather.setImageResource(R.drawable.sun);
            mainLayout.setBackgroundResource(R.drawable.sunny_gradient);
        } else if ((code / 10) >= 95 && (code / 100) <= 96) {
            switch (code / 10) {
                case 95:
                    weatherName = "좋네요 대충.";
//                    url += "50d.png";
                    ivWeather.setImageResource(R.drawable.atmosphere);
                    mainLayout.setBackgroundResource(R.drawable.sunny_gradient);
                    break;

                case 96:
                    weatherName = "와 날씨가 미침요... 집에 꼭 박혀있으세요.";
                    ivWeather.setImageResource(R.drawable.death);
                    mainLayout.setBackgroundResource(R.drawable.dark_gradient);
                    break;

            }
        } else {
            switch (code / 100) {
                case 2:
                    weatherName = "천둥 번개가 치네요.";
                    ivWeather.setImageResource(R.drawable.thunderstorm);
                    mainLayout.setBackgroundResource(R.drawable.storm_gradient);
                    break;

                case 3:
                    weatherName = "얕은 비가 오네요.";
                    ivWeather.setImageResource(R.drawable.rain);
                    mainLayout.setBackgroundResource(R.drawable.rainy_gradient);
                    break;


                case 5:
                    weatherName = "비가 오네요.";
                    ivWeather.setImageResource(R.drawable.rain);
                    mainLayout.setBackgroundResource(R.drawable.rainy_gradient);
                    break;

                case 6:
                    weatherName = "눈이 오네요.";
                    ivWeather.setImageResource(R.drawable.snow);
                    mainLayout.setBackgroundResource(R.drawable.snowy_gradient);
                    break;

                case 7:
                    weatherName = "그냥저냥 그런 날씨네요.";
                    ivWeather.setImageResource(R.drawable.atmosphere);
                    mainLayout.setBackgroundResource(R.drawable.cloudy_gradient);
                    break;

                case 8:
                    weatherName = "구름이 꼈네요.";
                    ivWeather.setImageResource(R.drawable.cloud);
                    mainLayout.setBackgroundResource(R.drawable.cloudy_gradient);

                    break;

                case 9:
                    weatherName = "날씨고 뭐고 집에 나가지 마세요";
                    ivWeather.setImageResource(R.drawable.death);
                    break;
            }
        }
//        tvCityName.setText(results[0].toString());
        tvCityName.setText(covidInfo.getCityName());
        // String str_Weather : 날씨
        // String str_Temp : 온도정보들
        // String str_Covid : 야로나 관련 정보
        String strTemp = "";
        String strWeather = "";
        String strCovid = "";
        strTemp = "현재 온도 : " + results[1]
                + "°C\n\n오늘 최고 기온 : " + results[2]
                + "°C\n오늘 최저 기온 : " + results[3];
        //일교차
        double tempDiff = Double.parseDouble(String.format("%.1f", (double) results[2] - (double) results[3]));
        //double per = Double.parseDouble(String.format("%.2f",per2));
        strTemp += "\n\n오늘 일교차 : " + tempDiff + "°C";
        if (item != null) {
            strCovid += "" + item.getGubun() + " 지역 코로나 정보\n"
                    + "총 사망자 : " + item.getDeathCnt()
                    + "\n확진자 증가 추세 : " + (Integer.parseInt(item.getIncDec()) > 0 ? Math.abs(Integer.parseInt(item.getIncDec())) + "명 증가" : Math.abs(Integer.parseInt(item.getIncDec())) + "명 감소")
                    + "\n격리중 : " + item.getIsolIngCnt()
                    + "\n지역 발생 환자 수 : " + item.getLocalOccCnt();
            tvCovidInfo.setText(strCovid);
        } else {
//            tvCovidInfo.setText("이 지역에 코로나 현황으로 검색된 결과 없음.");
            tvCovidInfo.setEnabled(false);
            Toast.makeText(this, "해당지역에 현재 수신된 코로나 현황이 검색되지 않음.", Toast.LENGTH_SHORT).show();
        }
        tvWeather.setText(weatherName);
        tvMin_MaxTemp.setText(strTemp);
    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasInternetPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasInternetPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
//            Toast.makeText(this, "권한 승인됨", Toast.LENGTH_LONG).show();

            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}