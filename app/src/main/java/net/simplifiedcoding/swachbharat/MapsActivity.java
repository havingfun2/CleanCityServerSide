package net.simplifiedcoding.swachbharat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import net.simplifiedcoding.swachbharat.models.MyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    ProgressDialog p;
    private static final String TAG = "1";
    private GoogleMap mMap;
    GPSTracker gps;
    double latitude;
    double longitude;
    PrefManager pref;

    String  json_string0,json_string1,json_string2;
    JSONObject jsonObject,JO;
    JSONArray jsonArray;
    String JSON_String;
    ImageButton check_status;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //       try{
        setContentView(R.layout.activity_maps);

        //Requesting storage permission
        checkAndRequestPermissions();



        check_status = (ImageButton) findViewById(R.id.imageButton1);

        pref = new PrefManager(getApplicationContext());
        pref.setEvent("0");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        check_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.setEvent("1");
                AlertDialogManager alert = new AlertDialogManager();
                ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(MapsActivity.this,
                            "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    // stop executing code by return
                    return;
                }
                else
                    new BackGroundTask().execute();




            }
        });



        //       // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment.getMapAsync(this);
 /*       }
       catch (Exception e){
            startActivity(new Intent(MapsActivity.this,TravelActivity.class));
        }*/

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(false);
        AlertDialogManager alert = new AlertDialogManager();
        ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(MapsActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        else
            new BackGroundTask().execute();

        // Add a marker in Sydney and move the camera


    }

    class BackGroundTask extends AsyncTask<Void, Void, String> {

        // params,progress,result
        String json_url[]=new String[1];

        @Override
        protected void onPreExecute() {// handled by UI threads
            json_url[0] = Constants.CONSUMER_URL;

             p = new ProgressDialog(MapsActivity.this);
            p.setMessage("Loading...");
            p.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }// can be used for displaying progress bars

        @Override
        protected String doInBackground(Void... voids) {// this carries out the background task
            // StringBuilder stringBuilder = new StringBuilder();
            try {

                StringBuilder stringBuilder = new StringBuilder();
                URL url = new URL(json_url[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


                while ((JSON_String = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_String + "\n");
                }
                json_string0 = stringBuilder.toString().trim();

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                return json_string0;    // trim deletes white space
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            json_string0 = result;
            try {

                JO = new JSONObject(json_string0);
                jsonArray = JO.optJSONArray("clean");
                int count = 0;
                String name, lat, lng,mobile,date,url;
                Integer status;
                String id;

                MyData.com_date = new ArrayList<String>();
                MyData.com_image = new ArrayList<String>();
                MyData.com_id = new ArrayList<String>();
                MyData.com_status = new ArrayList<Integer>();

                System.out.println(json_string0);

                while (count < jsonArray.length()) {
                    //  JSONObject JO = null;
                    try {
                        JO = jsonArray.getJSONObject(count);
                        name = JO.getString("name");
                        lat = JO.getString("latitude");
                        lng = JO.getString("longitude");
                        mobile = JO.getString("mobile");
                        status = JO.getInt("status");
                        id = JO.getString("complaint_id");
                        url = JO.getString("url");
                        date = JO.getString("date");




                            MyData.com_date.add(date);
                            MyData.com_status.add(status);
                            MyData.com_image.add(url);
                            MyData.com_id.add(id);
                            LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            mMap.addMarker(new MarkerOptions().position(sydney).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                        count++;

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }
                LatLngBounds GWALIOR = new LatLngBounds(new LatLng(26.104292, 78.111943), new LatLng(26.320862, 78.270518));

                p.dismiss();
      //          mMap.addCircle(new CircleOptions()).setRadius(100);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GWALIOR.getCenter(), 11));
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(26.2183, 78.1828))
                        .radius(10000)
                        .strokeColor(Color.BLACK)
                        .fillColor(0x42ffff00));

                if(pref.getEvent().equals("1")){
                    Intent i = new Intent(MapsActivity.this,CheckComplaint.class);
                    startActivity(i);
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }




    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}