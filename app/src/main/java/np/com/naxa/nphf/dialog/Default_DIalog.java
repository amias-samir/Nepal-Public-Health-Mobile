package np.com.naxa.nphf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import np.com.naxa.nphf.R;


/**
 * Created by user1 on 9/1/2015.
 */
public class Default_DIalog {

    public static void showDefaultDialog(Context context, int title, String displayText) {


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(context);
        showDialog.setContentView(R.layout.default_dialog);
        TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
        Button btnOk = (Button) showDialog.findViewById(R.id.button_defaultDialog);
        showDialog.setTitle(title);
        tvDisplay.setText(displayText);
        showDialog.setCancelable(true);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog.dismiss();
            }
        });
    }





//    public static void showDateDialog(final Context context) {
//
//        final String[] dateOfData = new String[1];
//        final String[] formName = new String[1];
//        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;
//
//        final Dialog showDialog = new Dialog(context);
//        showDialog.setContentView(R.hwc_human_casulty.login_layout);
//        final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_userName);
//        final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_password);
//        dateToInput.setHint("Date");
//        FormNameToInput.setHint("Name Of Form");
//
//        AppCompatButton logIn = (AppCompatButton) showDialog.findViewById(R.id.login_button);
//        showDialog.setTitle("Enter Data");
//        showDialog.setCancelable(false);
//        showDialog.show();
//        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        logIn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                dateOfData[0] = dateToInput.getText().toString();
//                formName[0] = FormNameToInput.getText().toString();
//                if(dateOfData[0] ==null || dateOfData[0].equals("")|| formName[0] == null || formName[0].equals("")){
//                    Toast.makeText(context , "Please fill the required field. ", Toast.LENGTH_SHORT).show();
//                }else {
//                    showDialog.dismiss();
//                }
//            }
//        });
//    }

}
