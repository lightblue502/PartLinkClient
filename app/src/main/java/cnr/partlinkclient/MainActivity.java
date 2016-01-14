package cnr.partlinkclient;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    private EditText address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.loginBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process();
            }
        });


    }
    public void process(){
        EditText ed = (EditText)findViewById(R.id.addressText);
        String ipAddress = ed.getText().toString();
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        intent.putExtra("port", 5566);
        Log.d(Utils.TAG, "MAIN : in process");
        startActivity(intent);
    }


}
