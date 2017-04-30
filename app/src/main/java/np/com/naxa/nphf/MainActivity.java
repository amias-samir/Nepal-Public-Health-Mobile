package np.com.naxa.nphf;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import np.com.naxa.nphf.activities.ChildrenUnderFive;
import np.com.naxa.nphf.activities.ChildrenUnderTwo;
import np.com.naxa.nphf.activities.HouseholdVisitActivity;
import np.com.naxa.nphf.activities.LactatingWomenActivity;
import np.com.naxa.nphf.activities.MonthlyMGMeetingActivity;
import np.com.naxa.nphf.activities.PeerGroupActivity;
import np.com.naxa.nphf.activities.PregnentWomenActivity;
import np.com.naxa.nphf.activities.SavedFormsActivity;
import np.com.naxa.nphf.activities.SuccessStoryActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = "MainActivity";

    Toolbar toolbar ;
    CardView cvPregnentWomen, cvLocatingWomen, cvChildUnderTwo, cvChildUnderFive, cvHouseholdVisit;
    RelativeLayout rlPregnentWomen, rlLocatingWomen, rlChildunderTwo, rlChildUnderFive, rlSavedForms, rlHouseholdVisit, rlPeerGroup,
            rlMonthlyMeeting, rlSuccessStory;

    //Permission for higher then lollipop devices
    private int MULTIPLE_PERMISSION_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NPHF");

        cvPregnentWomen = (CardView) findViewById(R.id.card_view_pregnent_women);
        cvLocatingWomen = (CardView) findViewById(R.id.card_view_locating);
        cvChildUnderTwo = (CardView) findViewById(R.id.card_view__under_2yrs);
        cvChildUnderFive = (CardView) findViewById(R.id.card_view_under_5yrs);

        rlPregnentWomen = (RelativeLayout) findViewById(R.id.top_layout_pregnent_women);
        rlLocatingWomen = (RelativeLayout) findViewById(R.id.top_layout_locating);
        rlChildunderTwo = (RelativeLayout) findViewById(R.id.top_layout__under_2yrs);
        rlChildUnderFive = (RelativeLayout) findViewById(R.id.top_layout_under_5yrs);
        rlSavedForms = (RelativeLayout) findViewById(R.id.top_layout_saved_forms);
        rlHouseholdVisit = (RelativeLayout) findViewById(R.id.top_layout_household_visit);
        rlPeerGroup = (RelativeLayout) findViewById(R.id.top_layout_peer_group);
        rlMonthlyMeeting = (RelativeLayout) findViewById(R.id.top_layout_monthly_meeting);
        rlSuccessStory = (RelativeLayout) findViewById(R.id.top_layout_success_story);

        cvPregnentWomen.setOnClickListener( this);
        cvPregnentWomen.setOnClickListener(this);
        cvChildUnderTwo.setOnClickListener(this);
        cvChildUnderFive.setOnClickListener(this);
        rlPregnentWomen.setOnClickListener(this);
        rlLocatingWomen.setOnClickListener(this);
        rlChildUnderFive.setOnClickListener(this);
        rlChildunderTwo.setOnClickListener(this);
        rlSavedForms.setOnClickListener(this);
        rlHouseholdVisit.setOnClickListener(this);
        rlPeerGroup.setOnClickListener(this);
        rlMonthlyMeeting.setOnClickListener(this);
        rlSuccessStory.setOnClickListener(this);


        try {
            //First checking if the app is already having the permission
            if (isPermissionAllowed()) {
                //If permission is already having then showing the toast
//                Toast.makeText(AddYourBusinessActivity.this, "You already have the permission", Toast.LENGTH_LONG).show();
                //Existing the method with return
                return;
            } else {
                //If the app has not the permission then asking for the permission
                requestMultiplePermission();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.getMessage();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.top_layout_pregnent_women):
                Log.e(TAG, "onClick: "+ "card_view_pregnent_women" );
                Intent intent = new Intent(MainActivity.this, PregnentWomenActivity.class);
                startActivity(intent);
                break;

            case (R.id.top_layout_locating):
                Intent intent1 = new Intent(MainActivity.this, LactatingWomenActivity.class);
                startActivity(intent1);

                break;
            case  (R.id.top_layout__under_2yrs):
                Intent intent2 = new Intent(MainActivity.this, ChildrenUnderTwo.class);
                startActivity(intent2);
                break;

            case  (R.id.top_layout_under_5yrs):
                Intent intent3 = new Intent(MainActivity.this, ChildrenUnderFive.class);
                startActivity(intent3);

                break;

            case  (R.id.top_layout_household_visit):
                Intent intent4 = new Intent(MainActivity.this, HouseholdVisitActivity.class);
                startActivity(intent4);

                break;

            case  (R.id.top_layout_saved_forms):
                Intent intent5 = new Intent(MainActivity.this, SavedFormsActivity.class);
                startActivity(intent5);

                break;

            case  (R.id.top_layout_peer_group):
                Intent intent6 = new Intent(MainActivity.this, PeerGroupActivity.class);
                startActivity(intent6);

                break;

            case  (R.id.top_layout_monthly_meeting):
                Intent intent7 = new Intent(MainActivity.this, MonthlyMGMeetingActivity.class);
                startActivity(intent7);

                break;

            case  (R.id.top_layout_success_story):
                Intent intent8 = new Intent(MainActivity.this, SuccessStoryActivity.class);
                startActivity(intent8);

                break;
        }

    }

    /**
     *
     * @return Susan Permissions: Camera, Storage, Location, Internet, etc.
     */
    //We are calling this method to check the permission status
    private boolean isPermissionAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }


    //Requesting permission
    private void requestMultiplePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, MULTIPLE_PERMISSION_CODE);

    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == MULTIPLE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Exit From App")
                .setMessage("Are you sure you want to Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.onBackPressed();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        } else {
                            finish();
                        }
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }
}
