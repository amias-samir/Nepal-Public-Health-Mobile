package np.com.naxa.nphf.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import np.com.naxa.nphf.R;
import np.com.naxa.nphf.activities.ChildrenUnderFive;
import np.com.naxa.nphf.activities.ChildrenUnderTwo;
import np.com.naxa.nphf.activities.LactatingWomenActivity;
import np.com.naxa.nphf.activities.PregnentWomenActivity;
import np.com.naxa.nphf.adapter.GridSpacingItemDecorator;
import np.com.naxa.nphf.adapter.RecyclerItemClickListener;
import np.com.naxa.nphf.adapter.Sent_Forms_Adapter;
import np.com.naxa.nphf.database.DataBaseConserVationTracking;
import np.com.naxa.nphf.dialog.Default_DIalog;
import np.com.naxa.nphf.model.SavedFormParameters;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Sent_Forms extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public static List<SavedFormParameters> resultCur = new ArrayList<>();
    Sent_Forms_Adapter ca;
    Context context = getActivity() ;

    public Fragment_Sent_Forms() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment__sent__form_list, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.NewsList);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecorator(1, 5, true));
        createList();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                alert_editlist(position);

            }

            @Override
            public void onItemLongClick(View view, int position) {


            }
        }));

        return rootview;
    }

    //-------------------------------Method Dialog Box List for << REPORT DETAIL, SEND and DELETE >>-----------------------------------//
    protected void alert_editlist(final int position) {

        // TODO Auto-generated method stub
        final CharSequence[] items = {"Open", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Action");

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (items[item] == "Open") {
                    String id = resultCur.get(position).formId;
                    String jSon = resultCur.get(position).jSON;
                    String photo = resultCur.get(position).photo;
                    String gps = resultCur.get(position).gps;
                    loadForm(id, jSon, photo, gps);

                } else if (items[item] == "Delete") {
                    DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

                    final Dialog showDialog = new Dialog(getActivity());
                    showDialog.setContentView(R.layout.delete_dialog);
                    TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
                    Button btnOk = (Button) showDialog.findViewById(R.id.button_delete);
                    Button cancle = (Button) showDialog.findViewById(R.id.button_cancle);
                    showDialog.setTitle("Are You Sure ??");
                    tvDisplay.setText("Are you sure you want to delete the data ??");
                    showDialog.setCancelable(true);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    btnOk.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub


                            DataBaseConserVationTracking dataBaseConserVationTracking = new DataBaseConserVationTracking(getActivity());
                            dataBaseConserVationTracking.open();
                            int id = (int) dataBaseConserVationTracking.updateTable_DeleteFlag(resultCur.get(position).dbId);
//                Toast.makeText(getActivity() ,resultCur.get(position).date+ " Long Clicked "+id , Toast.LENGTH_SHORT ).show();
                            dataBaseConserVationTracking.close();
                            showDialog.dismiss();
                            createList();
                        }
                    });
                    cancle.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showDialog.dismiss();
                        }
                    });

                }
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDeleteDialog(final int position) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(getActivity());
        showDialog.setContentView(R.layout.delete_dialog);
        TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
        Button btnOk = (Button) showDialog.findViewById(R.id.button_delete);
        Button cancle = (Button) showDialog.findViewById(R.id.button_cancle);
        showDialog.setCancelable(true);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                DataBaseConserVationTracking dataBaseConserVationTracking = new DataBaseConserVationTracking(getActivity());
                dataBaseConserVationTracking.open();
                int id = (int) dataBaseConserVationTracking.updateTable_DeleteFlag(resultCur.get(position).dbId);
//                Toast.makeText(getActivity() ,resultCur.get(position).date+ " Long Clicked "+id , Toast.LENGTH_SHORT ).show();
                dataBaseConserVationTracking.close();
                showDialog.dismiss();
                createList();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog.dismiss();
            }
        });

    }

    public void loadForm(String formId, String jsonData , String photo , String gps){
        switch (formId){
            case "1" :
                Intent intent1 = new Intent(getActivity(), PregnentWomenActivity.class);
                intent1.putExtra("JSON1", jsonData);
                intent1.putExtra("photo" , photo);
                intent1.putExtra("gps" , gps) ;
                startActivity(intent1);
                break;

            case "2" :
                Intent intent2 = new Intent(getActivity(), LactatingWomenActivity.class);
                intent2.putExtra("JSON1", jsonData);
                intent2.putExtra("photo" , photo);
                intent2.putExtra("gps" , gps) ;
                startActivity(intent2);
                break;

            case "3" :
                Intent intent3 = new Intent(getActivity(), ChildrenUnderTwo.class);
                intent3.putExtra("JSON1", jsonData);
                intent3.putExtra("photo" , photo);
                intent3.putExtra("gps" , gps) ;
                startActivity(intent3);
                break;

            case "4" :
                Intent intent4 = new Intent(getActivity(), ChildrenUnderFive.class);
                intent4.putExtra("JSON1", jsonData);
                intent4.putExtra("photo" , photo);
                intent4.putExtra("gps" , gps) ;
                startActivity(intent4);
                break;

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void createList() {
        resultCur.clear();
//        Single_String_Title newData1 = new Single_String_Title();
//        newData1.title = "CF Detail";
//        resultCur.add(newData1);
        DataBaseConserVationTracking dataBaseConserVationTracking = new DataBaseConserVationTracking(getActivity());
        dataBaseConserVationTracking.open();
        boolean isTableEmpty = dataBaseConserVationTracking.is_TABLE_MAIN_Empty();
        if(isTableEmpty){
            Default_DIalog.showDefaultDialog(getActivity() , R.string.app_name , "No data Saved ");
        }else{
            int count = dataBaseConserVationTracking.returnTotalNoOf_TABLE_MAIN_NUM();
            for(int i=count ; i>=1 ; i--) {
//                String[] data = dataBaseConserVationTracking.return_Data_TABLE_MAIN(i);
                String[] data = dataBaseConserVationTracking.return_Data_ID(i);
                SavedFormParameters savedData = new SavedFormParameters();
                Log.e("DATA" , "08 "+data[8] +" one: "+ data[1]+" two: "+data[2]);
//                savedData.dbId = data[0];
                savedData.formId = data[0];
                savedData.formName = data[1];
                savedData.date = data[2];
                savedData.jSON = data[3];
                savedData.gps = data[4] ;
                savedData.photo = data[5];
                savedData.status = data[6];
                savedData.deletedStatus = data[7];
                savedData.dbId = data[8];

                if(data[7].equals("0")) {

                    resultCur.add(savedData);
                }

            }
        }
        fillTable();
    }

    public void fillTable() {
        Log.e("FILLTABLE", "INSIDE FILL TABLE");
        ca = new Sent_Forms_Adapter(resultCur);
        recyclerView.setAdapter(ca);
        Log.e("FILLTABLE", "AFTER FILL TABLE");
//        CheckValues.setValue();
    }

}


