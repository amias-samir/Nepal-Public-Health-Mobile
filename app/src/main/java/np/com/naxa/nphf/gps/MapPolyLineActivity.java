package np.com.naxa.nphf.gps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.model.CheckValues;
import np.com.naxa.nphf.model.StaticListOfCoordinates;

/**
 * Created by ramaan on 2/6/2016.
 */
public class MapPolyLineActivity extends FragmentActivity {
    static LatLng HAMBURG;
    private GoogleMap map;
    GpsTracer gps;
    static ArrayList<LatLng> list = new ArrayList<LatLng>();

    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    BroadcastReceiver mReceiver;
    Marker hamburg, startPoint;
    Boolean firstPass = false;
    public static boolean isPausedInPreview = false ;
    Boolean isReceiverRegister = true ;
    ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        list = StaticListOfCoordinates.getList();
        Log.e("GPS ARRAY", list.toString());

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

//        ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//                .getMapAsync(new OnMapReadyCallback() {
//                    @Override
//                    public void onMapReady(GoogleMap googleMap) {
//
//                        map = googleMap;
//                    }
//                });
        MapsInitializer.initialize(this);




        if(CheckValues.isFromSavedFrom){
            isReceiverRegister = false ;

            if(list.size()>0)
                HAMBURG = list.get(0);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(17), 1000, null);

//            startPoint = map.addMarker(new MarkerOptions().position(HAMBURG)
//                    .title("Start"));

            startPoint = map.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("My Saved Location")
                    .snippet("GPS Saved Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)));

//            hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
//                    .title("My Location"));

//            drawPath();
            drawPolygon();
        }else {

            isReceiverRegister = true ;
            gps = new GpsTracer(MapPolyLineActivity.this);

            // check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
//                Log.e("latlang", " lat: " + latitude + " long: " + longitude);
                if(list.size()>0) {
                    HAMBURG = list.get(0);
                }else {
                    HAMBURG = new LatLng(latitude, longitude);
                }

            } else {
                gps.showSettingsAlert();
            }

            // Move the camera instantly to hamburg with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

            // Zoom in, animating the camera.
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(17), 1000, null);

            hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("My Location"));
            startPoint = map.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("Start")
                    .snippet("GPS Start Location")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                    .alpha(0.7f));
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)));

//            3 sec previous
//            RegisterAlarmBroadcast();
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1, 5000,
//                    pendingIntent);
            // This schedule a task to run every 10 minutes:

            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    // Parsing RSS feed:
//                myFeedParser.doSomething();

                    // If you need update UI, simply do this:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // update your UI component here.

                            try{
                                UpdateData();
                            }catch (Throwable throwable){
                                throwable.printStackTrace();
                            }

                        }
                    });
                }
            }, 0, 10 , TimeUnit.SECONDS);

        }
    }

    private void UpdateData() {

        gps = new GpsTracer(MapPolyLineActivity.this);

        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.e("latlang", " lat: " + latitude + " long: "
                    + longitude);
            LatLng d = new LatLng(latitude, longitude);

            if (!firstPass) {
                hamburg.remove();
            }
            firstPass = false;
            hamburg = map.addMarker(new MarkerOptions().position(d)
                    .title("My Location")
                    .snippet("GPS Current Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current)));
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(d, 17));


            // Zoom in, animating the camera.
//                    map.animateCamera(CameraUpdateFactory.zoomTo(17), 10, null);

            if(!isPausedInPreview) {
                list.add(d);
            }
            drawPath();

        } else {
            gps.showSettingsAlert();
        }

//            }

//        };

//        registerReceiver(mReceiver, new IntentFilter("sample"));
//        pendingIntent = PendingIntent.getBroadcast(this, 0,
//                new Intent("sample"), 0);
//        alarmManager = (AlarmManager) (this
//                .getSystemService(Context.ALARM_SERVICE));
    }

    public void drawPath() {
        Polyline line = map.addPolyline(new PolylineOptions()
                .addAll(list).width(6).color(Color.BLUE)
                .geodesic(true));
    }

    public void drawPolygon(){
        Polygon polygon = map.addPolygon(new PolygonOptions()
                .addAll(list).strokeWidth(6).strokeColor(Color.RED)
                .fillColor(Color.BLUE));

    }

    @SuppressWarnings("unused")
//    private void UnregisterAlarmBroadcast() {
//        alarmManager.cancel(pendingIntent);
//        getBaseContext().unregisterReceiver(mReceiver);
//    }

//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//        if(isReceiverRegister) {
//            unregisterReceiver(mReceiver);
//        }
//
//    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
//         unregisterReceiver(mReceiver);
        finish();
    }

}