package np.com.naxa.nphf.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class SuccessStoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SuccessStoryActivity";
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
    String formid, formNameSavedForm = "";
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

    Spinner spinnerVDCName, spinnerWardNo ;
    ArrayAdapter vdcNameadpt, wardNoadpt ;
    //tvNameOfTool, name_of_tool,
    AutoCompleteTextView tvNameOfRespondaents, tvTopics, tvProblems, tvInterventionOfSM, tvResultOfInput;
    CheckBox cbANC, cbPNC, cbInstitunationalDelivery, cbNewBornCare, cbBreastFeeding, cbComplementryFeeding, cbHygieneRelated, cbMotherGroupRelated,
            cbRefer, cbSexualAndRepreductive, cbPeerGroup;
    CardView cv_Send_Save;
    String vdc_name,ward_no, name_of_respondents, topics, anc, pnc, institunationl_delivery, new_born_care, breast_feeding, complementry_feeding,
            hygiene_related, mother_group_related, refer, sexual_and_reproductive, peer_group, img,
            problems, intervention_by_sm, result_of_input;
    JSONArray jsonArrayGPS = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_story);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Success Story");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        tvNameOfTool = (AutoCompleteTextView) findViewById(R.id.success_story_tool_name);
        tvNameOfRespondaents = (AutoCompleteTextView) findViewById(R.id.success_story_respondents_name);
        tvTopics = (AutoCompleteTextView) findViewById(R.id.success_story_story);
        tvProblems = (AutoCompleteTextView) findViewById(R.id.success_story_problems);
        tvInterventionOfSM = (AutoCompleteTextView) findViewById(R.id.success_story_intervention_by_sm);
        tvResultOfInput = (AutoCompleteTextView) findViewById(R.id.success_story_result_of_input);

        startGps = (Button) findViewById(R.id.success_story_GpsStart);
        previewMap = (Button) findViewById(R.id.success_story_preview_map);
        previewMap.setEnabled(false);
        send = (Button) findViewById(R.id.success_story_send);
        save = (Button) findViewById(R.id.success_story_save);

        photo = (ImageButton) findViewById(R.id.success_story_photo_site);
        previewImageSite = (ImageView) findViewById(R.id.success_story_PhotographSiteimageViewPreview);
        previewImageSite.setVisibility(View.GONE);

        cv_Send_Save = (CardView) findViewById(R.id.cv_SaveSend);
        spinnerVDCName = (Spinner) findViewById(R.id.success_story_vdc_name);
        spinnerWardNo = (Spinner) findViewById(R.id.success_story_ward_no);

        cbANC = (CheckBox) findViewById(R.id.success_story_anc);
        cbPNC = (CheckBox) findViewById(R.id.success_story_pnc);
        cbInstitunationalDelivery = (CheckBox) findViewById(R.id.success_story_institutional_delivery);
        cbNewBornCare = (CheckBox) findViewById(R.id.success_story_new_born_care);
        cbBreastFeeding = (CheckBox) findViewById(R.id.success_story_breast_feeding);
        cbComplementryFeeding = (CheckBox) findViewById(R.id.success_story_complementry_feeding);
        cbHygieneRelated = (CheckBox) findViewById(R.id.success_story_hygiene_related);
        cbMotherGroupRelated = (CheckBox) findViewById(R.id.success_story_mother_group_related);
        cbRefer = (CheckBox) findViewById(R.id.success_story_refer);
        cbSexualAndRepreductive = (CheckBox) findViewById(R.id.success_story_sexual_and_reproductive);
        cbPeerGroup = (CheckBox) findViewById(R.id.success_story_peer_group);

        cv_Send_Save = (CardView) findViewById(R.id.cv_SaveSend);

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

        initilizeUI();

        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();


        /**₧
         * Author Samir
         */
        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

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
                    gps = new GPS_TRACKER_FOR_POINT(SuccessStoryActivity.this);
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
                    startActivity(new Intent(SuccessStoryActivity.this, MapPointActivity.class));
                } else {

                    if (GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {
                        StaticListOfCoordinates.setList(listCf);
                        startActivity(new Intent(SuccessStoryActivity.this, MapPointActivity.class));
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

//                        name_of_tool = tvNameOfTool.getText().toString();
                        name_of_respondents = tvNameOfRespondaents.getText().toString();
                        problems = tvProblems.getText().toString();
                        intervention_by_sm = tvInterventionOfSM.getText().toString();
                        result_of_input = tvResultOfInput.getText().toString();
                        topics = tvTopics.getText().toString();
                        img = encodedImage;
                        jsonLatLangArray = jsonArrayGPS.toString();
//===============================Diarrhoea details =====================================//
                        if (cbANC.isChecked() == true) {
                            Log.e("cbANC", " ");
                            anc = "yes";
                        } else {
                            anc = "no";

                        }
                        if (cbPNC.isChecked() == true) {
                            pnc = "yes";
                        } else {
                            pnc = "no";

                        }
                        if (cbInstitunationalDelivery.isChecked() == true) {
                            institunationl_delivery = "yes";
                        } else {
                            institunationl_delivery = "no";
                        }
                        //==================================ARI details ========================================== //
                        if (cbNewBornCare.isChecked() == true) {
                            Log.e("cbSufferedARI", " ");
                            new_born_care = "yes";
                        } else {
                            new_born_care = "no";

                        }
                        if (cbBreastFeeding.isChecked() == true) {
                            breast_feeding = "yes";
                        } else {
                            breast_feeding = "no";

                        }
                        if (cbComplementryFeeding.isChecked() == true) {
                            complementry_feeding = "yes";
                        } else {
                            complementry_feeding = "no";
                        }

                        if (cbHygieneRelated.isChecked() == true) {
                            hygiene_related = "yes";
                        } else {
                            hygiene_related = "no";
                        }

                        if (cbMotherGroupRelated.isChecked() == true) {
                            mother_group_related = "yes";
                        } else {
                            mother_group_related = "no";
                        }

                        if (cbRefer.isChecked() == true) {
                            refer = "yes";
                        } else {
                            refer = "no";
                        }

                        if (cbSexualAndRepreductive.isChecked() == true) {
                            sexual_and_reproductive = "yes";
                        } else {
                            sexual_and_reproductive = "no";
                        }

                        if (cbPeerGroup.isChecked() == true) {
                            peer_group = "yes";
                        } else {
                            peer_group = "no";
                        }
                        //========================================================================================//


                        convertDataToJson();

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        final int width = metrics.widthPixels;
                        int height = metrics.heightPixels;

                        final Dialog showDialog = new Dialog(context);
                        showDialog.setContentView(R.layout.date_input_layout);
                        final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                        final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);

                        if (formNameSavedForm.equals("")) {
                            FormNameToInput.setText("Success Story");
                        } else {
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
                                    String[] data = new String[]{"8", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + imageName, "Not Sent", "0"};

                                    DataBaseNepalPublicHealth_NotSent dataBaseNepalPublicHealthNotSent = new DataBaseNepalPublicHealth_NotSent(context);
                                    dataBaseNepalPublicHealthNotSent.open();
                                    dataBaseNepalPublicHealthNotSent.insertIntoTable_Main(data);


                                    Toast.makeText(SuccessStoryActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
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
                                            Intent intent = new Intent(SuccessStoryActivity.this, SavedFormsActivity.class);
                                            startActivity(intent);
//                                finish();
                                        }
                                    });

                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog.dismiss();
                                            Intent intent = new Intent(SuccessStoryActivity.this, MainActivity.class);
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

                if (isGpsTaken) {
//                    name_of_tool = tvNameOfTool.getText().toString();
                    name_of_respondents = tvNameOfRespondaents.getText().toString();
                    problems = tvProblems.getText().toString();
                    intervention_by_sm = tvInterventionOfSM.getText().toString();
                    result_of_input = tvResultOfInput.getText().toString();
                    topics = tvTopics.getText().toString();
                    img = encodedImage;
                    jsonLatLangArray = jsonArrayGPS.toString();
//===============================Diarrhoea details =====================================//
                    if (cbANC.isChecked() == true) {
                        Log.e("cbANC", " ");
                        anc = "yes";
                    } else {
                        anc = "no";

                    }
                    if (cbPNC.isChecked() == true) {
                        pnc = "yes";
                    } else {
                        pnc = "no";

                    }
                    if (cbInstitunationalDelivery.isChecked() == true) {
                        institunationl_delivery = "yes";
                    } else {
                        institunationl_delivery = "no";
                    }
                    //==================================ARI details ========================================== //
                    if (cbNewBornCare.isChecked() == true) {
                        Log.e("cbSufferedARI", " ");
                        new_born_care = "yes";
                    } else {
                        new_born_care = "no";

                    }
                    if (cbBreastFeeding.isChecked() == true) {
                        breast_feeding = "yes";
                    } else {
                        breast_feeding = "no";

                    }
                    if (cbComplementryFeeding.isChecked() == true) {
                        complementry_feeding = "yes";
                    } else {
                        complementry_feeding = "no";
                    }

                    if (cbHygieneRelated.isChecked() == true) {
                        hygiene_related = "yes";
                    } else {
                        hygiene_related = "no";
                    }

                    if (cbMotherGroupRelated.isChecked() == true) {
                        mother_group_related = "yes";
                    } else {
                        mother_group_related = "no";
                    }

                    if (cbRefer.isChecked() == true) {
                        refer = "yes";
                    } else {
                        refer = "no";
                    }

                    if (cbSexualAndRepreductive.isChecked() == true) {
                        sexual_and_reproductive = "yes";
                    } else {
                        sexual_and_reproductive = "no";
                    }

                    if (cbPeerGroup.isChecked() == true) {
                        peer_group = "yes";
                    } else {
                        peer_group = "no";
                    }
                    //========================================================================================//


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
                        final View coordinatorLayoutView = findViewById(R.id.activity_success_story);
                        Snackbar.make(coordinatorLayoutView, "No internet connection", Snackbar.LENGTH_LONG)
                                .setAction("Retry", null).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();

                }


            }


        });

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SuccessStoryActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SuccessStoryActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(SuccessStoryActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(SuccessStoryActivity.this, new String[]{permission}, requestCode);
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
                            status.startResolutionForResult(SuccessStoryActivity.this, GPS_SETTINGS);
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
            if (ActivityCompat.checkSelfPermission(SuccessStoryActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForGPS();
                Log.v("Susan", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(SuccessStoryActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
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

        imageName = "Success_Story" + timeInMillis;

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
            Log.d(TAG, "initilizeUI: " + sent_Status);

            if (sent_Status.equals("Sent")) {
                spinnerVDCName.setEnabled(false);
                    spinnerWardNo.setEnabled(false);
                tvNameOfRespondaents.setEnabled(false);
                tvTopics.setEnabled(false);
                tvProblems.setEnabled(false);
                tvInterventionOfSM.setEnabled(false);
                tvResultOfInput.setEnabled(false);
                cbANC.setEnabled(false);
                cbPNC.setEnabled(false);
                cbInstitunationalDelivery.setEnabled(false);
                cbNewBornCare.setEnabled(false);
                cbBreastFeeding.setEnabled(false);
                cbComplementryFeeding.setEnabled(false);
                cbHygieneRelated.setEnabled(false);
                cbMotherGroupRelated.setEnabled(false);
                cbRefer.setEnabled(false);
                cbSexualAndRepreductive.setEnabled(false);
                cbPeerGroup.setEnabled(false);
                photo.setEnabled(false);
                startGps.setEnabled(false);
                cv_Send_Save.setVisibility(View.GONE);
            }

            Log.e("SuccessStory", "i-" + imageName);

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
                Log.e("SuccessStory", "" + jsonToParse);
                parseJson(jsonToParse);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            gps = new GPS_TRACKER_FOR_POINT(SuccessStoryActivity.this);
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
            header.put("tablename", "recording_tool_for_success_stories");
//            header.put("name_of_tool", name_of_tool);
            header.put("name_of_VDC", vdc_name);
            header.put("ward_no", ward_no);
            header.put("name_of_respondents", name_of_respondents);
            header.put("problems", problems);
            header.put("intervention_by_sm", intervention_by_sm);
            header.put("result_of_input", result_of_input);
            header.put("anc", anc);
            header.put("pnc", pnc);
            header.put("institunationl_delivery", institunationl_delivery);
            header.put("new_born_care", new_born_care);
            header.put("breast_feeding", breast_feeding);
            header.put("complementry_feeding", complementry_feeding);
            header.put("hygiene_related", hygiene_related);
            header.put("mother_group_related", mother_group_related);
            header.put("refer", refer);
            header.put("sexual_and_reproductive", sexual_and_reproductive);
            header.put("peer_group", peer_group);
            header.put("story", topics);

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
        Log.e("SuccessStory", "json parse : " + jsonObj.toString());

        finalLat = Double.parseDouble(jsonObj.getString("lat"));
        finalLong = Double.parseDouble(jsonObj.getString("lon"));
        LatLng d = new LatLng(finalLat, finalLong);
        listCf.add(d);


        vdc_name = jsonObj.getString("name_of_VDC");
        ward_no = jsonObj.getString("ward_no");
        name_of_respondents = jsonObj.getString("name_of_respondents");
        topics = jsonObj.getString("story");
        problems = jsonObj.getString("problems");
        intervention_by_sm = jsonObj.getString("intervention_by_sm");
        result_of_input = jsonObj.getString("result_of_input");

        Log.e(TAG, "SuccessStory: " + " SAMIR  " + vdc_name + "----location----" + finalLat + " , " + finalLong);


        anc = jsonObj.getString("anc");
        pnc = jsonObj.getString("pnc");
        institunationl_delivery = jsonObj.getString("institunationl_delivery");
        new_born_care = jsonObj.getString("new_born_care");
        breast_feeding = jsonObj.getString("breast_feeding");
        complementry_feeding = jsonObj.getString("complementry_feeding");
        hygiene_related = jsonObj.getString("hygiene_related");
        mother_group_related = jsonObj.getString("mother_group_related");
        refer = jsonObj.getString("refer");
        sexual_and_reproductive = jsonObj.getString("sexual_and_reproductive");
        peer_group = jsonObj.getString("peer_group");


//        tvNameOfTool.setText(name_of_tool);
        tvNameOfRespondaents.setText(name_of_respondents);
        tvTopics.setText(topics);
        tvProblems.setText(problems);
        tvInterventionOfSM.setText(intervention_by_sm);
        tvResultOfInput.setText(result_of_input);

        if (anc.equals("yes")) {
            cbANC.setChecked(true);
        }
        if (pnc.equals("yes")) {
            cbPNC.setChecked(true);
        }
        if (institunationl_delivery.equals("yes")) {
            cbInstitunationalDelivery.setChecked(true);
        }

        if (new_born_care.equals("yes")) {
            cbNewBornCare.setChecked(true);
        }
        if (breast_feeding.equals("yes")) {
            cbBreastFeeding.setChecked(true);
        }
        if (complementry_feeding.equals("yes")) {
            cbComplementryFeeding.setChecked(true);
        }
        if (hygiene_related.equals("yes")) {
            cbHygieneRelated.setChecked(true);
        }
        if (mother_group_related.equals("yes")) {
            cbMotherGroupRelated.setChecked(true);
        }
        if (refer.equals("yes")) {
            cbRefer.setChecked(true);
        }
        if (sexual_and_reproductive.equals("yes")) {
            cbSexualAndRepreductive.setChecked(true);
        }
        if (peer_group.equals("yes")) {
            cbPeerGroup.setChecked(true);
        }

        int setVDCName = vdcNameadpt.getPosition(vdc_name);
        spinnerVDCName.setSelection(setVDCName);

        int setWardNo = wardNoadpt.getPosition(ward_no);
        spinnerWardNo.setSelection(setWardNo);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinnerId = parent.getId();

        if(spinnerId == R.id.success_story_vdc_name){
            vdc_name = Constants.VDC_NAME[position];
            Log.e(TAG, "onItemSelected: "+vdc_name );

        }

        if(spinnerId == R.id.success_story_ward_no){
            ward_no = Constants.VDC_WARD_NO[position];
            Log.e(TAG, "onItemSelected: "+ward_no );

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

//                tvNameOfTool.setText(name_of_tool);
                tvNameOfRespondaents.setText(name_of_respondents);
                tvTopics.setText(topics);
                tvProblems.setText(problems);
                tvInterventionOfSM.setText(intervention_by_sm);
                tvResultOfInput.setText(result_of_input);
                previewImageSite.setImageBitmap(thumbnail);

                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                dateString = sdf.format(date);
//                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                        .setTitleText("")
//                        .setContentText("Data sent successfully!")
//                        .show();
                String[] data = new String[]{"8", "Success Story", dateString, jsonToSend, jsonLatLangArray,
                        "" + imageName, "Sent", "0"};

                DataBaseNepalPublicHealth_Sent dataBaseNepalPublicHealthSent = new DataBaseNepalPublicHealth_Sent(context);
                dataBaseNepalPublicHealthSent.open();
                long id = dataBaseNepalPublicHealthSent.insertIntoTable_Main(data);
                Log.e("dbID", "" + id);
                dataBaseNepalPublicHealthSent.close();

                if (CheckValues.isFromSavedFrom) {
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
                            Intent intent = new Intent(SuccessStoryActivity.this, SuccessStoryActivity.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(SuccessStoryActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                if (!CheckValues.isFromSavedFrom) {
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
                            Intent intent = new Intent(SuccessStoryActivity.this, SuccessStoryActivity.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(SuccessStoryActivity.this, MainActivity.class);
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
