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
import android.content.ContentValues;
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
import android.provider.SyncStateContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.dialog.Default_DIalog;
import np.com.naxa.nphf.gps.GPS_TRACKER_FOR_POINT;
import np.com.naxa.nphf.gps.MapPointActivity;
import np.com.naxa.nphf.model.CheckValues;
import np.com.naxa.nphf.model.Constants;
import np.com.naxa.nphf.model.StaticListOfCoordinates;
import np.com.naxa.nphf.model.UrlClass;


public class LactatingWomenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener   {

    private static final String TAG = "LactatingWomenActivity";
    public static Toolbar toolbar;
    int CAMERA_PIC_REQUEST = 2;
    Spinner pnc_visit_spinner,delivery_at_place_spinner, birth_attended_by_lcw_spinner,
            lactaing_women_3rd_labour_spinner, oxytocin_received_after_birth_spinner,
            lactating_women_neonates_asphysia_spinner,lactating_women_45days_iron_spinner,
            lactating_women_vitaminA_spinner,lactating_women_neonatal_records_spinner,
            lactating_women_breastfeed_in1hour_spinner,lactating_women_exclusive_breastfeeding_spinner;

    ArrayAdapter pnc_visit_adpt,delivery_at_place_adpt,birth_attended_by_lcw_adpt,
            lactaing_women_3rd_labour_adpt,oxytocin_received_after_birth_adpt,
            lactating_women_neonates_asphysia_adpt, lactating_women_45days_iron_adtp,
            lactating_women_vitaminA_adpt, lactating_women_neonatal_records_adpt,
            lactating_women_breastfeed_in1hour_adpt,lactating_women_exclusive_breastfeeding_adpt;

    Button send, save,startGPS,previewMAP;
    String jsToSend, picTosend;
    String imgPath, encodedImg = null, imgName = "no_photo";
    ImageButton pic;
    boolean isGpsTracking = false;
    boolean isGpsTaken = false;
    double initLat;
    double finalLat;
    double initLong;
    double finalLong;
    ImageView previewImageSite;
    Bitmap thumbnail;
    PendingIntent lactatingpendingIntent;
    BroadcastReceiver lactatingmReceiver;
    AlarmManager lactatingalarmManager;
    ArrayList<LatLng> lactatinglistCf = new ArrayList<LatLng>();
    List<Location> lactatinggpslocation = new ArrayList<>();
    StringBuilder lactatingstringBuilder = new StringBuilder();
    String lactatinglatLangArray = "", lactatingjsonLatLangArray = "";
    ProgressDialog mProgressDlg;
    Context context = this;
    GPS_TRACKER_FOR_POINT gps;
    String jsonToSend, photoTosend;
    String imagePath, encodedImage = null, imageName = "no_photo";
    ImageButton photo;
    PendingIntent pendingIntent;
    BroadcastReceiver mReceiver;
    AlarmManager alarmManager;
    ArrayList<LatLng> listCf = new ArrayList<LatLng>();
    List<Location> gpslocation = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    String latLangArray = "", jsonLatLangArray = "";
    ProgressDialog lwProgressDlg;

    AutoCompleteTextView tvLactatingWomenName, tvVDCName, tvWardNo, tvEthnicity, tvAge,tvsmName,tvVisitDate,
            tvVisitTime;

    String lactating_women_name, vdc_name, ward_no, ethnicity, age, pnc_visit,deliver_place,
            birth_attended_by,third_labour,oxytocin_received,neonates_asphysia,img,
            fourtyfivedays_iron,vitaminA,neonatal_records,breastfeed_in1hour,exclusive_breastfeeding, sm_name, visit_date, visit_time;

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
    String dataSentStatus;

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
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);

        // casting text view
        tvLactatingWomenName = (AutoCompleteTextView)findViewById(R.id.lactating_women_name);
        tvVDCName = (AutoCompleteTextView)findViewById(R.id.lactating_women_vdc_name);
        tvWardNo = (AutoCompleteTextView)findViewById(R.id.lactating_women_ward_no);
        tvEthnicity =(AutoCompleteTextView)findViewById(R.id.lactating_ethnicityName);
        tvAge = (AutoCompleteTextView)findViewById(R.id.lactating_women_age);
        tvsmName = (AutoCompleteTextView)findViewById(R.id.lactating_women_sm_name);

        setCurrentDateOnView();
        addListenerOnButton();
        setCurrentTimeOnView();
        addListenerOnTimeButton();


        // initialiting spinners
        pnc_visit_spinner = (Spinner) findViewById(R.id.pnc_visit_spinner);
        delivery_at_place_spinner = (Spinner)findViewById(R.id.delivery_at_place);
        birth_attended_by_lcw_spinner = (Spinner)findViewById(R.id.birth_attended_by_lcw);
        lactaing_women_3rd_labour_spinner = (Spinner)findViewById
                (R.id.lactating_women_3rd_labour);
        oxytocin_received_after_birth_spinner = (Spinner)findViewById
                (R.id.oxytocin_received_after_birth);
        lactating_women_neonates_asphysia_spinner = (Spinner)findViewById
                (R.id.lactating_women_neonates_asphysia);
        lactating_women_45days_iron_spinner = (Spinner)findViewById(R.id.lactating_women_45days_iron);
        lactating_women_vitaminA_spinner = (Spinner)findViewById(R.id.lactating_women_vitaminA);
        lactating_women_neonatal_records_spinner = (Spinner)findViewById
                (R.id.lactating_women_neonatal_records);
        lactating_women_breastfeed_in1hour_spinner = (Spinner)findViewById
                (R.id.lactating_women_breastfeed_in1hour);
        lactating_women_exclusive_breastfeeding_spinner = (Spinner)findViewById
                (R.id.lactating_women_exclusive_breastfeeding);

        // buttons
        send = (Button) findViewById(R.id.lactating_women_send);
        save = (Button) findViewById(R.id.lactating_women_save);

        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        // pnc visit adapters
        pnc_visit_adpt = new ArrayAdapter<String>(this,android.R.layout.activity_list_item, Constants.PNC_VISIT);
        pnc_visit_adpt.setDropDownViewResource(android.R.layout.simple_spinner_item);
        pnc_visit_spinner.setAdapter(pnc_visit_adpt);
        pnc_visit_spinner.setOnItemSelectedListener(this);

        // birth_attended_by_lcw of lactating women
        birth_attended_by_lcw_adpt = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Constants.ATTENDED_BIRTH_BY );
        birth_attended_by_lcw_adpt.setDropDownViewResource(android.R.layout.simple_spinner_item);
        birth_attended_by_lcw_spinner.setAdapter(birth_attended_by_lcw_adpt);
        birth_attended_by_lcw_spinner.setOnItemSelectedListener(this);

        // delivery place of a child by a lactating mother
        delivery_at_place_adpt = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Constants.BIRTH_PLACE);
        delivery_at_place_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delivery_at_place_spinner.setAdapter(delivery_at_place_adpt);
        delivery_at_place_spinner.setOnItemSelectedListener(this);

        // neonatal national records
        lactating_women_neonatal_records_adpt = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,Constants.NATIONAL_NEONATES);
        lactating_women_neonatal_records_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_neonatal_records_spinner.setAdapter(lactating_women_neonatal_records_adpt);
        lactating_women_neonatal_records_spinner.setOnItemSelectedListener(this);

        // adapter  receiving active management in 3rd labour by lactating women

        lactaing_women_3rd_labour_adpt = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactaing_women_3rd_labour_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactaing_women_3rd_labour_spinner.setAdapter(lactaing_women_3rd_labour_adpt);
        lactaing_women_3rd_labour_spinner.setOnItemSelectedListener(this);

        // received oxytocin after delivery for lactating women
        oxytocin_received_after_birth_adpt = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,Constants.YES_NO);
        oxytocin_received_after_birth_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        oxytocin_received_after_birth_spinner.setAdapter(oxytocin_received_after_birth_adpt);
        oxytocin_received_after_birth_spinner.setOnItemSelectedListener(this);

        // received neonates with birth asphysia
        lactating_women_neonates_asphysia_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_neonates_asphysia_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_neonates_asphysia_spinner.setAdapter(lactating_women_neonates_asphysia_adpt);
        lactating_women_neonates_asphysia_spinner.setOnItemSelectedListener(this);

        // receives 45 days of iron by lactating women
        lactating_women_45days_iron_adtp = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item, Constants.YES_NO);
        lactating_women_45days_iron_adtp.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_45days_iron_spinner.setAdapter(lactating_women_45days_iron_adtp);
        lactating_women_45days_iron_spinner.setOnItemSelectedListener(this);

        // vitamin A
        lactating_women_vitaminA_adpt =  new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_vitaminA_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_vitaminA_spinner.setAdapter(lactating_women_vitaminA_adpt);
        lactating_women_vitaminA_spinner.setOnItemSelectedListener(this);

        // breast feeding to infant by lactating mother with in an hour
        lactating_women_breastfeed_in1hour_adpt = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_breastfeed_in1hour_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_breastfeed_in1hour_spinner.setAdapter(lactating_women_breastfeed_in1hour_adpt);
        lactating_women_breastfeed_in1hour_spinner.setOnItemSelectedListener(this);

        // exclusive breast feeding bu lactating women
        lactating_women_exclusive_breastfeeding_adpt = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_exclusive_breastfeeding_adpt.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lactating_women_exclusive_breastfeeding_spinner.setAdapter(lactating_women_breastfeed_in1hour_adpt);
        lactating_women_exclusive_breastfeeding_spinner.setOnItemSelectedListener(this);

        // for picture click
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        startGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GPS_SETTINGS.equals(true)|| GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED){
                    if(gps.canGetLocation()){
                        lactatinggpslocation.add(gps.getLocation());
                        finalLat = gps.getLatitude();
                        finalLong = gps.getLongitude();
                        if (finalLat !=0){
                            try {
                                JSONObject data = new JSONObject();
                                data.put("lat",finalLat);
                                data.put("lon",finalLong);
                                jsonArrayGPS.put(data);
                            }

                            catch (JSONException e){
                                e.printStackTrace();

                            }

                            LatLng d = new LatLng(finalLat,finalLong);

                            lactatinglistCf.add(d);
                            isGpsTaken = true;
                            Toast.makeText(
                                    getApplicationContext(), "your location is - \nLat: " + finalLat
                                    + "\nLOng: " + finalLong, Toast.LENGTH_SHORT).show();
                            stringBuilder.append("["+ finalLat + ","+ finalLong +"]" +",");



                        }

                    }
                }
                else {
                    askForGPS();
                    gps = new GPS_TRACKER_FOR_POINT(LactatingWomenActivity.this);
                    Default_DIalog.showDefaultDialog(context, R.string.app_name, "Please try again, Gps not initialized");
                     }
            }
        });

        previewMAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValues.isFromSavedFrom){
                    StaticListOfCoordinates.setList(lactatinglistCf);
                    startActivity(new Intent(LactatingWomenActivity.this, MapPointActivity.class));
                }
                else {
                   if (GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED){
                       StaticListOfCoordinates.setList(lactatinglistCf);
                       startActivity(new Intent(LactatingWomenActivity.this,MapPointActivity.class));
                   }
                    else{
                       Default_DIalog.showDefaultDialog(context,R.string.app_name,"Please try again, Gps not initialized");
                   }
                }
            }
        });



        // add click listener to Button "POST"
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lactating_women_name = tvLactatingWomenName.getText().toString();
                vdc_name = tvVDCName.getText().toString();
                ward_no = tvWardNo.getText().toString();
                ethnicity = tvEthnicity.getText().toString();
                age = tvAge.getText().toString();
                img = encodedImage;
                sm_name = tvsmName.getText().toString();
                visit_date= tvVisitDate.getText().toString();
                visit_time = tvVisitTime.getText().toString();

                // check internet
                if(networkInfo != null && networkInfo.isConnected())
                {
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
                    showDialog.getWindow().setLayout((6 * width) / 7,LinearLayout.LayoutParams.WRAP_CONTENT);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            lwProgressDlg = new ProgressDialog(context);
                            lwProgressDlg.setMessage("Please wait .....");
                            lwProgressDlg.setIndeterminate(false);
                            lwProgressDlg.setCancelable(false);
                            lwProgressDlg.show();
                            // data goes here
                            convertDataToJson();


                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog.dismiss();
                                }
                            });



                        }
                    });
                }
                else
                {
                    final View coordinatorLayoutView = findViewById(R.id.activity_lactating_women);
                    Snackbar.make(coordinatorLayoutView, "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Retry", null).show();
                }
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int SpinnerID = parent.getId();
        if(SpinnerID == R.id.pnc_visit_spinner){
            switch (position){
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

        if(SpinnerID == R.id.delivery_at_place){
            switch (position) {
                case 0:
                    deliver_place = "Home";
                    break;
                case 1:
                    deliver_place = "Hospital";
                    break;
            }
        }

        if(SpinnerID == R.id.birth_attended_by_lcw){
            switch(position){
                case 0:
                    birth_attended_by = "SBA";
                    break;
                case 1:
                    birth_attended_by = "HW";
                    break;
                case 2:
                    birth_attended_by = "Others than SBA/HW";
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
            }
        }

        if(SpinnerID == R.id.lactating_women_neonates_asphysia){
            switch (position){
                case 0:
                    neonates_asphysia = "Yes";
                    break;
                case 1:
                    neonates_asphysia = "No";
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

        if(SpinnerID == R.id.lactating_women_neonatal_records){
            switch (position){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
            if(resultCode == Activity.RESULT_OK){
                Uri selectPic = data.getData();

                String filePath = getPath(selectPic);
                String file_extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                imagePath = filePath;
                addImage();



            }
        if (requestCode == CAMERA_PIC_REQUEST){
            if (requestCode == Activity.RESULT_OK){
                thumbnail = (Bitmap) data.getExtras().get("data");
                previewImageSite.setVisibility(View.VISIBLE);
                previewImageSite.setImageBitmap(thumbnail);
                saveToExternalStorage(thumbnail);
                addImage();
            }
        }
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

    private void saveToExternalStorage(Bitmap thumbnail) {
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



    // get url of image
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

    public void convertDataToJson(){
        try{
            JSONObject header = new JSONObject();

            header.put("tablename","recording_tool_for_lactating_woman");
            header.put("name_of_SM",sm_name);
            header.put("date",visit_date);
            header.put("name_of_lactating_woman",lactating_women_name);
            header.put("ward_no",ward_no);
            header.put("age",age);
            header.put("ethicity",ethnicity);
            header.put("delivery_at",deliver_place);
            header.put("irth_attended_by",birth_attended_by);
            header.put("recieved_active_management_of_third_stage_laour",third_labour);
            header.put("recieved_oxytocin_after_delivery",oxytocin_received);
            header.put("neonates_with_irth_asphyxia",neonates_asphysia);
            header.put("PNC_visit",pnc_visit);
            header.put("received_45_days_supply_of_iron",fourtyfivedays_iron);
            header.put("vit_A",vitaminA);
            header.put("neonatal_check_ups",neonatal_records);
            header.put("breast_feeding_within_one_hour_of_birth",breastfeed_in1hour);
            header.put("exclusive_breast_feeding",exclusive_breastfeeding);
            header.put("lat", finalLat);
            header.put("lon", finalLong);
            header.put("image", encodedImage);


            jsonToSend = header.toString();


        }
        catch(Exception e){
            e.printStackTrace();
        }

        sendDatToserver();

    }

    public void sendDatToserver() {

        if (jsonToSend.length() > 0) {

            LactatingWomenActivity.RestApii restApii = new LactatingWomenActivity.RestApii();
            restApii.execute();
        }
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
            mProgressDlg.dismiss();

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

                tvsmName.setText(sm_name);
                tvLactatingWomenName.setText(lactating_women_name);
                tvVDCName.setText(vdc_name);
                tvWardNo.setText(ward_no);
                tvEthnicity.setText(ethnicity);
                tvAge.setText(age);
                tvVisitDate.setText(visit_date);
                tvVisitTime.setText(visit_time);

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

        tvVisitDate.setOnClickListener(new View.OnClickListener() {

            //            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvVisitDate.setShowSoftInputOnFocus(false);
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



