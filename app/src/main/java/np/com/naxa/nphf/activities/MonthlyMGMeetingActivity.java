package np.com.naxa.nphf.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.database.DataBaseNepalPublicHealth_NotSent;
import np.com.naxa.nphf.database.DataBaseNepalPublicHealth_Sent;
import np.com.naxa.nphf.dialog.Default_DIalog;
import np.com.naxa.nphf.gps.GPS_TRACKER_FOR_POINT;
import np.com.naxa.nphf.gps.MapPointActivity;
import np.com.naxa.nphf.model.CheckValues;
import np.com.naxa.nphf.model.Constants;
import np.com.naxa.nphf.model.StaticListOfCoordinates;
import np.com.naxa.nphf.model.UrlClass;

public class MonthlyMGMeetingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MonthlyMGMeeting";
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
    String formid ;
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
    JSONArray jsonArrayGPS = new JSONArray();


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
    ArrayAdapter visit_month_adpt, mother_group_adpt;

    EditText tvVisitDate, tvVisitTime, tvPregnentWomenOld, tvPregnentWomenNew, tvLactatingWomenOld, tvLactatingWomenNew, tvMotherU2Old,
            tvMotherU2New, tvMotherU5Old, tvMotherU5New ;
    AutoCompleteTextView tvVDCName, tvDiscussedTopic, tvTotalParticipants, tvNameOfSM;

    String visit_date, visit_time, visit_month, mother_group, pregnent_women_old, pregnent_women_new, lactating_women_old, lactating_women_new, mother_u2_old,
            mother_u2_new, mother_u5_old, mother_u5_new, vdc_name, discussed_topic, total_prticipants, sm_name, img ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_mgmeeting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Monthly Mother Group Meeting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner_month = (Spinner) findViewById(R.id.spinner_monthly_meeting_visit_month);
        spinner_mother_group = (Spinner) findViewById(R.id.spinner_monthly_meeting_mother_group);

        tvVisitDate = (EditText) findViewById(R.id.monthly_meeting_visit_date);
        tvVisitTime = (EditText) findViewById(R.id.monthly_meeting_visit_time);
        tvPregnentWomenOld = (EditText) findViewById(R.id.monthly_meeting_pregnent_old_number);
        tvPregnentWomenNew = (EditText) findViewById(R.id.monthly_meeting_pregnent_new_number);
        tvLactatingWomenOld = (EditText) findViewById(R.id.monthly_meeting_lactating_old_number);
        tvLactatingWomenNew = (EditText) findViewById(R.id.monthly_meeting_lactating_new_number);
        tvMotherU2Old = (EditText) findViewById(R.id.monthly_meeting_mother_with_u2child_old_number);
        tvMotherU2New = (EditText) findViewById(R.id.monthly_meeting_mother_with_u2child_new_number);
        tvMotherU5Old= (EditText) findViewById(R.id.monthly_meeting_mother_with_u5child_old_number);
        tvMotherU5New = (EditText) findViewById(R.id.monthly_meeting_mother_with_u5child_new_number);

        setCurrentDateOnView();
        addListenerOnButton();
        setCurrentTimeOnView();
        addListenerOnTimeButton();


        tvVDCName = (AutoCompleteTextView) findViewById(R.id.monthly_meeting_vdc_name);
        tvDiscussedTopic = (AutoCompleteTextView) findViewById(R.id.monthly_meeting_discussed_topic);
        tvTotalParticipants = (AutoCompleteTextView) findViewById(R.id.monthly_meeting_total_paticipants);
        tvTotalParticipants.setVisibility(View.GONE);
        tvNameOfSM = (AutoCompleteTextView) findViewById(R.id.monthly_meeting_sm_name);

        startGps = (Button) findViewById(R.id.monthly_meeting_GpsStart);
        previewMap = (Button) findViewById(R.id.monthly_meeting_preview_map);
        previewMap.setEnabled(false);
        send = (Button) findViewById(R.id.monthly_meeting_send);
        save = (Button) findViewById(R.id.monthly_meeting_save);

        photo = (ImageButton) findViewById(R.id.monthly_meeting_photo_site);
        previewImageSite = (ImageView) findViewById(R.id.monthly_meeting_PhotographSiteimageViewPreview);
        previewImageSite.setVisibility(View.GONE);

        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        // visit month
        visit_month_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.MONTH);
        visit_month_adpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(visit_month_adpt);
        spinner_month.setOnItemSelectedListener(this);

        // mother group
        mother_group_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.MOTHER_GROUP);
        mother_group_adpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_mother_group.setAdapter(mother_group_adpt);
        spinner_mother_group.setOnItemSelectedListener(this);

        initilizeUI();


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });
        startGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GPS_SETTINGS.equals(true) || GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {

                    if (gps.canGetLocation()) {
                        gpslocation.add(gps.getLocation());
                        finalLat = gps.getLatitude();
                        finalLong = gps.getLongitude();
                        if (finalLat != 0) {
                            previewMap.setEnabled(true);
                            try {
                                JSONObject data = new JSONObject();
                                data.put("latitude", finalLat);
                                data.put("longitude", finalLong);

                                jsonArrayGPS.put(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            LatLng d = new LatLng(finalLat, finalLong);

                            listCf.add(d);
                            isGpsTaken = true;
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Your Location is - \nLat: " + finalLat
                                            + "\nLong: " + finalLong, Toast.LENGTH_SHORT)
                                    .show();
                            stringBuilder.append("[" + finalLat + "," + finalLong + "]" + ",");
                        }

                    }
                } else {
                    askForGPS();
                    gps = new GPS_TRACKER_FOR_POINT(MonthlyMGMeetingActivity.this);
                    Default_DIalog.showDefaultDialog(context, R.string.app_name, "Please try again, Gps not initialized");
//                        gps.showSettingsAlert();
                }
            }
        });

        previewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckValues.isFromSavedFrom) {
                    StaticListOfCoordinates.setList(listCf);
                    startActivity(new Intent(MonthlyMGMeetingActivity.this, MapPointActivity.class));
                } else {

                    if (GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {
                        StaticListOfCoordinates.setList(listCf);
                        startActivity(new Intent(MonthlyMGMeetingActivity.this, MapPointActivity.class));
                    } else {
                        Default_DIalog.showDefaultDialog(context, R.string.app_name, "Please try again, Gps not initialized");

                    }
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGpsTracking) {
                    Toast.makeText(getApplicationContext(), "Please end GPS Tracking.", Toast.LENGTH_SHORT).show();
                } else {

                    if (isGpsTaken) {
                        vdc_name = tvVDCName.getText().toString();
                        sm_name = tvNameOfSM.getText().toString();
                        discussed_topic = tvDiscussedTopic.getText().toString();
//                            pregnent women
                        if(tvPregnentWomenOld.getText().toString().equals("")){
                            pregnent_women_old = "0";
                        }
                        else {
                            pregnent_women_old = tvPregnentWomenOld.getText().toString();
                        }

                        if(tvPregnentWomenNew.getText().toString().equals("")){
                            pregnent_women_new = "0";
                        }
                        else {
                            pregnent_women_new = tvPregnentWomenNew.getText().toString();
                        }

//                            lactating women
                        if(tvLactatingWomenOld.getText().toString().equals("")){
                            lactating_women_old = "0";
                        }
                        else {
                            lactating_women_old = tvLactatingWomenOld.getText().toString();
                        }

                        if(tvLactatingWomenNew.getText().toString().equals("")){
                            lactating_women_new = "0";
                        }
                        else {
                            lactating_women_new = tvLactatingWomenNew.getText().toString();
                        }

//                            mother with child under two
                        if(tvMotherU2Old.getText().toString().equals("")){
                            mother_u2_old = "0";
                        }
                        else {
                            mother_u2_old = tvMotherU2Old.getText().toString();
                        }

                        if(tvMotherU2New.getText().toString().equals("")){
                            mother_u2_new = "0";
                        }
                        else {
                            mother_u2_new = tvMotherU2New.getText().toString();
                        }

//                        mother with child under five
                        if(tvMotherU5Old.getText().toString().equals("")){
                            mother_u5_old = "0";
                        }
                        else {
                            mother_u5_old = tvMotherU5Old.getText().toString();
                        }

                        if(tvMotherU5New.getText().toString().equals("")){
                            mother_u5_new = "0";
                        }
                        else {
                            mother_u5_new = tvMotherU5New.getText().toString();
                        }






                        int participants = Integer.parseInt(pregnent_women_old) + Integer.parseInt(pregnent_women_new)+ Integer.parseInt(lactating_women_old)
                                + Integer.parseInt(lactating_women_new) + Integer.parseInt(mother_u2_old) + Integer.parseInt(mother_u2_new)+
                                    Integer.parseInt(mother_u5_old) + Integer.parseInt(mother_u5_new);

                        total_prticipants = ""+participants;
                        tvTotalParticipants.setVisibility(View.VISIBLE);
                        tvTotalParticipants.setText(total_prticipants);

                        img = encodedImage;
                        visit_date = tvVisitDate.getText().toString();
                        visit_time = tvVisitTime.getText().toString();
                        jsonLatLangArray = jsonArrayGPS.toString();


                        convertDataToJson();

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;

                        final Dialog showDialog = new Dialog(context);
                        showDialog.setContentView(R.layout.date_input_layout);
                        final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                        final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);
                        FormNameToInput.setText("Monthly Mother Group Meeting");

                        long date = System.currentTimeMillis();

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                        String dateString = sdf.format(date);
                        dateToInput.setText(dateString);

                        AppCompatButton logIn = (AppCompatButton) showDialog.findViewById(R.id.login_button);
                        showDialog.setTitle("Save Data");
                        showDialog.setCancelable(true);
                        showDialog.show();
                        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                        logIn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String dateDataCollected = dateToInput.getText().toString();
                                String formName = FormNameToInput.getText().toString();
                                if (dateDataCollected == null || dateDataCollected.equals("") || formName == null || formName.equals("")) {
                                    Toast.makeText(context, "Please fill the required field. ", Toast.LENGTH_SHORT).show();
                                } else {
                                    String[] data = new String[]{"7", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + imageName, "Not Sent", "0"};

                                    DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealthNotSent = new DataBaseNepalPublicHealth_NotSent(context);
                                    dataBaseNepalPublicHealthNotSent.open();
                                    long id = dataBaseNepalPublicHealthNotSent.insertIntoTable_Main(data);

//                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                                            .setTitleText("Job done!")
//                                            .setContentText("Data saved successfully!")
//                                            .show();
//                                    dataBaseNepalPublicHealthNotSent.close();
                                    Toast.makeText(MonthlyMGMeetingActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                    showDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGpsTracking) {
                    Toast.makeText(getApplicationContext(), "Please end GPS Tracking.", Toast.LENGTH_SHORT).show();
                } else {

                    if (isGpsTaken) {
                        vdc_name = tvVDCName.getText().toString();
                        sm_name = tvNameOfSM.getText().toString();
                        discussed_topic = tvDiscussedTopic.getText().toString();
//                            pregnent women
                        if(tvPregnentWomenOld.getText().toString().equals("")){
                            pregnent_women_old = "0";
                        }
                        else {
                            pregnent_women_old = tvPregnentWomenOld.getText().toString();
                        }

                        if(tvPregnentWomenNew.getText().toString().equals("")){
                            pregnent_women_new = "0";
                        }
                        else {
                            pregnent_women_new = tvPregnentWomenNew.getText().toString();
                        }

//                            lactating women
                        if(tvLactatingWomenOld.getText().toString().equals("")){
                            lactating_women_old = "0";
                        }
                        else {
                            lactating_women_old = tvLactatingWomenOld.getText().toString();
                        }

                        if(tvLactatingWomenNew.getText().toString().equals("")){
                            lactating_women_new = "0";
                        }
                        else {
                            lactating_women_new = tvLactatingWomenNew.getText().toString();
                        }

//                            mother with child under two
                        if(tvMotherU2Old.getText().toString().equals("")){
                            mother_u2_old = "0";
                        }
                        else {
                            mother_u2_old = tvMotherU2Old.getText().toString();
                        }

                        if(tvMotherU2New.getText().toString().equals("")){
                            mother_u2_new = "0";
                        }
                        else {
                            mother_u2_new = tvMotherU2New.getText().toString();
                        }

//                        mother with child under five
                        if(tvMotherU5Old.getText().toString().equals("")){
                            mother_u5_old = "0";
                        }
                        else {
                            mother_u5_old = tvMotherU5Old.getText().toString();
                        }

                        if(tvMotherU5New.getText().toString().equals("")){
                            mother_u5_new = "0";
                        }
                        else {
                            mother_u5_new = tvMotherU5New.getText().toString();
                        }






                        int participants = Integer.parseInt(pregnent_women_old) + Integer.parseInt(pregnent_women_new)+ Integer.parseInt(lactating_women_old)
                                + Integer.parseInt(lactating_women_new) + Integer.parseInt(mother_u2_old) + Integer.parseInt(mother_u2_new)+
                                Integer.parseInt(mother_u5_old) + Integer.parseInt(mother_u5_new);

                        total_prticipants = ""+participants;
                        tvTotalParticipants.setVisibility(View.VISIBLE);
                        tvTotalParticipants.setText(total_prticipants);

                        img = encodedImage;
                        visit_date = tvVisitDate.getText().toString();
                        visit_time = tvVisitTime.getText().toString();
                        jsonLatLangArray = jsonArrayGPS.toString();


                        convertDataToJson();
                        sendDatToserver();

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;

                        final Dialog showDialog = new Dialog(context);
                        showDialog.setContentView(R.layout.date_input_layout);
                        final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                        final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);
                        FormNameToInput.setText("Monthly Mother Group Meeting");

                        long date = System.currentTimeMillis();

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                        String dateString = sdf.format(date);
                        dateToInput.setText(dateString);

                        AppCompatButton logIn = (AppCompatButton) showDialog.findViewById(R.id.login_button);
                        showDialog.setTitle("Save Data");
                        showDialog.setCancelable(true);
                        showDialog.show();
                        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                        logIn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String dateDataCollected = dateToInput.getText().toString();
                                String formName = FormNameToInput.getText().toString();
                                if (dateDataCollected == null || dateDataCollected.equals("") || formName == null || formName.equals("")) {
                                    Toast.makeText(context, "Please fill the required field. ", Toast.LENGTH_SHORT).show();
                                } else {
                                    String[] data = new String[]{"7", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + imageName, "Sent", "0"};

                                    DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealthNotSent = new DataBaseNepalPublicHealth_NotSent(context);
                                    dataBaseNepalPublicHealthNotSent.open();
                                    long id = dataBaseNepalPublicHealthNotSent.insertIntoTable_Main(data);

                                    showDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });



    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MonthlyMGMeetingActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MonthlyMGMeetingActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MonthlyMGMeetingActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MonthlyMGMeetingActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void askForGPS() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MonthlyMGMeetingActivity.this, GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (ActivityCompat.checkSelfPermission(MonthlyMGMeetingActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForGPS();
                Log.v("Susan", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(MonthlyMGMeetingActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.getMessage();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    //Date picker
    // display current date
    public void setCurrentDateOnView() {

        //dpResult = (DatePicker) findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvVisitDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("/").append(month + 1).append("/")
                .append(day).append(""));

        // set current date into textview
        tvVisitDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("/").append(month + 1).append("/")
                .append(day).append(""));
    }


    public void addListenerOnButton() {


        tvVisitDate.setOnClickListener(new View.OnClickListener() {

            //            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvVisitDate.setShowSoftInputOnFocus(false);
                }
                showDialog(DATE_DIALOG_ID);
            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);

            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvVisitDate.setText(new StringBuilder().append(year)
                    .append("-").append(month + 1).append("-").append(day)
                    .append(""));
        }
    };

    private DatePickerDialog.OnDateSetListener deliveryDatePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvVisitDate.setText(new StringBuilder().append(year)
                    .append("-").append(month + 1).append("-").append(day)
                    .append(""));
        }
    };

    // Time picker code

    // display current time
    public void setCurrentTimeOnView() {

//        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // set current time into textview
        tvVisitTime.setText(new StringBuilder().append(pad(hour)).append(":")
                .append(pad(minute)));

        // set current time into timepicker
//        timePicker1.setCurrentHour(hour);
//        timePicker1.setCurrentMinute(minute);

    }

    public void addListenerOnTimeButton() {


        tvVisitTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(TIME_DIALOG_ID);

            }

        });

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour,
                              int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;

            // set current time into textview
            tvVisitTime.setText(new StringBuilder().append(pad(hour))
                    .append(":").append(pad(minute)));


        }
    };


    @Override
    public void onBackPressed() {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(context);
        showDialog.setContentView(R.layout.close_dialog_english);
        final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
        final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

        showDialog.setTitle("WARNING !!!");
        showDialog.setCancelable(false);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                imagePath = filePath;
                addImage();

            }
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                //  ImageView image =(ImageView) findViewById(R.id.Photo);
                // image.setImageBitmap(thumbnail);
                previewImageSite.setVisibility(View.VISIBLE);
                previewImageSite.setImageBitmap(thumbnail);
                saveToExternalSorage(thumbnail);
                addImage();
//                Toast.makeText(getApplicationContext(), "" + encodedImage, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveToExternalSorage(Bitmap thumbnail) {
        // TODO Auto-generated method stub
        //String merocinema="Mero Cinema";
//        String movname=getIntent().getExtras().getString("Title");
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();

        imageName = "MonthlyMGMeeting" + timeInMillis;

        File file1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), imageName);
//        if (!file1.mkdirs()) {
//            Toast.makeText(getApplicationContext(), "Not Created", Toast.LENGTH_SHORT).show();
//        }

        if (file1.exists()) file1.delete();
        try {
            FileOutputStream out = new FileOutputStream(file1);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(getApplicationContext(), "Saved " + imageName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void addImage() {
        File file1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), imageName);
        String path = file1.toString();

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 1;
        options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
//        Bitmap bm = BitmapFactory.decodeFile( imagePath ,options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);


        // bitmap object

        byte[] byteImage_photo = baos.toByteArray();

        //generate base64 string of image
        encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
        Log.e("IMAGE STRING", "-" + encodedImage);

    }

    public void initilizeUI() {
        Intent intent = getIntent();
        if (intent.hasExtra("JSON1")) {


            CheckValues.isFromSavedFrom = true;
            startGps.setEnabled(false);
            isGpsTaken = true;
            previewMap.setEnabled(true);
            Bundle bundle = intent.getExtras();
            String jsonToParse = (String) bundle.get("JSON1");
            imageName = (String) bundle.get("photo");
            String gpsLocationtoParse = (String) bundle.get("gps");
            formid = (String) bundle.get("DBid");
            String sent_Status = (String) bundle.get("sent_Status");
            Log.d(TAG, "initilizeUI: "+sent_Status);


//            if (sent_Status.equals("Sent")) {
//                tvchild_motherName.setEnabled(false);
//                tvchildren2VDCName.setEnabled(false);
//                tvchildrenWardNo.setEnabled(false);
//                tvchild2_age.setEnabled(false);
//                tvchild2_sex.setEnabled(false);
//                tvcontact_details_lactating_women.setEnabled(false);
//                tvsmName.setEnabled(false);
//                tvVisitDate.setEnabled(false);
//                tvVisitTime.setEnabled(false);
//                photo.setEnabled(false);
//                startGps.setEnabled(false);
//                cv_Send_Save.setVisibility(View.GONE);
//
//
//            }


            Log.e("MonthlyMGMeeting", "i-" + imageName);

            if (imageName.equals("no_photo")) {
            } else {
                File file1 = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), imageName);
                String path = file1.toString();
                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();

                loadImageFromStorage(path);

                addImage();
            }
            try {
                //new adjustment
                Log.e(TAG, "" + jsonToParse);
//                parseArrayGPS(gpsLocationtoParse);
                parseJson(jsonToParse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            gps = new GPS_TRACKER_FOR_POINT(MonthlyMGMeetingActivity.this);
            gps.canGetLocation();
            startGps.setEnabled(true);

        }
    }

    private void loadImageFromStorage(String path) {
        try {
            previewImageSite.setVisibility(View.VISIBLE);
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            previewImageSite.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "invalid path", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int SpinnerID = parent.getId();

        if (SpinnerID == R.id.spinner_monthly_meeting_visit_month) {
            long spinnerPosition = 0;
            spinnerPosition = visit_month_adpt.getItemId(position);
            int spinner_item_selected_position = (int) (long) spinnerPosition;

            visit_month = Constants.MONTH[spinner_item_selected_position];
            Log.e("PeerGroupActivity", "onItemSelected: " + visit_month);
        }

        if (SpinnerID == R.id.spinner_monthly_meeting_mother_group) {
            long spinnerPosition = 0;
            spinnerPosition = mother_group_adpt.getItemId(position);
            int spinner_item_selected_position = (int) (long) spinnerPosition;

            mother_group = Constants.MOTHER_GROUP[spinner_item_selected_position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // data convert
    public void convertDataToJson() {
        //function in the activity that corresponds to the hwc_human_casulty button

        try {

            JSONObject header = new JSONObject();
            header.put("tablename", "recording_tool_for_monthly_mother_group_meeting");
            header.put("visit_month", visit_month);
            header.put("date", visit_date);
            header.put("time", visit_time);
            header.put("name_of_VDC", vdc_name);
            header.put("mother_group", mother_group);
            header.put("discussed_topic", discussed_topic);
            header.put("pregnent_women_old", pregnent_women_old);
            header.put("pregnent_women_new", pregnent_women_new);
            header.put("lactating_women_old", lactating_women_old);
            header.put("lactating_women_new", lactating_women_new);
            header.put("mother_u2_old", mother_u2_old);
            header.put("mother_u2_new", mother_u2_new);
            header.put("mother_u5_old", mother_u5_old);
            header.put("mother_u5_new", mother_u5_new);

            header.put("total_prticipants", total_prticipants);
            header.put("name_of_SM", sm_name);

            header.put("lat", finalLat);
            header.put("lon", finalLong);
            header.put("image", encodedImage);


            jsonToSend = header.toString();
            Log.e(TAG, "convertDataToJson: " + jsonToSend);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void sendDatToserver() {

        if (jsonToSend.length() > 0) {

            RestApii restApii = new RestApii();
            restApii.execute();
        }
    }

    public void parseJson(String jsonToParse) throws JSONException {

        JSONObject jsonObj = new JSONObject(jsonToParse);
        Log.e(TAG, "json parse : " + jsonObj.toString());

        finalLat = Double.parseDouble(jsonObj.getString("lat"));
        finalLong = Double.parseDouble(jsonObj.getString("lon"));
        LatLng d = new LatLng(finalLat, finalLong);
        listCf.add(d);

        vdc_name = jsonObj.getString("name_of_VDC");
        visit_month = jsonObj.getString("visit_month");
        visit_date = jsonObj.getString("date");
        visit_time = jsonObj.getString("time");
        mother_group = jsonObj.getString("mother_group");
        pregnent_women_old = jsonObj.getString("pregnent_women_old");
        pregnent_women_new = jsonObj.getString("pregnent_women_new");
        lactating_women_old = jsonObj.getString("lactating_women_old");
        lactating_women_new = jsonObj.getString("lactating_women_new");
        mother_u2_new = jsonObj.getString("mother_u2_new");
        mother_u2_old = jsonObj.getString("mother_u2_old");
        mother_u5_new = jsonObj.getString("mother_u5_new");
        mother_u5_old = jsonObj.getString("mother_u5_old");

        discussed_topic = jsonObj.getString("discussed_topic");

        total_prticipants = jsonObj.getString("total_prticipants");
        sm_name = jsonObj.getString("name_of_SM");


        Log.e(TAG, "" + " SAMIR  " + vdc_name + "----location----" + finalLat + " , " + finalLong);

        tvVisitDate.setText(visit_date);
        tvVisitTime.setText(visit_time);
        tvVDCName.setText(vdc_name);
        tvDiscussedTopic.setText(discussed_topic);
        tvPregnentWomenOld.setText(pregnent_women_old);
        tvPregnentWomenNew.setText(pregnent_women_new);
        tvLactatingWomenOld.setText(lactating_women_old);
        tvLactatingWomenNew.setText(lactating_women_new);
        tvMotherU2Old.setText(mother_u2_old);
        tvMotherU2New.setText(mother_u2_new);
        tvMotherU5Old.setText(mother_u5_old);
        tvMotherU5New.setText(mother_u5_new);
        tvNameOfSM.setText(sm_name);

        tvTotalParticipants.setVisibility(View.VISIBLE);
        tvTotalParticipants.setText(total_prticipants);


        int setVisitMonth = visit_month_adpt.getPosition(visit_month);
        spinner_month.setSelection(setVisitMonth);

        int setMotherGroup = mother_group_adpt.getPosition(mother_group);
        spinner_mother_group.setSelection(setMotherGroup);


    }



    private class RestApii extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String text = null;
            text = POST(UrlClass.URL_DATA_SEND);
            Log.d(TAG, "RAW resposne" + text);

            return text.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (mProgressDlg != null && mProgressDlg.isShowing()) {
                mProgressDlg.dismiss();
            }

            Log.d(TAG, "on post resposne" + result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                dataSentStatus = jsonObject.getString("status");
                Log.e(TAG, "SAMIR " + dataSentStatus);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (dataSentStatus.equals("200")) {
                Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();
                previewImageSite.setVisibility(View.VISIBLE);
                tvTotalParticipants.setVisibility(View.VISIBLE);

                tvVDCName.setText(vdc_name);
                tvDiscussedTopic.setText(discussed_topic);
                tvPregnentWomenOld.setText(pregnent_women_old);
                tvPregnentWomenNew.setText(pregnent_women_new);
                tvLactatingWomenOld.setText(lactating_women_old);
                tvLactatingWomenNew.setText(lactating_women_new);
                tvMotherU2Old.setText(mother_u2_old);
                tvMotherU2New.setText(mother_u2_new);
                tvMotherU5Old.setText(mother_u5_old);
                tvMotherU5New.setText(mother_u5_new);
                tvNameOfSM.setText(sm_name);
                tvTotalParticipants.setText(total_prticipants);
                previewImageSite.setImageBitmap(thumbnail);

                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                dateString = sdf.format(date);
//                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                        .setTitleText("")
//                        .setContentText("Data sent successfully!")
//                        .show();
                String[] data = new String[]{"7", "Monthly Mother Group Meeting", dateString, jsonToSend, jsonLatLangArray,
                        "" + imageName, "Sent", "0"};

                DataBaseNepalPublicHealth_Sent dataBaseNepalPublicHealthSent = new DataBaseNepalPublicHealth_Sent(context);
                dataBaseNepalPublicHealthSent.open();
                long id = dataBaseNepalPublicHealthSent.insertIntoTable_Main(data);
                Log.e("dbID", "" + id);
                dataBaseNepalPublicHealthSent.close();

                if(CheckValues.isFromSavedFrom) {
                    Log.e(TAG, "onPostExecute: FormID : " + formid);
                    DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealth_NotSent = new DataBaseNepalPublicHealth_NotSent(context);
                    dataBaseNepalPublicHealth_NotSent.open();
                    dataBaseNepalPublicHealth_NotSent.dropRowNotSentForms(formid);
//                    Log.e("dbID", "" + id);
                    dataBaseNepalPublicHealth_NotSent.close();
                }


            }
        }


        public String POST(String urll) {
            String result = "";
            URL url;

            try {
                url = new URL(urll);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data", jsonToSend);

                String query = builder.build().getEncodedQuery();

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                } else {
                    result = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


    }
}
