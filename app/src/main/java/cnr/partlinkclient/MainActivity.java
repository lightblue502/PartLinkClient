package cnr.partlinkclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private EditText address;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button button = (Button)findViewById(R.id.loginBtn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                process();
//            }
//        });


    }

    //product barcode mode
    public void scanBar(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //product qr code mode
    public void scanQR(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
//                address = (EditText)findViewById(R.id.addressText);
//                address.setText(contents);
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

//                toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                //checkbit 0.0.0.0

                Toast toast;
                String text;
                if(!isOnline(contents)){
                    text = "Connection failed";
                }else if (contents.length() < 7) {
                    text = "Wrong Room..";
                } else {
                    text = "Room "+contents;
                    process(contents);
                }
                toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.show();

            }
        }
    }

    public Boolean isOnline(String ipAdrress) {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+ipAdrress);
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void process(String ipAddress){
//        address = (EditText)findViewById(R.id.addressText);
//        String ipAddress = address.getText().toString();
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        intent.putExtra("port", 5566);
        Log.d(Utils.TAG, "MAIN : in process");
        startActivity(intent);
    }


}
