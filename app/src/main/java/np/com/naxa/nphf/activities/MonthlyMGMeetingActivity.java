package np.com.naxa.nphf.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.gps.GPS_TRACKER_FOR_POINT;

public class MonthlyMGMeetingActivity extends AppCompatActivity {

    private static final String TAG = "MonthlyMGMeetingActivity";
    Toolbar toolbar;
    int CAMERA_PIC_REQUEST = 2;
    Button send, save, startGps, previewMap;
    ProgressDialog mProgressDlg;
    Context context = this;
    GPS_TRACKER_FOR_POINT gps;
    String jsonToSend, photoTosend;
    String imagePath, encodedImage = "", imageName = "no_photo";
    ImageButton photo;
    boolean isGpsTracking = false;
    boolean isGpsTaken = false;
    double finalLat;
    double finalLong;
    ImageView previewImageSite;
    Bitmap thumbnail;
    ArrayList<LatLng> listCf = new ArrayList<LatLng>();
    List<Location> gpslocation = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x8;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    String latLangArray = "", jsonLatLangArray = "";

    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;
    String dataSentStatus, dateString;

    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 999;

    private TimePicker timePicker1;
    private int hour;
    private int minute;
    static final int TIME_DIALOG_ID = 9999;

    Spinner spinner_month, spinner_mother_group;
    ArrayAdapter month_adpt, mother_group_adpt;

    EditText tvDate, tvTime, tvPregnentWomenOld, tvPregnentWomenNew, tvLactatingWomenOld, tvLactatingWomenNew, tvMotherU2Old,
            tvMotherU2New, tvMotherU5Old, tvMotherU5New ;
    AutoCompleteTextView tvVDCName, tvDiscussedTopic, tvTotalParticipants, tvNameOfSM;

    String visit_date, visit_time, visit_month, mother_group, pregnent_women_old, pregnent_women_new, lactating_women_old, lactating_women_new, mother_u2_old,
            mother_u2_new, mother_u5_old, mother_u5_new ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_mgmeeting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Monthly Mother Group Meeting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
