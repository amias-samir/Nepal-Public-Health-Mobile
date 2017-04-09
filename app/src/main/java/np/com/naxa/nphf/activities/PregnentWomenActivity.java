package np.com.naxa.nphf.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
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
import android.support.design.widget.Snackbar;
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
import np.com.naxa.nphf.database.DataBaseConserVationTracking;
import np.com.naxa.nphf.dialog.Default_DIalog;
import np.com.naxa.nphf.gps.GPS_TRACKER_FOR_POINT;
import np.com.naxa.nphf.gps.MapPointActivity;
import np.com.naxa.nphf.model.CheckValues;
import np.com.naxa.nphf.model.Constants;
import np.com.naxa.nphf.model.StaticListOfCoordinates;
import np.com.naxa.nphf.model.UrlClass;

public class PregnentWomenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "PregnentWomenActivity";
    public static Toolbar toolbar;
    int CAMERA_PIC_REQUEST = 2;
    Spinner spinnerANCVisit, spinnerTD, spinnerTDPlus, spinnerVitA, spinnerReceivedIron, spinnerGravawatiBhet, spinnerFCHVsHelp;
    ArrayAdapter ancVisitAdpt, tdAdpt, tdPlusAdapter, vitaAdpt, receivedIronAdapter, garvawatiBhetAdapter, fchvHelpAdapter;
    Button send, save, startGps, previewMap;
    ProgressDialog mProgressDlg;
    Context context = this;
    GPS_TRACKER_FOR_POINT gps;
    String jsonToSend, photoTosend;
    String imagePath, encodedImage = " ", imageName = "pregnent_women";
    ImageButton photo;
    boolean isGpsTracking = false;
    boolean isGpsTaken = false;
    double initLat;
    double finalLat;
    double initLong;
    double finalLong;
    ImageView previewImageSite;
    Bitmap thumbnail;
    PendingIntent pendingIntent;
    BroadcastReceiver mReceiver;
    AlarmManager alarmManager;
    ArrayList<LatLng> listCf = new ArrayList<LatLng>();
    List<Location> gpslocation = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    String latLangArray = "", jsonLatLangArray = "";

    AutoCompleteTextView tvPregnentWomenName, tvVDCName, tvWardNo, tvEthnicity, tvAge, tvLMP, tvEDD, tvContactNo, tvSMName;
    EditText tvVisitDate, tvVisitTime, tvDeliveryDate;

    String pregenent_women_name, vdc_name, ward_no, ethnicity, age, lmp, edd, visit_date, visit_time, delivery_date, contact_no, sm_name,
            anc_visit, td, td_plus, vit_a, received_iron, garvawati_bhet, fchv_help, img;

    JSONArray jsonArrayGPS = new JSONArray();

    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;
    String dataSentStatus, dateString;

    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 999;
    static final int DELIVERY_DATE_DIALOG_ID = 99;


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
        setContentView(R.layout.activity_pregnent_women);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recording tool for pregnant women");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**₧
         * Author Susan Lama
         */
        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        tvPregnentWomenName = (AutoCompleteTextView) findViewById(R.id.pregnent_women_name);
        tvVDCName = (AutoCompleteTextView) findViewById(R.id.pregnent_women_vdc_name);
        tvWardNo = (AutoCompleteTextView) findViewById(R.id.pregnent_women_ward_no);
        tvEthnicity = (AutoCompleteTextView) findViewById(R.id.pregnent_women_ethnicity);
        tvAge = (AutoCompleteTextView) findViewById(R.id.pregnent_women_age);
        tvLMP = (AutoCompleteTextView) findViewById(R.id.pregnent_women_lmp);
        tvEDD = (AutoCompleteTextView) findViewById(R.id.pregnent_women_edd);
        tvContactNo = (AutoCompleteTextView) findViewById(R.id.pregnent_women_contact_no);
        tvSMName = (AutoCompleteTextView) findViewById(R.id.pregnent_women_sm_name);

        tvVisitDate = (EditText) findViewById(R.id.pregnent_women_visit_date);
        tvVisitTime = (EditText) findViewById(R.id.pregnent_women_visit_time);
        tvDeliveryDate = (EditText) findViewById(R.id.pregnent_women_delivery_date);
        setCurrentDateOnView();
        addListenerOnButton();
        setCurrentTimeOnView();
        addListenerOnTimeButton();

        photo = (ImageButton) findViewById(R.id.pregnent_women_photo_site);
        previewImageSite = (ImageView) findViewById(R.id.pregnent_women_PhotographSiteimageViewPreview);
        previewImageSite.setVisibility(View.GONE);

        spinnerANCVisit = (Spinner) findViewById(R.id.pregnent_women_anc_visit_type);
        spinnerTD = (Spinner) findViewById(R.id.pregnant_women_45days_iron);
        spinnerTDPlus = (Spinner) findViewById(R.id.pregnent_women_td_plus);
        spinnerVitA = (Spinner) findViewById(R.id.pregnent_women_vitA);
        spinnerReceivedIron = (Spinner) findViewById(R.id.pregnent_women_iron);
        spinnerGravawatiBhet = (Spinner) findViewById(R.id.pregnent_women_garvawati_bhet);
        spinnerFCHVsHelp = (Spinner) findViewById(R.id.pregnent_women_fchv_help);

        send = (Button) findViewById(R.id.pregnent_women_send);
        save = (Button) findViewById(R.id.pregnent_women_save);
        startGps = (Button) findViewById(R.id.pregnent_women_GpsStart);
        //endGps = (Button) findViewById(R.id.human_casualty_GpsEnd);
        previewMap = (Button) findViewById(R.id.pregnent_women_preview_map);
        previewMap.setEnabled(false);

//        gps = new GPS_TRACKER_FOR_POINT(this);

        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

//anc visit spinner
        ancVisitAdpt = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.ANC_VISIT);
        ancVisitAdpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerANCVisit.setAdapter(ancVisitAdpt);
        spinnerANCVisit.setOnItemSelectedListener(this);
//td spinner
        tdAdpt = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.YES_NO);
        tdAdpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTD.setAdapter(tdAdpt);
        spinnerTD.setOnItemSelectedListener(this);
//td plus spinner
        tdPlusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.YES_NO);
        tdPlusAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTDPlus.setAdapter(tdPlusAdapter);
        spinnerTDPlus.setOnItemSelectedListener(this);

        //Vit A spinner
        vitaAdpt = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.YES_NO);
        vitaAdpt
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVitA.setAdapter(vitaAdpt);
        spinnerVitA.setOnItemSelectedListener(this);

        //received iron spinner
        receivedIronAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.YES_NO);
        receivedIronAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReceivedIron.setAdapter(receivedIronAdapter);
        spinnerReceivedIron.setOnItemSelectedListener(this);

        //td spinner
        garvawatiBhetAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.YES_NO);
        garvawatiBhetAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGravawatiBhet.setAdapter(garvawatiBhetAdapter);
        spinnerGravawatiBhet.setOnItemSelectedListener(this);

        //td spinner
        fchvHelpAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.YES_NO);
        fchvHelpAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFCHVsHelp.setAdapter(fchvHelpAdapter);
        spinnerFCHVsHelp.setOnItemSelectedListener(this);

        initilizeUI();

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, 1);
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
                    gps = new GPS_TRACKER_FOR_POINT(PregnentWomenActivity.this);
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
                    startActivity(new Intent(PregnentWomenActivity.this, MapPointActivity.class));
                } else {

                    if (GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {
                        StaticListOfCoordinates.setList(listCf);
                        startActivity(new Intent(PregnentWomenActivity.this, MapPointActivity.class));
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
                        pregenent_women_name = tvPregnentWomenName.getText().toString();
                        vdc_name = tvVDCName.getText().toString();
                        ward_no = tvWardNo.getText().toString();
                        ethnicity = tvEthnicity.getText().toString();
                        age = tvAge.getText().toString();
                        lmp = tvLMP.getText().toString();
                        edd = tvEDD.getText().toString();
                        visit_date = tvVisitDate.getText().toString();
                        visit_time = tvVisitTime.getText().toString();
                        delivery_date = tvDeliveryDate.getText().toString();
                        contact_no = tvContactNo.getText().toString();
                        sm_name = tvSMName.getText().toString();
                        img = encodedImage;
                        jsonLatLangArray = jsonArrayGPS.toString();


                        convertDataToJson();

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;

                        final Dialog showDialog = new Dialog(context);
                        showDialog.setContentView(R.layout.date_input_layout);
                        final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                        final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);
                        FormNameToInput.setText("Recording Tool For Pregnent Women");

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
                                    String[] data = new String[]{"1", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + imageName, "Not Sent", "0"};

                                    DataBaseConserVationTracking dataBaseConserVationTracking = new DataBaseConserVationTracking(context);
                                    dataBaseConserVationTracking.open();
                                    long id = dataBaseConserVationTracking.insertIntoTable_Main(data);

//                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                                            .setTitleText("Job done!")
//                                            .setContentText("Data saved successfully!")
//                                            .show();
//                                    dataBaseConserVationTracking.close();
                                    Toast.makeText(PregnentWomenActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
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
        // add click listener to Button "POST"
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pregenent_women_name = tvPregnentWomenName.getText().toString();
                vdc_name = tvVDCName.getText().toString();
                ward_no = tvWardNo.getText().toString();
                ethnicity = tvEthnicity.getText().toString();
                age = tvAge.getText().toString();
                lmp = tvLMP.getText().toString();
                edd = tvEDD.getText().toString();
                visit_date = tvVisitDate.getText().toString();
                visit_time = tvVisitTime.getText().toString();
                img = encodedImage;
                delivery_date = tvDeliveryDate.getText().toString();
                contact_no = tvContactNo.getText().toString();
                sm_name = tvSMName.getText().toString();


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

            }


        });

    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int spinnerId = parent.getId();

        if (spinnerId == R.id.pregnent_women_anc_visit_type) {
            switch (position) {
                case 0:
                    anc_visit = "First Visit";
                    break;
                case 1:
                    anc_visit = "Second Visit";
                    break;
                case 2:
                    anc_visit = "Third Visit";
                    break;
                case 3:
                    anc_visit = "Fourth Visit";
                    break;
            }
        }

        if (spinnerId == R.id.pregnant_women_45days_iron) {
            switch (position) {
                case 0:
                    td = "Yes";
                    break;
                case 1:
                    td = "No";
                    break;
            }
        }

        if (spinnerId == R.id.pregnent_women_td_plus) {
            switch (position) {
                case 0:
                    td_plus = "Yes";
                    break;
                case 1:
                    td_plus = "No";
                    break;
            }
        }

        if (spinnerId == R.id.pregnent_women_vitA) {
            switch (position) {
                case 0:
                    vit_a = "Yes";
                    break;
                case 1:
                    vit_a = "No";
                    break;
            }
        }

        if (spinnerId == R.id.pregnent_women_iron) {
            switch (position) {
                case 0:
                    received_iron = "Yes";
                    break;
                case 1:
                    received_iron = "No";
                    break;
            }
        }

        if (spinnerId == R.id.pregnent_women_garvawati_bhet) {
            switch (position) {
                case 0:
                    garvawati_bhet = "Yes";
                    break;
                case 1:
                    garvawati_bhet = "No";
                    break;
            }
        }

        if (spinnerId == R.id.pregnent_women_fchv_help) {
            switch (position) {
                case 0:
                    fchv_help = "Yes";
                    break;
                case 1:
                    fchv_help = "No";
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        imageName = "Pregnent_Women" + timeInMillis;

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
            isGpsTaken=true;
            previewMap.setEnabled(true);
            Bundle bundle = intent.getExtras();
            String jsonToParse = (String) bundle.get("JSON1");
            imageName = (String) bundle.get("photo");
            String gpsLocationtoParse = (String) bundle.get("gps");

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
            gps = new GPS_TRACKER_FOR_POINT(PregnentWomenActivity.this);
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

            header.put("tablename", "recording_tool_for_pregnant_woman");
            header.put("name_of_VDC", vdc_name);
            header.put("name_of_SM", sm_name);
            header.put("name_of_pregnant_women", pregenent_women_name);
            header.put("ward", ward_no);
            header.put("age", age);
            header.put("ethinicity", ethnicity);
            header.put("LMP", lmp);
            header.put("EDD", edd);
            header.put("ANC_visit", anc_visit);
            header.put("date", visit_date);
            header.put("time", visit_time);
            header.put("Td", td);
            header.put("Td_+", td_plus);
            header.put("Vit_A", vit_a);
            header.put("lived_180_day", received_iron);
            header.put("garvawati_het_recieved", garvawati_bhet);
            header.put("did_FCHVs_helped_her_to_prepare_birth_preparedness_plan", fchv_help);
            header.put("date_of_delivery", delivery_date);
            header.put("contact_no_of_family_member", contact_no);
            header.put("lat", finalLat);
            header.put("lon", finalLong);
            header.put("image", encodedImage);


            jsonToSend = header.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Nishon", jsonToSend);

        sendDatToserver();
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
        Log.e("PregnentWomenActivity", "json : " + jsonObj.toString());

        vdc_name = jsonObj.getString("name_of_VDC");
        sm_name = jsonObj.getString("name_of_SM");
        pregenent_women_name = jsonObj.getString("name_of_pregnant_women");
        ward_no = jsonObj.getString("ward");
        age = jsonObj.getString("age");
        ethnicity = jsonObj.getString("ethinicity");
        lmp = jsonObj.getString("LMP");
        edd = jsonObj.getString("EDD");
        anc_visit = jsonObj.getString("ANC_visit");
        visit_date = jsonObj.getString("date");
        visit_time = jsonObj.getString("time");
        td = jsonObj.getString("Td");
        td_plus = jsonObj.getString("Td_+");
        vit_a = jsonObj.getString("Vit_A");
        received_iron = jsonObj.getString("lived_180_day");
        garvawati_bhet = jsonObj.getString("garvawati_het_recieved");
        fchv_help = jsonObj.getString("did_FCHVs_helped_her_to_prepare_birth_preparedness_plan");
        delivery_date = jsonObj.getString("date_of_delivery");
        contact_no = jsonObj.getString("contact_no_of_family_member");
        finalLat = Double.parseDouble(jsonObj.getString("lat"));
        finalLong = Double.parseDouble(jsonObj.getString("lon"));
        LatLng d = new LatLng(finalLat, finalLong);
        listCf.add(d);
        encodedImage = jsonObj.getString("image");


        Log.e("Pregnent Women", "Parsed data " + pregenent_women_name + anc_visit + contact_no);

        tvPregnentWomenName.setText(pregenent_women_name);
        tvVDCName.setText(vdc_name);
        tvWardNo.setText(ward_no);
        tvEthnicity.setText(ethnicity);
        tvAge.setText(age);
        tvLMP.setText(lmp);
        tvEDD.setText(edd);
        tvContactNo.setText(contact_no);
        tvSMName.setText(sm_name);
        tvDeliveryDate.setText(delivery_date);
        tvVisitDate.setText(visit_date);
        tvVisitTime.setText(visit_time);


        int setANCVisit = ancVisitAdpt.getPosition(anc_visit);
        spinnerANCVisit.setSelection(setANCVisit);

        int setTD = tdAdpt.getPosition(td);
        spinnerTD.setSelection(setTD);

        int setTDPlus = tdPlusAdapter.getPosition(td_plus);
        spinnerTDPlus.setSelection(setTDPlus);

        int setVitA = vitaAdpt.getPosition(vit_a);
        spinnerVitA.setSelection(setVitA);

        int setReceivedIron = receivedIronAdapter.getPosition(received_iron);
        spinnerReceivedIron.setSelection(setReceivedIron);

        int setGarvawati = garvawatiBhetAdapter.getPosition(garvawati_bhet);
        spinnerGravawatiBhet.setSelection(setGarvawati);

        int setFCVHelp = fchvHelpAdapter.getPosition(fchv_help);
        spinnerFCHVsHelp.setSelection(setFCVHelp);


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

            if(mProgressDlg != null && mProgressDlg.isShowing()){
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
                previewImageSite.setVisibility(View.VISIBLE);

                tvPregnentWomenName.setText(pregenent_women_name);
                tvVDCName.setText(vdc_name);
                tvWardNo.setText(ward_no);
                tvEthnicity.setText(ethnicity);
                tvAge.setText(age);
                tvLMP.setText(lmp);
                tvEDD.setText(edd);
                tvContactNo.setText(contact_no);
                tvSMName.setText(sm_name);
                tvDeliveryDate.setText(delivery_date);
                tvVisitDate.setText(visit_date);
                tvVisitTime.setText(visit_time);
                previewImageSite.setImageBitmap(thumbnail);

                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                dateString = sdf.format(date);
//                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                        .setTitleText("")
//                        .setContentText("Data sent successfully!")
//                        .show();
                String[] data = new String[]{"1", "Recording Tool For Pregnent Women", dateString, jsonToSend, jsonLatLangArray,
                        "" + imageName, "Sent", "0"};

                DataBaseConserVationTracking dataBaseConserVationTracking = new DataBaseConserVationTracking(context);
                dataBaseConserVationTracking.open();
                long id = dataBaseConserVationTracking.insertIntoTable_Main(data);
                Log.e("dbID", "" + id);
                dataBaseConserVationTracking.close();


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
        tvDeliveryDate.setText(new StringBuilder()
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

        tvDeliveryDate.setOnClickListener(new View.OnClickListener() {

            //            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvDeliveryDate.setShowSoftInputOnFocus(false);
                }
                showDialog(DELIVERY_DATE_DIALOG_ID);
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

            case DELIVERY_DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, deliveryDatePickerListener, year, month,
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
            tvDeliveryDate.setText(new StringBuilder().append(year)
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(PregnentWomenActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PregnentWomenActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(PregnentWomenActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(PregnentWomenActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (ActivityCompat.checkSelfPermission(PregnentWomenActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForGPS();
                Log.v("Susan", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(PregnentWomenActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
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
                            status.startResolutionForResult(PregnentWomenActivity.this, GPS_SETTINGS);
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
}
