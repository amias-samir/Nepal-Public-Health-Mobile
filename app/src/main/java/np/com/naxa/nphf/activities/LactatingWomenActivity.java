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
import android.widget.RelativeLayout;
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


public class LactatingWomenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "LactatingWomenActivity";
    public static Toolbar toolbar;
    int CAMERA_PIC_REQUEST = 2;
    Spinner spinnerVDCName, spinnerWardNo, pnc_visit_spinner, delivery_at_place_spinner, birth_attended_by_lcw_spinner,
            lactaing_women_3rd_labour_spinner, oxytocin_received_after_birth_spinner,
            lactating_women_neonates_asphysia_spinner, lactating_women_45days_iron_spinner,
            lactating_women_vitaminA_spinner, lactating_women_neonatal_records_spinner,
            lactating_women_breastfeed_in1hour_spinner, lactating_women_exclusive_breastfeeding_spinner;

    ArrayAdapter vdcNameadpt, wardNoadpt, pnc_visit_adpt, delivery_at_place_adpt, birth_attended_by_lcw_adpt,
            lactaing_women_3rd_labour_adpt, oxytocin_received_after_birth_adpt,
            lactating_women_neonates_asphysia_adpt, lactating_women_45days_iron_adtp,
            lactating_women_vitaminA_adpt, lactating_women_neonatal_records_adpt,
            lactating_women_breastfeed_in1hour_adpt, lactating_women_exclusive_breastfeeding_adpt;

    Button send, save, startGps, previewMap;
    ImageButton pic;
    boolean isGpsTracking = false;
    boolean isGpsTaken = false;
    double finalLat;
    double finalLong;
    String formid, formNameSavedForm = "" ;
    ImageView previewImageSite;
    Bitmap thumbnail;
    ProgressDialog mProgressDlg;
    Context context = this;
    GPS_TRACKER_FOR_POINT gps;
    String jsonToSend, photoTosend;
    String imagePath, encodedImage = "", imageName = "no_photo";

    ArrayList<LatLng> listCf = new ArrayList<LatLng>();
    List<Location> gpslocation = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    String latLangArray = "", jsonLatLangArray = "";

    AutoCompleteTextView tvLactatingWomenName,  tvEthnicity, tvAge, tvsmName, tvBirthAttendedByOthers;
//    tvVDCName, tvWardNo,
    EditText tvVisitDate, tvVisitTime, tvDeliveryDate;
    CardView cv_Send_Save;

    RelativeLayout rlBirthAttendedBy ;


    String lactating_women_name, vdc_name, ward_no, ethnicity, age, pnc_visit, deliver_place, delivery_date,
            birth_attended_by, birth_attended_by_others, third_labour, oxytocin_received, neonates_asphysia, img,
            fourtyfivedays_iron, vitaminA, neonatal_records, breastfeed_in1hour, exclusive_breastfeeding, sm_name, visit_date, visit_time;

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
    JSONArray jsonArrayGPS = new JSONArray();


    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;
    String dataSentStatus, dateString;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lactating_women);


        toolbar = (Toolbar) findViewById(R.id.lactating_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recording tool for Lactating women");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        // casting text view
        tvLactatingWomenName = (AutoCompleteTextView) findViewById(R.id.lactating_women_name);

        rlBirthAttendedBy = (RelativeLayout) findViewById(R.id.rlbirthattended);
        tvBirthAttendedByOthers = (AutoCompleteTextView) findViewById(R.id.lactating_women_birth_attended_by_others);
//        tvBirthAttendedByOthers.setVisibility(View.INVISIBLE);
        tvEthnicity = (AutoCompleteTextView) findViewById(R.id.lactating_women_ethnicity);
        tvAge = (AutoCompleteTextView) findViewById(R.id.lactating_women_age);
        tvsmName = (AutoCompleteTextView) findViewById(R.id.lactating_women_sm_name);
        tvVisitDate = (EditText) findViewById(R.id.lactating_women_visit_date);
        tvVisitTime = (EditText) findViewById(R.id.lactating_women_visit_time);
        tvDeliveryDate = (EditText) findViewById(R.id.lactating_women_delivery_date);
        pic = (ImageButton) findViewById(R.id.lactating_women_photo_site);
        previewImageSite = (ImageView) findViewById(R.id.lactating_women_PhotographSiteimageViewPreview);
        previewImageSite.setVisibility(View.GONE);
        startGps = (Button) findViewById(R.id.lactating_women_GpsStart);
        previewMap = (Button) findViewById(R.id.lactating_women_preview_map);
        cv_Send_Save = (CardView) findViewById(R.id.cv_SaveSend);

        previewMap.setEnabled(false);

        setCurrentDateOnView();
        addListenerOnButton();
        setCurrentTimeOnView();
        addListenerOnTimeButton();


        // initialiting spinners
        spinnerVDCName = (Spinner) findViewById(R.id.lactating_women_vdc_name);
        spinnerWardNo = (Spinner) findViewById(R.id.lactating_women_ward_no);
        pnc_visit_spinner = (Spinner) findViewById(R.id.pnc_visit_spinner);
        delivery_at_place_spinner = (Spinner) findViewById(R.id.delivery_at_place);
        birth_attended_by_lcw_spinner = (Spinner) findViewById(R.id.birth_attended_by_lcw);
        lactaing_women_3rd_labour_spinner = (Spinner) findViewById(R.id.lactating_women_3rd_labour);
        oxytocin_received_after_birth_spinner = (Spinner) findViewById(R.id.oxytocin_received_after_birth);
        lactating_women_neonates_asphysia_spinner = (Spinner) findViewById(R.id.lactating_women_neonates_asphysia);
        lactating_women_45days_iron_spinner = (Spinner) findViewById(R.id.lactating_women_45days_iron);
        lactating_women_vitaminA_spinner = (Spinner) findViewById(R.id.lactating_women_vitaminA);
        lactating_women_neonatal_records_spinner = (Spinner) findViewById(R.id.lactating_women_neonatal_records);
        lactating_women_breastfeed_in1hour_spinner = (Spinner) findViewById(R.id.lactating_women_breastfeed_in1hour);
        lactating_women_exclusive_breastfeeding_spinner = (Spinner) findViewById(R.id.lactating_women_exclusive_breastfeeding);

        // buttons
        send = (Button) findViewById(R.id.lactating_women_send);
        save = (Button) findViewById(R.id.lactating_women_save);

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

        // pnc visit adapters
        pnc_visit_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.PNC_VISIT);
        pnc_visit_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pnc_visit_spinner.setAdapter(pnc_visit_adpt);
        pnc_visit_spinner.setOnItemSelectedListener(this);

        // birth_attended_by_lcw of lactating women
        birth_attended_by_lcw_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.ATTENDED_BIRTH_BY);
        birth_attended_by_lcw_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        birth_attended_by_lcw_spinner.setAdapter(birth_attended_by_lcw_adpt);
        birth_attended_by_lcw_spinner.setOnItemSelectedListener(this);

        // delivery place of a child by a lactating mother
        delivery_at_place_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.BIRTH_PLACE);
        delivery_at_place_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delivery_at_place_spinner.setAdapter(delivery_at_place_adpt);
        delivery_at_place_spinner.setOnItemSelectedListener(this);

        // neonatal national records
        lactating_women_neonatal_records_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.NATIONAL_NEONATES);
        lactating_women_neonatal_records_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_neonatal_records_spinner.setAdapter(lactating_women_neonatal_records_adpt);
        lactating_women_neonatal_records_spinner.setOnItemSelectedListener(this);

        // adapter  receiving active management in 3rd labour by lactating women
        // 3rd labour adapters
        lactaing_women_3rd_labour_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.YES_NO_NA);
        lactaing_women_3rd_labour_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactaing_women_3rd_labour_spinner.setAdapter(lactaing_women_3rd_labour_adpt);
        lactaing_women_3rd_labour_spinner.setOnItemSelectedListener(this);

        // received oxytocin after delivery for lactating women
        oxytocin_received_after_birth_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.YES_NO_NA);
        oxytocin_received_after_birth_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        oxytocin_received_after_birth_spinner.setAdapter(oxytocin_received_after_birth_adpt);
        oxytocin_received_after_birth_spinner.setOnItemSelectedListener(this);

        // received neonates with birth asphysia
        lactating_women_neonates_asphysia_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.YES_NO_NA);
        lactating_women_neonates_asphysia_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_neonates_asphysia_spinner.setAdapter(lactating_women_neonates_asphysia_adpt);
        lactating_women_neonates_asphysia_spinner.setOnItemSelectedListener(this);

        // receives 45 days of iron by lactating women
        lactating_women_45days_iron_adtp = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.YES_NO);
        lactating_women_45days_iron_adtp.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_45days_iron_spinner.setAdapter(lactating_women_45days_iron_adtp);
        lactating_women_45days_iron_spinner.setOnItemSelectedListener(this);

        // vitamin A
        lactating_women_vitaminA_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.YES_NO);
        lactating_women_vitaminA_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_vitaminA_spinner.setAdapter(lactating_women_vitaminA_adpt);
        lactating_women_vitaminA_spinner.setOnItemSelectedListener(this);

        // breast feeding to infant by lactating mother with in an hour
        lactating_women_breastfeed_in1hour_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.YES_NO);
        lactating_women_breastfeed_in1hour_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_breastfeed_in1hour_spinner.setAdapter(lactating_women_breastfeed_in1hour_adpt);
        lactating_women_breastfeed_in1hour_spinner.setOnItemSelectedListener(this);

        // exclusive breast feeding bu lactating women
        lactating_women_exclusive_breastfeeding_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Constants.YES_NO);
        lactating_women_exclusive_breastfeeding_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_exclusive_breastfeeding_spinner.setAdapter(lactating_women_breastfeed_in1hour_adpt);
        lactating_women_exclusive_breastfeeding_spinner.setOnItemSelectedListener(this);

        initilizeUI();

        // for picture click
        pic.setOnClickListener(new View.OnClickListener() {
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
                    gps = new GPS_TRACKER_FOR_POINT(LactatingWomenActivity.this);
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
                    startActivity(new Intent(LactatingWomenActivity.this, MapPointActivity.class));
                } else {
                    if (GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {
                        StaticListOfCoordinates.setList(listCf);
                        startActivity(new Intent(LactatingWomenActivity.this, MapPointActivity.class));
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
                        lactating_women_name = tvLactatingWomenName.getText().toString();
                        birth_attended_by_others = tvBirthAttendedByOthers.getText().toString();
//                        ward_no = tvWardNo.getText().toString();
                        ethnicity = tvEthnicity.getText().toString();
                        age = tvAge.getText().toString();
                        img = encodedImage;
                        sm_name = tvsmName.getText().toString();
                        visit_date = tvVisitDate.getText().toString();
                        delivery_date = tvDeliveryDate.getText().toString();
                        visit_time = tvVisitTime.getText().toString();
                        img = encodedImage;
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
                            FormNameToInput.setText("Recording Tool For Lactating Women");
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
                                    String[] data = new String[]{"2", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + imageName, "Not Sent", "0"};

                                    DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealthNotSent = new DataBaseNepalPublicHealth_NotSent(context);
                                    dataBaseNepalPublicHealthNotSent.open();
                                    dataBaseNepalPublicHealthNotSent.insertIntoTable_Main(data);

                                    Toast.makeText(LactatingWomenActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
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
                                            Intent intent = new Intent(LactatingWomenActivity.this, SavedFormsActivity.class);
                                            startActivity(intent);
//                                finish();
                                        }
                                    });

                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog.dismiss();
                                            Intent intent = new Intent(LactatingWomenActivity.this, MainActivity.class);
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

                lactating_women_name = tvLactatingWomenName.getText().toString();
                birth_attended_by_others = tvBirthAttendedByOthers.getText().toString();
//                ward_no = tvWardNo.getText().toString();
                ethnicity = tvEthnicity.getText().toString();
                age = tvAge.getText().toString();
                img = encodedImage;
                sm_name = tvsmName.getText().toString();
                visit_date = tvVisitDate.getText().toString();
                delivery_date = tvDeliveryDate.getText().toString();
                visit_time = tvVisitTime.getText().toString();

                // check internet
                if (networkInfo != null && networkInfo.isConnected()) {
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

                    final Dialog showDialog = new Dialog(context);
                    showDialog.setContentView(R.layout.alert_dialog_before_send);
                    final Button yes = (Button) showDialog.findViewById(R.id.alertButtonYes);
                    final Button no = (Button) showDialog.findViewById(R.id.alertButtonNo);

                    // warning dialog
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
                            // data goes here
                            convertDataToJson();
                            sendDatToserver();

                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                        }
                    });
                } else {
                    final View coordinatorLayoutView = findViewById(R.id.activity_lactating_women);
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


        if(SpinnerID == R.id.lactating_women_vdc_name){
            vdc_name = Constants.VDC_NAME[position];
            Log.e(TAG, "onItemSelected: "+vdc_name );

        }

        if(SpinnerID == R.id.lactating_women_ward_no){
            ward_no = Constants.VDC_WARD_NO[position];
            Log.e(TAG, "onItemSelected: "+ward_no );

        }

        if (SpinnerID == R.id.pnc_visit_spinner) {
            switch (position) {
                case 0:
                    pnc_visit = "First Visit";
                    break;
                case 1:
                    pnc_visit = "Second Visit";
                    break;
                case 2:
                    pnc_visit = "Third Visit";
                    break;
            }
        }

        if (SpinnerID == R.id.delivery_at_place) {
            switch (position) {
                case 0:
                    deliver_place = "Home";
                    break;
                case 1:
                    deliver_place = "Hospital";
                    break;
            }
        }

        if (SpinnerID == R.id.birth_attended_by_lcw) {
            switch (position) {
                case 0:
                    birth_attended_by = "SBA";
                    rlBirthAttendedBy.setVisibility(View.INVISIBLE);
                    tvBirthAttendedByOthers.setText("");
                    break;
                case 1:
                    birth_attended_by = "HW";
                    tvBirthAttendedByOthers.setText("");
                    rlBirthAttendedBy.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    birth_attended_by = "Others than SBA/HW";

                    rlBirthAttendedBy.setVisibility(View.VISIBLE);


                    break;
            }
        }


        if (SpinnerID == R.id.lactating_women_3rd_labour) {
            switch (position) {
                case 0:
                    third_labour = "Yes";
                    break;
                case 1:
                    third_labour = "No";
                    break;
                case 2:
                    third_labour = "N/A";
                    break;
            }
        }

        if (SpinnerID == R.id.oxytocin_received_after_birth) {
            switch (position) {
                case 0:
                    oxytocin_received = "Yes";
                    break;
                case 1:
                    oxytocin_received = "No";
                    break;
                case 2:
                    oxytocin_received = "N/A";
                    break;
            }
        }

        if (SpinnerID == R.id.lactating_women_neonates_asphysia) {
            switch (position) {
                case 0:
                    neonates_asphysia = "Yes";
                    break;
                case 1:
                    neonates_asphysia = "No";
                    break;
                case 2:
                    neonates_asphysia = "N/A";
                    break;
            }
        }

        if (SpinnerID == R.id.lactating_women_45days_iron) {
            switch (position) {
                case 0:
                    fourtyfivedays_iron = "Yes";
                    break;
                case 1:
                    fourtyfivedays_iron = "No";
                    break;
            }
        }

        if (SpinnerID == R.id.lactating_women_vitaminA) {
            switch (position) {
                case 0:
                    vitaminA = "Yes";
                    break;
                case 1:
                    vitaminA = "No";
                    break;
            }
        }

        if (SpinnerID == R.id.lactating_women_neonatal_records) {
            switch (position) {
                case 0:
                    neonatal_records = "First checkup";
                    break;
                case 1:
                    neonatal_records = "Second checkup";
                    break;
                case 2:
                    neonatal_records = "Third checkup";
                    break;
            }
        }

        if (SpinnerID == R.id.lactating_women_breastfeed_in1hour) {
            switch (position) {
                case 0:
                    breastfeed_in1hour = "Yes";
                    break;
                case 1:
                    breastfeed_in1hour = "No";
                    break;
            }
        }

        if (SpinnerID == R.id.lactating_women_exclusive_breastfeeding) {
            switch (position) {
                case 0:
                    exclusive_breastfeeding = "Yes";
                    break;
                case 1:
                    exclusive_breastfeeding = "No";
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

        imageName = "Lactating_Women" + timeInMillis;

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
                pnc_visit_spinner.setEnabled(false);
                delivery_at_place_spinner.setEnabled(false);
                birth_attended_by_lcw_spinner.setEnabled(false);
                lactaing_women_3rd_labour_spinner.setEnabled(false);
                oxytocin_received_after_birth_spinner.setEnabled(false);
                lactating_women_neonates_asphysia_spinner.setEnabled(false);
                lactating_women_45days_iron_spinner.setEnabled(false);
                lactating_women_vitaminA_spinner.setEnabled(false);
                lactating_women_neonatal_records_spinner.setEnabled(false);
                lactating_women_breastfeed_in1hour_spinner.setEnabled(false);
                lactating_women_exclusive_breastfeeding_spinner.setEnabled(false);
                spinnerVDCName.setEnabled(false);
                spinnerWardNo.setEnabled(false);
                tvLactatingWomenName.setEnabled(false);
                tvBirthAttendedByOthers.setEnabled(false);
//                tvWardNo.setEnabled(false);
                tvEthnicity.setEnabled(false);
                tvAge.setEnabled(false);
                tvsmName.setEnabled(false);
                tvVisitDate.setEnabled(false);
                tvDeliveryDate.setEnabled(false);
                tvVisitTime.setEnabled(false);
                pic.setEnabled(false);
                startGps.setEnabled(false);
                cv_Send_Save.setVisibility(View.GONE);

            }

            Log.e("LactatingWomen", "i-" + imageName);

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
                Log.e("Lactating_Women", "" + jsonToParse);
//                parseArrayGPS(gpsLocationtoParse);
                parseJson(jsonToParse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            gps = new GPS_TRACKER_FOR_POINT(LactatingWomenActivity.this);
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


    public void convertDataToJson() {
        try {
            JSONObject header = new JSONObject();

            header.put("tablename", "recording_tool_for_lactating_woman");
            header.put("name_of_SM", sm_name);
            header.put("date", visit_date);
            header.put("delivery_date", delivery_date);
            header.put("time", visit_time);
            header.put("name_of_lactating_woman", lactating_women_name);
            header.put("name_of_vdc", vdc_name);
            header.put("ward_no", ward_no);
            header.put("age", age);
            header.put("ethnicity", ethnicity);
            header.put("delivery_at", deliver_place);
            header.put("birth_attended_by", birth_attended_by);
            header.put("birth_attended_by_if_other", birth_attended_by_others);
            header.put("recieved_active_management_of_third_stage_laour", third_labour);
            header.put("recieved_oxytocin_after_delivery", oxytocin_received);
            header.put("neonates_with_birth_asphyxia", neonates_asphysia);
            header.put("PNC_visit", pnc_visit);
            header.put("received_45_days_supply_of_iron", fourtyfivedays_iron);
            header.put("vit_A", vitaminA);
            header.put("neonatal_check_ups", neonatal_records);
            header.put("breast_feeding_within_one_hour_of_birth", breastfeed_in1hour);
            header.put("exclusive_breast_feeding", exclusive_breastfeeding);
            header.put("lat", finalLat);
            header.put("lon", finalLong);
            header.put("image", encodedImage);


            jsonToSend = header.toString();

            Log.e(TAG, "SAMIR: " + jsonToSend);


        } catch (Exception e) {
            e.printStackTrace();
        }

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
        Log.e("LactatingWomenActivity", "json : " + jsonObj.toString());


        finalLat = Double.parseDouble(jsonObj.getString("lat"));
        finalLong = Double.parseDouble(jsonObj.getString("lon"));
        LatLng d = new LatLng(finalLat, finalLong);
        listCf.add(d);

        sm_name = jsonObj.getString("name_of_SM");
        visit_date = jsonObj.getString("date");
        delivery_date = jsonObj.getString("delivery_date");
        visit_time = jsonObj.getString("time");

        lactating_women_name = jsonObj.getString("name_of_lactating_woman");
        vdc_name = jsonObj.getString("name_of_vdc");
        ward_no = jsonObj.getString("ward_no");
        age = jsonObj.getString("age");

        ethnicity = jsonObj.getString("ethnicity");
        deliver_place = jsonObj.getString("delivery_at");
        birth_attended_by = jsonObj.getString("birth_attended_by");
        birth_attended_by_others = jsonObj.getString("birth_attended_by_if_other");

        third_labour = jsonObj.getString("recieved_active_management_of_third_stage_laour");
        oxytocin_received = jsonObj.getString("recieved_oxytocin_after_delivery");
        neonates_asphysia = jsonObj.getString("neonates_with_birth_asphyxia");

        pnc_visit = jsonObj.getString("PNC_visit");
        fourtyfivedays_iron = jsonObj.getString("received_45_days_supply_of_iron");
        vitaminA = jsonObj.getString("vit_A");
        neonatal_records = jsonObj.getString("neonatal_check_ups");
        breastfeed_in1hour = jsonObj.getString("breast_feeding_within_one_hour_of_birth");
        exclusive_breastfeeding = jsonObj.getString("exclusive_breast_feeding");

        encodedImage = jsonObj.getString("image");

        Log.e("Lactating Women ", "Parsed data " + neonates_asphysia + " finalLat " + finalLong + " listcf" + listCf);


        tvsmName.setText(sm_name);
        tvLactatingWomenName.setText(lactating_women_name);
        tvBirthAttendedByOthers.setText(birth_attended_by_others);
//        tvWardNo.setText(ward_no);
        tvEthnicity.setText(ethnicity);
        tvAge.setText(age);
        tvVisitDate.setText(visit_date);
        tvDeliveryDate.setText(delivery_date);
        tvVisitTime.setText(visit_time);

        int setVDCName = vdcNameadpt.getPosition(vdc_name);
        spinnerVDCName.setSelection(setVDCName);

        int setWardNo = wardNoadpt.getPosition(ward_no);
        spinnerWardNo.setSelection(setWardNo);

        int setDeliveryPlace = delivery_at_place_adpt.getPosition(deliver_place);
        delivery_at_place_spinner.setSelection(setDeliveryPlace);

        int setBirthAttended = birth_attended_by_lcw_adpt.getPosition(birth_attended_by);
        birth_attended_by_lcw_spinner.setSelection(setBirthAttended);

        int setThirdLabour = lactaing_women_3rd_labour_adpt.getPosition(third_labour);
        lactaing_women_3rd_labour_spinner.setSelection(setThirdLabour);

        int setOxytocinReceived = oxytocin_received_after_birth_adpt.getPosition(oxytocin_received);
        oxytocin_received_after_birth_spinner.setSelection(setOxytocinReceived);

        int setReceivedNeonates = lactating_women_neonates_asphysia_adpt.getPosition(neonates_asphysia);
        lactating_women_neonates_asphysia_spinner.setSelection(setReceivedNeonates);

        int setPNCVisit = pnc_visit_adpt.getPosition(pnc_visit);
        pnc_visit_spinner.setSelection(setPNCVisit);

        int setFourtyfiveDaysIron = lactating_women_45days_iron_adtp.getPosition(fourtyfivedays_iron);
        lactating_women_45days_iron_spinner.setSelection(setFourtyfiveDaysIron);

        int setVITA = lactating_women_vitaminA_adpt.getPosition(vitaminA);
        lactating_women_vitaminA_spinner.setSelection(setVITA);

        int setNenotatalRecords = lactating_women_neonatal_records_adpt.getPosition(neonatal_records);
        lactating_women_neonatal_records_spinner.setSelection(setNenotatalRecords);

        int setBreastFeeding = lactating_women_breastfeed_in1hour_adpt.getPosition(breastfeed_in1hour);
        lactating_women_breastfeed_in1hour_spinner.setSelection(setBreastFeeding);

        int setExcluseBreastFeeding = lactating_women_exclusive_breastfeeding_adpt.getPosition(exclusive_breastfeeding);
        lactating_women_exclusive_breastfeeding_spinner.setSelection(setExcluseBreastFeeding);


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
                previewImageSite.setVisibility(View.VISIBLE);

                tvsmName.setText(sm_name);
                tvLactatingWomenName.setText(lactating_women_name);
                tvBirthAttendedByOthers.setText(birth_attended_by_others);
//                tvWardNo.setText(ward_no);
                tvEthnicity.setText(ethnicity);
                tvAge.setText(age);
                tvVisitDate.setText(visit_date);
                tvDeliveryDate.setText(delivery_date);
                tvVisitTime.setText(visit_time);
                previewImageSite.setImageBitmap(thumbnail);

                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                dateString = sdf.format(date);
//                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                        .setTitleText("")
//                        .setContentText("Data sent successfully!")
//                        .show();
                String[] data = new String[]{"2", "Recording Tool For Lactating Women", dateString, jsonToSend, jsonLatLangArray,
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
                            Intent intent = new Intent(LactatingWomenActivity.this, LactatingWomenActivity.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(LactatingWomenActivity.this, MainActivity.class);
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
                            Intent intent = new Intent(LactatingWomenActivity.this, LactatingWomenActivity.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(LactatingWomenActivity.this, MainActivity.class);
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
                            status.startResolutionForResult(LactatingWomenActivity.this, GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LactatingWomenActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LactatingWomenActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LactatingWomenActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(LactatingWomenActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (ActivityCompat.checkSelfPermission(LactatingWomenActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForGPS();
                Log.v("Susan", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(LactatingWomenActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
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



