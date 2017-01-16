package com.oit.ray.carefortheelderly;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CONTACTS = 1;
    private LocationManager mLocationManager;

    //城市代碼
    String cityArray[][] = {
            {"TaipeiCity", "Taipei", "Taoyuan", "Taichung", "Tainan", "KaohsiungCity",
            "Keelung", "HsinchuCity", "ChiayiCity",
            "Hsinchu", "Miaoli", "Changhua", "Nantou", "Yunlin", "Chiayi",
            "Pingtung", "Yilan", "Hualien", "Taitung",
            "Penghu", "Kinmen", "Matsu"},
            {"28751958", "90717580", "2306254", "2306181", "2306182", "2306180",
             "2306188", "2306185", "2296315",
             "2306185", "91290404", "91290191", "91290405", "28776037", "2296315",
             "91290319", "91290369", "91290403", "91290354",
             "56810980", "12517930", "90413470"},
            {"台北市", "新北市", "桃園市", "台中市", "台南市", "高雄市",
             "基隆市", "新竹市", "嘉義市",
             "新竹縣", "苗栗縣", "彰化縣", "南投縣", "雲林縣", "嘉義縣",
             "屏東縣", "宜蘭縣", "花蓮縣", "台東縣",
             "澎湖縣", "金門縣", "連江縣"}};
    int city_num = 0;
    //  Yahoo氣象(新北市)
    String url_y = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D" + cityArray[1][0] + "&format=json";
    //                  https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20%0Awhere%20url%3D%27http%3A%2F%2Fwww.cwb.gov.tw%2FV7%2Fforecast%2Ff_index.htm%27%0Aand%20xpath%3D%27%2F%2Ftr%5B%40id%3D%22TaipeiCityList%22%5D%27&format=json
    String url_c = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20%0Awhere%20url%3D%27http%3A%2F%2Fwww.cwb.gov.tw%2FV7%2Fforecast%2Ff_index.htm%27%0Aand%20xpath%3D%27%2F%2Ftr%5B%40id%3D%22" + cityArray[0][0] + "List%22%5D%27&format=json";
    String city, sunrise, sunset, nowTemp, highTemp, lowTemp, rain;

    TextView tvWeather,header_mail,header_name;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //  宣告SQLite
    private ItemDAO itemDAO;
    //  宣告Item
    private  Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //6.0以上權限詢問
        int[] permission = new int[]{ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION),
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)};

        if (permission[0] != PackageManager.PERMISSION_GRANTED ||
                permission[1] != PackageManager.PERMISSION_GRANTED ||
                permission[2] != PackageManager.PERMISSION_GRANTED) {
            System.out.println("未取得權限");
            //未取得權限，向使用者要求允許權限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    REQUEST_CONTACTS);
        } else {
            System.out.println("已有權限");
        }

        openGPS(this);

        tvWeather = (TextView) findViewById(R.id.tvWeather);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    }

    String AdminArea = "";

    protected void thread_showWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("TODO showWeather()");
                try {
                    //註冊LocationManage
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    System.out.println(loc);
                    String GPSx = "";
                    String GPSy = "";


                    if (loc != null) {
                        double lat = loc.getLatitude();//取得緯度
                        double lng = loc.getLongitude();//取得經度
                        GPSx = String.valueOf(lng);
                        GPSy = String.valueOf(lat);

                        //建立Geocoder物件
                        Geocoder gc = new Geocoder(MainActivity.this, Locale.TRADITIONAL_CHINESE);//地區：台灣
                        //自經緯度取得地址
                        List<Address> lstAddress = gc.getFromLocation(lat, lng, 1);
                        AdminArea = lstAddress.get(0).getAdminArea();

                        System.out.println(lat);
                        System.out.println(lng);
                        System.out.println("AdminArea:" + AdminArea);
                    }

                    for(int i = 0;i<cityArray[2].length;i++){
                        if(cityArray[2][i].equals(AdminArea)) {
                            city_num = i;
                            break;
                        }
                    }
                    url_y = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D" + cityArray[1][city_num] + "&format=json";
                    //                  https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20%0Awhere%20url%3D%27http%3A%2F%2Fwww.cwb.gov.tw%2FV7%2Fforecast%2Ff_index.htm%27%0Aand%20xpath%3D%27%2F%2Ftr%5B%40id%3D%22TaipeiCityList%22%5D%27&format=json
                    url_c = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20%0Awhere%20url%3D%27http%3A%2F%2Fwww.cwb.gov.tw%2FV7%2Fforecast%2Ff_index.htm%27%0Aand%20xpath%3D%27%2F%2Ftr%5B%40id%3D%22" + cityArray[0][city_num] + "List%22%5D%27&format=json";


//                    ===========================================================================


                    JsonParser jParser = new JsonParser();
//                  地區
                    JSONObject channel = jParser.getJSONFromUrl(url_y).getJSONObject("query").getJSONObject("results").getJSONObject("channel");
                    city = channel.getJSONObject("location").getString("city");
                    System.out.println("城市:" + city);
//                  本日日出日落
                    sunrise = channel.getJSONObject("astronomy").getString("sunrise");
                    sunset = channel.getJSONObject("astronomy").getString("sunset");
                    System.out.println("日出時間:" + sunrise);
                    System.out.println("日落時間:" + sunset);
//                  溫度
                    JSONObject item = channel.getJSONObject("item");
//                  現在溫度
                    String nowTemp_s = item.getJSONObject("condition").getString("temp");
                    nowTemp = toC(nowTemp_s);//華氏轉攝氏
                    System.out.println("現在溫度:" + nowTemp);
//                  一周最高最低溫
                    JSONArray forecast = item.getJSONArray("forecast");
//                  本日最高最低溫
                    String highTemp_s = forecast.getJSONObject(0).getString("high");
                    highTemp = toC(highTemp_s);
                    System.out.println("本日最高溫度:" + highTemp);
//                  本日最低溫度
                    String lowTemp_s = forecast.getJSONObject(0).getString("low");
                    lowTemp = toC(lowTemp_s);
                    System.out.println("本日最低溫度:" + lowTemp);

                    JSONObject td = jParser.getJSONFromUrl(url_c).getJSONObject("query").getJSONObject("results").getJSONObject("tr").getJSONArray("td").getJSONObject(2);
                    rain = td.getJSONObject("a").getString("content");
                    System.out.println("降雨機率:" + rain);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String city,sunrise,sunset,nowTemp,highTemp,lowTemp;
                        tvWeather.setText("城市：" + city
                                + "\n日出時間：" + sunrise
                                + "\n日落時間：" + sunset
                                + "\n現在溫度：" + nowTemp
                                + "\n本日高溫：" + highTemp
                                + "\n本日低溫：" + lowTemp
                                + "\n降雨機率：" + rain
                                + "\n地區:" + AdminArea);
                    }
                });

            }
        }).start();
    }

    String toC(String s) {
        double c = ((Double.valueOf(s)) - 32) * 5 / 9;
        DecimalFormat df = new DecimalFormat("#.#℃");
        String dc = df.format(c);
        return dc;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // 建立資料庫物件
        itemDAO = new ItemDAO(getApplicationContext());
        item = itemDAO.get(Long.valueOf(1));

        header_name = (TextView)findViewById(R.id.header_name);
        header_mail = (TextView)findViewById(R.id.header_mail);
        header_name.setText(item.getName());
        header_mail.setText(item.getEmail());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.oit.ray.carefortheelderly/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

        thread_showWeather();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.oit.ray.carefortheelderly/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }





    public void openGPS(Context context) {
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Toast.makeText(context, "GPS: " + gps + ", Network : " + network, Toast.LENGTH_SHORT).show();
        if (gps || network) {
            return;
        } else {
            Intent gpsOptionsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("取得權限");
                } else {
                    System.out.println("拒絕權限");
                    new AlertDialog.Builder(this)
                            .setMessage("必須開啟權限才能繼續使用本APP")
                            .setPositiveButton("OK", null)
                            .show();
                }
                return;
        }
    }
}
