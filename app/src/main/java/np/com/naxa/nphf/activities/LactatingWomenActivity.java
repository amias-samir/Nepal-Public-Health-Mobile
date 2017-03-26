package np.com.naxa.nphf.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.SyncStateContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.gps.GPS_TRACKER_FOR_POINT;
import np.com.naxa.nphf.model.Constants;


public class LactatingWomenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener   {

    private static final String TAG = "LactatingWomenActivity";
    public static Toolbar toolbar;
    Spinner pnc_visit_spinner,delivery_at_place_spinner, birth_attended_by_lcw_spinner, lactaing_women_3rd_labour_spinner, oxytocin_received_after_birth_spinner,
            lactating_women_neonates_asphysia_spinner,lactating_women_45days_iron_spinner, lactating_women_vitaminA_spinner,lactating_women_neonatal_records_spinner,
            lactating_women_breastfeed_in1hour_spinner,lactating_women_exclusive_breastfeeding_spinner;

    ArrayAdapter pnc_visit_adpt,delivery_at_place_adpt,birth_attended_by_lcw_adpt,lactaing_women_3rd_labour_adpt,oxytocin_received_after_birth_adpt,
            lactating_women_neonates_asphysia_adpt, lactating_women_45days_iron_adtp, lactating_women_vitaminA_adpt,
            lactating_women_neonatal_records_adpt,lactating_women_breastfeed_in1hour_adpt,lactating_women_exclusive_breastfeeding_adpt;

    Button send, save;
    ProgressDialog mProgressDlg;
    Context context = this;
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

    AutoCompleteTextView tvLactatingWomenName, tvVDCName, tvWardNo, tvEthnicity, tvAge;

    String lactating_women_name, vdc_name, ward_no, ethnicity, age, pnc_visit;

    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;
    String dataSentStatus;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lactating_women);

        toolbar = (Toolbar) findViewById(R.id.lactating_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recording tool for Lactating women");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // casting text view
        tvLactatingWomenName = (AutoCompleteTextView)findViewById(R.id.lactating_women_name);
        tvVDCName = (AutoCompleteTextView)findViewById(R.id.lactating_women_vdc_name);
        tvWardNo = (AutoCompleteTextView)findViewById(R.id.lactating_women_ward_no);
        tvEthnicity =(AutoCompleteTextView)findViewById(R.id.lactating_ethnicityName);
        tvAge = (AutoCompleteTextView)findViewById(R.id.lactating_women_age);


        // spinners
        pnc_visit_spinner = (Spinner) findViewById(R.id.pnc_visit_spinner);
        delivery_at_place_spinner = (Spinner)findViewById(R.id.delivery_at_place);
        birth_attended_by_lcw_spinner = (Spinner)findViewById(R.id.birth_attended_by_lcw);
        lactaing_women_3rd_labour_spinner = (Spinner)findViewById(R.id.lactating_women_3rd_labour);
        oxytocin_received_after_birth_spinner = (Spinner)findViewById(R.id.oxytocin_received_after_birth);
        lactating_women_neonates_asphysia_spinner = (Spinner)findViewById(R.id.lactating_women_neonates_asphysia);
        lactating_women_45days_iron_spinner = (Spinner)findViewById(R.id.lactating_women_45days_iron);
        lactating_women_vitaminA_spinner = (Spinner)findViewById(R.id.lactating_women_vitaminA);
        lactating_women_neonatal_records_spinner = (Spinner)findViewById(R.id.lactating_women_neonatal_records);
        lactating_women_breastfeed_in1hour_spinner = (Spinner)findViewById(R.id.lactating_women_breastfeed_in1hour);
        lactating_women_exclusive_breastfeeding_spinner = (Spinner)findViewById(R.id.lactating_women_exclusive_breastfeeding);

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

        lactating_women_neonatal_records_adpt = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Constants.NATIONAL_NEONATES);
        lactating_women_neonatal_records_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactating_women_neonatal_records_spinner.setAdapter(lactating_women_neonatal_records_adpt);
        lactating_women_neonatal_records_spinner.setOnItemSelectedListener(this);

        // adapter  receiving active management in 3rd labour by lactating women

        lactaing_women_3rd_labour_adpt = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactaing_women_3rd_labour_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactaing_women_3rd_labour_spinner.setAdapter(lactaing_women_3rd_labour_adpt);
        lactaing_women_3rd_labour_spinner.setOnItemSelectedListener(this);

        // received oxytocin after delivery for lactating women
        oxytocin_received_after_birth_adpt = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Constants.YES_NO);
        oxytocin_received_after_birth_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        oxytocin_received_after_birth_spinner.setAdapter(oxytocin_received_after_birth_adpt);
        oxytocin_received_after_birth_spinner.setOnItemSelectedListener(this);

        // received neonates with birth asphysia
        lactating_women_neonates_asphysia_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_neonates_asphysia_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactating_women_neonates_asphysia_spinner.setAdapter(lactating_women_neonates_asphysia_adpt);
        lactating_women_neonates_asphysia_spinner.setOnItemSelectedListener(this);

        // receives 45 days of iron by lactating women
        lactating_women_45days_iron_adtp = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, Constants.YES_NO);
        lactating_women_45days_iron_adtp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactating_women_45days_iron_spinner.setAdapter(lactating_women_45days_iron_adtp);
        lactating_women_45days_iron_spinner.setOnItemSelectedListener(this);

        // vitamin A
        lactating_women_vitaminA_adpt =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_vitaminA_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactating_women_vitaminA_spinner.setAdapter(lactating_women_vitaminA_adpt);
        lactating_women_vitaminA_spinner.setOnItemSelectedListener(this);

        // breast feeding to infant by lactating mother with in an hour
        lactating_women_breastfeed_in1hour_adpt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_breastfeed_in1hour_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactating_women_breastfeed_in1hour_spinner.setAdapter(lactating_women_breastfeed_in1hour_adpt);
        lactating_women_breastfeed_in1hour_spinner.setOnItemSelectedListener(this);

        // exclusive breast feeding bu lactating women
        lactating_women_exclusive_breastfeeding_adpt = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Constants.YES_NO);
        lactating_women_exclusive_breastfeeding_adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lactating_women_exclusive_breastfeeding_spinner.setAdapter(lactating_women_breastfeed_in1hour_adpt);
        lactating_women_exclusive_breastfeeding_spinner.setOnItemSelectedListener(this);


        // add click listener to Button "POST"
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lactating_women_name = tvLactatingWomenName.getText().toString();
                vdc_name = tvVDCName.getText().toString();
                ward_no = tvWardNo.getText().toString();
                ethnicity = tvEthnicity.getText().toString();
                age = tvAge.getText().toString();
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
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

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
                           // convertDataToJson();


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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}



