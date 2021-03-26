package com.example.weatherapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private GpsTracker gpsTracker = null;
    private Button checkWeatherBtn;
    private ImageView weatherImg;
    private TextView weatherMsg;
    private double latitude, longitude;
    Weather weatherTask;

    private static final int GPS_ENABLE_REQUEST_CODE = 1997;
    private static final int PERMISSIONS_REQUEST_CODE = 603;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};

    public void getLocation() {
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsTracker = new GpsTracker(MainActivity.this);
        if (gpsTracker != null) getLocation();


        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }

        checkWeatherBtn = findViewById(R.id.btnRefresh);
        weatherImg = findViewById(R.id.weatherImg);
        weatherMsg = findViewById(R.id.weatherMsg);

        weatherTask = new Weather(this, latitude, longitude);

        checkWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherTask.setLocation(latitude, longitude);
                getLocation();
                getWeather();
            }
        });
        getWeather();
    }

    public void getWeather() {
        try {
            Thread th = new Thread(weatherTask);
            th.start();
            String[] results = new String[weatherTask.results.length];

            for (int i = 0; i < weatherTask.results.length; i++) {
                results[i] = weatherTask.getResult(i);
                Log.d("GYI", results[i]);
            }
            msgCreator(Integer.parseInt(results[5]), results);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GYI", "메인에서 받기 오류");
            getWeather();
        }
    }

    /*
     * 날씨 상황에 맞게 ImageView를 Vector 이미지로 바꿔준다.
     * 비상업적이나 혹시몰라 gitignore에 추가해뒀습니다.
     */
    public void msgCreator(int code, String[] results) {
        String weatherName = "";
        if (code == 800) {
            weatherName = "티없이 맑네요.";
            weatherImg.setImageResource(R.drawable.sun);
        } else if ((code / 10) >= 95 && (code / 100) <= 96) {
            switch (code / 10) {
                case 95:
                    weatherName = "좋네요 대충.";
//                    url += "50d.png";
                    weatherImg.setImageResource(R.drawable.atmosphere);
                    break;

                case 96:
                    weatherName = "와 날씨가 미침요... 집에 꼭 박혀있으세요.";
                    weatherImg.setImageResource(R.drawable.death);
                    break;

            }
        } else {
            switch (code / 100) {
                case 2:
                    weatherName = "천둥 번개가 치네요.";
                    weatherImg.setImageResource(R.drawable.thunderstorm);
                    break;

                case 3:
                    weatherName = "얕은 비가 오네요.";
                    weatherImg.setImageResource(R.drawable.rain);
                    break;


                case 5:
                    weatherName = "비가 오네요.";
                    weatherImg.setImageResource(R.drawable.rain);
                    break;

                case 6:
                    weatherName = "눈이 오네요.";
                    weatherImg.setImageResource(R.drawable.snow);
                    break;

                case 7:
                    weatherName = "그냥저냥 그런 날씨네요.";
                    weatherImg.setImageResource(R.drawable.atmosphere);
                    break;

                case 8:
                    weatherName = "구름이 꼈네요.";
                    weatherImg.setImageResource(R.drawable.cloud);
                    break;

                case 9:
                    weatherName = "날씨고 뭐고 집에 나가지 마세요";
                    weatherImg.setImageResource(R.drawable.death);
                    break;
            }
        }
//        task = new ImageLoadTask(weatherImg);
//        task.execute(url);
        weatherMsg.setText("내 도시 : " + results[0]
                + "\n현재 온도 : " + results[1]
                + "°C\n 오늘 날씨는 " + weatherName
                + "\n 오늘 최고 기온 : " + results[2]
                + "°C\n 오늘 최저 기온 : " + results[3]
                + "\n");
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