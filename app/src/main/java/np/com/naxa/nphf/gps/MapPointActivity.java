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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.model.StaticListOfCoordinates;


/**
 * Created by Samir on 3/4/2017.
 */
public class MapPointActivity extends FragmentActivity {
    static LatLng HAMBURG;
    private GoogleMap map;
    static ArrayList<LatLng> list = new ArrayList<LatLng>();

    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    BroadcastReceiver mReceiver;
    Marker hamburg, startPoint;

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
//                        map = googleMap;
//                    }
//                });
        try{
            HAMBURG = list.get(0);
        }catch (IndexOutOfBoundsException e){
            e.fillInStackTrace();
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        startPoint = map.addMarker(new MarkerOptions().position(HAMBURG)

//        hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                .title("My Location")
                .snippet("My GPS Location")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                    .alpha(0.7f));
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current)));

        drawPoint();

    }

    public void drawPoint() {
        Polyline line = map.addPolyline(new PolylineOptions()
                .addAll(list).width(6).color(Color.BLUE)
                .geodesic(true));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
}