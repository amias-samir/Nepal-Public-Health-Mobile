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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
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
import com.thomashaertel.widget.MultiSpinner;

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

import np.com.naxa.nphf.MainActivity;
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

public class ChildrenUnderTwo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "chidren_under_two";
    public static Toolbar toolbar;
    int CAMERA_PIC_REQUEST = 2;
    Spinner spinnerVDCName, spinnerWardNo, spinner_growth_monitor;
//    spinner_vaccination_verification;
    ArrayAdapter vdcNameadpt, wardNoadpt, growth_monitor_adpt;
//    vaccination_verification_adpt;

    private MultiSpinner spinner;
    private ArrayAdapter<String> adapter;

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
    String formid, formNameSavedForm = "" ;
    ImageView previewImageSite;
    Bitmap thumbnail;
    ArrayList<LatLng> listCf = new ArrayList<LatLng>();
    List<Location> gpslocation = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    String latLangArray = "", jsonLatLangArray = "";


    AutoCompleteTextView tvchild_motherName,  tvchild2_age, tvchild2_sex, tvcontact_details_lactating_women, tvsmName,
            tvWeightOfChild;
//    tvchildren2VDCName, tvchildrenWardNo,
    EditText tvVisitDate, tvVisitTime, tvDateOfBirth, tvMultispinnerVaccination;
    CardView cv_Send_Save;


    String child2_mother_name, child2_vdc_name, child2_ward_no, child2_age, child2_sex, growth_monitor, child2_sm_name,
            vaccination, weight, contact_no_lactating_women, visit_date, visit_time, date_of_birth, img;

    JSONArray jsonArrayGPS = new JSONArray();

    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;
    String dataSentStatus, dateString;

    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 999;
    static final int BIRTH_DATE_DIALOG_ID = 99;


    private TimePicker timePicker1;
    private int hour;
    private int minute;
    static final int TIME_DIALOG_ID = 9999;

    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x8;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_under_two);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Children Under Two Years");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tvchild_motherName = (AutoCompleteTextView) findViewById(R.id.mother_name_2);
//        tvchildren2VDCName = (AutoCompleteTextView) findViewById(R.id.child_under2_vdc_name);
        tvWeightOfChild = (AutoCompleteTextView) findViewById(R.id.children_2_weight_of_child);
        tvchild2_age = (AutoCompleteTextView) findViewById(R.id.children_2_age);
        tvchild2_sex = (AutoCompleteTextView) findViewById(R.id.children_2_sex);
        tvcontact_details_lactating_women = (AutoCompleteTextView) findViewById(R.id.contact_details);
        tvsmName = (AutoCompleteTextView) findViewById(R.id.children_2_sm_name);
        tvVisitDate = (EditText) findViewById(R.id.children_2_visit_date);
        tvVisitTime = (EditText) findViewById(R.id.children_2_visit_time);
        tvDateOfBirth = (EditText) findViewById(R.id.children_2_date_of_birth);
        cv_Send_Save = (CardView) findViewById(R.id.cv_SaveSend);


        setCurrentDateOnView();
        addListenerOnButton();
        setCurrentTimeOnView();
        addListenerOnTimeButton();

        photo = (ImageButton) findViewById(R.id.children_2_photo_site);
        previewImageSite = (ImageView) findViewById(R.id.children_2_PhotographSiteimageViewPreview);
        previewImageSite.setVisibility(View.GONE);

        spinnerVDCName = (Spinner) findViewById(R.id.children_2_vdc_name);
        spinnerWardNo = (Spinner) findViewById(R.id.children_2_ward_no);
        spinner_growth_monitor = (Spinner) findViewById(R.id.spinner_growth_monitor_2);
//        spinner_vaccination_verification = (Spinner) findViewById(R.id.spinner_vaccination_details);
//        spinner_visit_weight = (Spinner) findViewById(R.id.visit_for_weight2);

        send = (Button) findViewById(R.id.children_2_send);
        save = (Button) findViewById(R.id.children_2_save);
        startGps = (Button) findViewById(R.id.children_2_GpsStart);
        previewMap = (Button) findViewById(R.id.children_2_preview_map);
        previewMap.setEnabled(false);

//        multispinne spinner Vaccination
        // create spinner list elements
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("BCG");
        adapter.add("DPT,HepB-Hib,Polio,PCV 1st Dose");
        adapter.add("DPT,HepB-Hib,Polio,PCV 2nd Dose");
        adapter.add("DPT,HepB-Hib,Polio,IPV 3rd Dose");
        adapter.add("PCV (3rd) and MR (1st)");
        adapter.add("JE");
        adapter.add("MR (2nd)");

        // get spinner and set adapter
        spinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
        tvMultispinnerVaccination = (EditText) findViewById(R.id.selected_vaccination_details);
        spinner.setAdapter(adapter, false, onSelectedListener);

        // set initial selection
        boolean[] selectedItems = new boolean[adapter.getCount()];
//        selectedItems[1] = true; // select second item
        spinner.setSelected(selectedItems);


        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);


        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        //VDC name spinner
        vdcNameadpt = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.VDC_NAME);
        vdcNameadpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVDCName.setAdapter(vdcNameadpt);
        spinnerVDCName.setOnItemSelectedListener(this);

        //ward NO spinner
        wardNoadpt = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.VDC_WARD_NO);
        wardNoadpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWardNo.setAdapter(wardNoadpt);
        spinnerWardNo.setOnItemSelectedListener(this);

        // child under two growth monitoring spinner
        growth_monitor_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.YES_NO);
        growth_monitor_adpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_growth_monitor.setAdapter(growth_monitor_adpt);
        spinner_growth_monitor.setOnItemSelectedListener(this);

        // spinner vaccination details
//        vaccination_verification_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.VACCINATION);
//        vaccination_verification_adpt
//                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner_vaccination_verification.setAdapter(vaccination_verification_adpt);
//        spinner_vaccination_verification.setOnItemSelectedListener(this);

        // spinner weight of children two in different visit

//        visit_weight_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.CHILDREN_TWO_WEIGHT);
//        visit_weight_adpt
//                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_visit_weight.setAdapter(visit_weight_adpt);
//        spinner_visit_weight.setOnItemSelectedListener(this);

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

                            LatLng location = new LatLng(finalLat, finalLong);

                            listCf.add(location);
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
                    gps = new GPS_TRACKER_FOR_POINT(ChildrenUnderTwo.this);
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
                    startActivity(new Intent(ChildrenUnderTwo.this, MapPointActivity.class));
                } else {

                    if (GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {
                        StaticListOfCoordinates.setList(listCf);
                        startActivity(new Intent(ChildrenUnderTwo.this, MapPointActivity.class));
                    } else {
                        Default_DIalog.showDefaultDialog(context, R.string.app_name, "Please try again, Gps not initialized");

                    }
                }
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isGpsTracking) {
//                    Toast.makeText(getApplicationContext(), "Please end GPS Tracking.", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    if (isGpsTaken) {
                        child2_sm_name = tvsmName.getText().toString();
                        child2_mother_name = tvchild_motherName.getText().toString();
                        weight = tvWeightOfChild.getText().toString();
                        vaccination = tvMultispinnerVaccination.getText().toString();
                        child2_age = tvchild2_age.getText().toString();
                        child2_sex = tvchild2_sex.getText().toString();
                        img = encodedImage;
                        contact_no_lactating_women = tvcontact_details_lactating_women.getText().toString();
                        visit_date = tvVisitDate.getText().toString();
                        date_of_birth = tvDateOfBirth.getText().toString();
                        visit_time = tvVisitTime.getText().toString();
                        jsonLatLangArray = jsonArrayGPS.toString();


                        convertDataToJson();

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        final int width = metrics.widthPixels;
                        int height = metrics.heightPixels;

                        final Dialog showDialog = new Dialog(context);
                        showDialog.setContentView(R.layout.date_input_layout);
                        final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                        final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);


                        if (formNameSavedForm.equals("")){
                            FormNameToInput.setText("Children Under Two");
                        }
                        else {
                            FormNameToInput.setText(formNameSavedForm);
                            DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealthNotSent = new DataBaseNepalPublicHealth_NotSent(context);
                            dataBaseNepalPublicHealthNotSent.open();
                            dataBaseNepalPublicHealthNotSent.dropRowNotSentForms(formid);
                        }

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
                                    String[] data = new String[]{"3", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + imageName, "Not Sent", "0"};

                                    DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealthNotSent = new DataBaseNepalPublicHealth_NotSent(context);
                                    dataBaseNepalPublicHealthNotSent.open();
                                    dataBaseNepalPublicHealthNotSent.insertIntoTable_Main(data);

                                    Toast.makeText(ChildrenUnderTwo.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                    showDialog.dismiss();

                                    final Dialog showDialog = new Dialog(context);
                                    showDialog.setContentView(R.layout.savedform_sent_popup);
                                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
                                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

                                    showDialog.setTitle("Successfully Saved");
                                    showDialog.setCancelable(false);
                                    showDialog.show();
                                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog.dismiss();
                                            Intent intent = new Intent(ChildrenUnderTwo.this, SavedFormsActivity.class);
                                            startActivity(intent);
//                                finish();
                                        }
                                    });

                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog.dismiss();
                                            Intent intent = new Intent(ChildrenUnderTwo.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
//                    } else {
//                        Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
            }
        });

        // add click listener to Button "POST"
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGpsTracking) {
                    Toast.makeText(getApplicationContext(), "Please end GPS Tracking.", Toast.LENGTH_SHORT).show();
                } else {

                    if (isGpsTaken) {

                child2_sm_name = tvsmName.getText().toString();
                child2_mother_name = tvchild_motherName.getText().toString();
                weight = tvWeightOfChild.getText().toString();
                vaccination = tvMultispinnerVaccination.getText().toString();
                child2_age = tvchild2_age.getText().toString();
                child2_sex = tvchild2_sex.getText().toString();
                img = encodedImage;
                contact_no_lactating_women = tvcontact_details_lactating_women.getText().toString();
                visit_date = tvVisitDate.getText().toString();
                date_of_birth = tvDateOfBirth.getText().toString();
                visit_time = tvVisitTime.getText().toString();


                if (networkInfo != null && networkInfo.isConnected()) {


                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

                    final Dialog showDialog = new Dialog(context);
                    showDialog.setContentView(R.layout.alert_dialog_before_send);
                    final Button yes = (Button) showDialog.findViewById(R.id.alertButtonYes);
                    final Button no = (Button) showDialog.findViewById(R.id.alertButtonNo);

                    showDialog.setTitle("WARNING !!!");
                    showDialog.setCancelable(false);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            mProgressDlg = new ProgressDialog(context);
                            mProgressDlg.setMessage("Please wait...");
                            mProgressDlg.setIndeterminate(false);
                            mProgressDlg.setCancelable(false);
                            mProgressDlg.show();
                            convertDataToJson();
                            sendDatToserver();
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                        }
                    });


                } else {
                    final View coordinatorLayoutView = findViewById(R.id.activity_pregnent_women);
                    Snackbar.make(coordinatorLayoutView, "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Retry", null).show();
                }

                    } else {
                        Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();

                    }
                }

            }


        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int SpinnerID = parent.getId();

        if(SpinnerID == R.id.children_2_vdc_name){
            child2_vdc_name = Constants.VDC_NAME[position];
            Log.e(TAG, "onItemSelected: "+child2_vdc_name );

        }

        if(SpinnerID == R.id.children_2_ward_no){
            child2_ward_no = Constants.VDC_WARD_NO[position];
            Log.e(TAG, "onItemSelected: "+child2_ward_no );

        }

        if (SpinnerID == R.id.spinner_growth_monitor_2) {
            switch (position) {
                case 0:
                    growth_monitor = "Yes";
                    break;
                case 1:
                    growth_monitor = "No";
                    break;

            }
        }

//        if (SpinnerID == R.id.spinner_vaccination_details) {
//            switch (position) {
//                case 0:
//                    vaccination = "BCG";
//                    break;
//
//                case 1:
//                    vaccination = "DPT ,HepB -Hib,Polio,PCV 1st Dose";
//                    break;
//                case 2:
//                    vaccination = "DPT ,HepB -Hib,Polio,PCV 2nd Dose";
//                    break;
//                case 3:
//                    vaccination = "DPT,HepB-Hib,Polio,IPV 3rd Dose";
//                    break;
//                case 4:
//                    vaccination = "PCV (3rd) and MR (1st )";
//                    break;
//                case 5:
//                    vaccination = "JE";
//                    break;
//                case 6:
//                    vaccination = "MR (2nd)";
//                    break;
//
//            }
//        }

//        if (SpinnerID == R.id.visit_for_weight2) {
//            switch (position) {
//
//                case 0:
//                    weight = "1st visit_0_11_months";
//                    break;
//                case 1:
//                    weight = "1st visit_12_23_months";
//                    break;
//                case 2:
//                    weight = "2nd visit_0_11_months";
//                    break;
//                case 3:
//                    weight = "2nd visit_12_23_months";
//                    break;
//
//            }
//        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ChildrenUnderTwo.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChildrenUnderTwo.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ChildrenUnderTwo.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ChildrenUnderTwo.this, new String[]{permission}, requestCode);
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
                            status.startResolutionForResult(ChildrenUnderTwo.this, GPS_SETTINGS);
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
            if (ActivityCompat.checkSelfPermission(ChildrenUnderTwo.this, permissions[0]) == PackageManager.PERMISSION_GRANTED || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForGPS();
                Log.v("Susan", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(ChildrenUnderTwo.this, "Permission granted", Toast.LENGTH_SHORT).show();
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
        tvDateOfBirth.setText(new StringBuilder()
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

        tvDateOfBirth.setOnClickListener(new View.OnClickListener() {

            //            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvDateOfBirth.setShowSoftInputOnFocus(false);
                }
                showDialog(BIRTH_DATE_DIALOG_ID);
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

            case BIRTH_DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, birthDatePickerListener, year, month,
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

    private DatePickerDialog.OnDateSetListener birthDatePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvDateOfBirth.setText(new StringBuilder().append(year)
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

            // set current time into timepicker
//            timePicker1.setCurrentHour(hour);
//            timePicker1.setCurrentMinute(minute);

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

//                image_name_tv.setText(filePath);
                imagePath = filePath;
                addImage();
//                Toast.makeText(getApplicationContext(),""+encodedImage,Toast.LENGTH_SHORT).show();
//                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
//                    //FINE
//
//                }
//                else{
//                    //NOT IN REQUIRED FORMAT
//                }
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

        imageName = "Child_Under_Two" + timeInMillis;

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
            formNameSavedForm = (String) bundle.get("form_name");
            Log.d(TAG, "initilizeUI: "+sent_Status);


            if (sent_Status.equals("Sent")) {
                tvchild_motherName.setEnabled(false);
                spinnerVDCName.setEnabled(false);
                spinnerWardNo.setEnabled(false);
                spinner_growth_monitor.setEnabled(false);
                tvMultispinnerVaccination.setEnabled(false);
                tvchild2_age.setEnabled(false);
                tvWeightOfChild.setEnabled(false);
                tvchild2_sex.setEnabled(false);
                tvcontact_details_lactating_women.setEnabled(false);
                tvsmName.setEnabled(false);
                tvVisitDate.setEnabled(false);
                tvDateOfBirth.setEnabled(false);
                tvVisitTime.setEnabled(false);
                photo.setEnabled(false);
                startGps.setEnabled(false);
                cv_Send_Save.setVisibility(View.GONE);


            }


            Log.e("PregnentWomen", "i-" + imageName);

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
                Log.e("Pregnent_Women", "" + jsonToParse);
//                parseArrayGPS(gpsLocationtoParse);
                parseJson(jsonToParse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            gps = new GPS_TRACKER_FOR_POINT(ChildrenUnderTwo.this);
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


    // data convert
    public void convertDataToJson() {
        //function in the activity that corresponds to the hwc_human_casulty button

        try {

            JSONObject header = new JSONObject();

            header.put("tablename", "recording_tool_for_children_under_two");
            header.put("name_of_SM", child2_sm_name);
            header.put("name_of_mother", child2_mother_name);
            header.put("name_of_VDC", child2_vdc_name);
            header.put("ward_no", child2_ward_no);
            header.put("age", child2_age);
            header.put("sex", child2_sex);
            header.put("registered_for_growth_monitoring", growth_monitor);
            header.put("name_of_vaccination", vaccination);
            header.put("weight_of_the_child", weight);
            header.put("contact_detail_of_lactating_women", contact_no_lactating_women);
            header.put("date", visit_date);
            header.put("date_of_birth", date_of_birth);
            header.put("time", visit_time);
            header.put("lat", finalLat);
            header.put("lon", finalLong);
            header.put("image", encodedImage);

            jsonToSend = header.toString();
            Log.e(TAG, "SAMIR: " + jsonToSend);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Nishon", jsonToSend);

//        sendDatToserver();
    }

    public void sendDatToserver() {

        if (jsonToSend.length() > 0) {

            RestApii restApii = new RestApii();
            restApii.execute();
        }
    }

    public void parseJson(String jsonToParse) throws JSONException {
//        JSONObject jsonOb = new JSONObject(jsonToParse);
//        Log.e("PregnentWomenActivity", "json : " + jsonOb.toString());
//        String data = jsonOb.getString("formdata");
//        Log.e("PregnentWomenActivity", "formdata : " + jsonOb.toString());
        JSONObject jsonObj = new JSONObject(jsonToParse);
        Log.e("ChildrenUnderTwo", "json : " + jsonObj.toString());

        child2_sm_name = jsonObj.getString("name_of_SM");
        child2_mother_name = jsonObj.getString("name_of_mother");
        child2_vdc_name = jsonObj.getString("name_of_VDC");
        child2_ward_no = jsonObj.getString("ward_no");
        child2_age = jsonObj.getString("age");
        child2_sex = jsonObj.getString("sex");
        growth_monitor = jsonObj.getString("registered_for_growth_monitoring");
        vaccination = jsonObj.getString("name_of_vaccination");
        weight = jsonObj.getString("weight_of_the_child");
        visit_date = jsonObj.getString("date");
        date_of_birth = jsonObj.getString("date_of_birth");
        visit_time = jsonObj.getString("time");
        contact_no_lactating_women = jsonObj.getString("contact_detail_of_lactating_women");
        finalLat = Double.parseDouble(jsonObj.getString("lat"));
        finalLong = Double.parseDouble(jsonObj.getString("lon"));
        LatLng d = new LatLng(finalLat, finalLong);
        listCf.add(d);
//        encodedImage = jsonObj.getString("image");


        Log.e("Children Under Two", "Parsed data " + child2_vdc_name + child2_ward_no + weight);

        tvsmName.setText(child2_sm_name);
        tvchild_motherName.setText(child2_mother_name);
//        tvchildren2VDCName.setText(child2_vdc_name);
        tvMultispinnerVaccination.setText(vaccination);
        if(!vaccination.equals("")){
            tvMultispinnerVaccination.setVisibility(View.VISIBLE);
        }
        tvchild2_age.setText(child2_age);
        tvchild2_sex.setText(child2_sex);
        tvWeightOfChild.setText(weight);
        tvVisitDate.setText(visit_date);
        tvDateOfBirth.setText(date_of_birth);
        tvVisitTime.setText(visit_time);
        tvcontact_details_lactating_women.setText(contact_no_lactating_women);


        int setVDCName = vdcNameadpt.getPosition(child2_vdc_name);
        spinnerVDCName.setSelection(setVDCName);

        int setWardNo = wardNoadpt.getPosition(child2_ward_no);
        spinnerWardNo.setSelection(setWardNo);

        int setGrothMonitor = growth_monitor_adpt.getPosition(growth_monitor);
        spinner_growth_monitor.setSelection(setGrothMonitor);

//        int setVaccination = vaccination_verification_adpt.getPosition(vaccination);
//        spinner_vaccination_verification.setSelection(setVaccination);

//        int setChildWeight = visit_weight_adpt.getPosition(weight);
//        spinner_visit_weight.setSelection(setChildWeight);


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

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (dataSentStatus.equals("200")) {
                Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();
                previewImageSite.setVisibility(View.GONE);

                tvsmName.setText(child2_sm_name);
                tvchild_motherName.setText(child2_mother_name);
                tvMultispinnerVaccination.setText(vaccination);
//                tvchildrenWardNo.setText(child2_ward_no);
                tvchild2_age.setText(child2_age);
                tvchild2_sex.setText(child2_sex);
                tvWeightOfChild.setText(weight);
                tvVisitDate.setText(visit_date);
                tvVisitTime.setText(visit_time);
                tvDateOfBirth.setText(date_of_birth);
                tvcontact_details_lactating_women.setText(contact_no_lactating_women);
                previewImageSite.setImageBitmap(thumbnail);


                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                dateString = sdf.format(date);
//                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                        .setTitleText("")
//                        .setContentText("Data sent successfully!")
//                        .show();
                String[] data = new String[]{"3", "Children Under Two", dateString, jsonToSend, jsonLatLangArray,
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

                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

//                    Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();

                    final Dialog showDialog = new Dialog(context);
                    showDialog.setContentView(R.layout.thank_you_popup);
                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

                    showDialog.setTitle("Successfully Sent");
                    showDialog.setCancelable(false);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(ChildrenUnderTwo.this, ChildrenUnderTwo.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(ChildrenUnderTwo.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                if(!CheckValues.isFromSavedFrom){
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

//                    Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();

                    final Dialog showDialog = new Dialog(context);
                    showDialog.setContentView(R.layout.thank_you_popup);
                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

                    showDialog.setTitle("Successfully Sent");
                    showDialog.setCancelable(false);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(ChildrenUnderTwo.this, ChildrenUnderTwo.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(ChildrenUnderTwo.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
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


    //=====================multispinner selection ============================//

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            tvMultispinnerVaccination.setVisibility(View.VISIBLE);


            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    tvMultispinnerVaccination.setText(builder.append(adapter.getItem(i)).append("&"));
                }

            }

//            Toast.makeText(ChildrenUnderTwo.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    //==========================multiselection spinner selection code ends here========================//


}
