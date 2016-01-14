package cnr.partlinkclient;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class ShakeActivity extends GameActivity {
    private TextView tv;
    private Intent intent;
    private boolean isShake;
    private String event;

    private ShakeListener mShaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        super.initialServiceBinding();

        intent = getIntent();
        event = intent.getStringExtra("shake_game");
        tv = (TextView)findViewById(R.id.shakeView);


        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake(int score) {
                receiveShake(score);
            }
        });

        onGameEvent(event, null);
    }

    @Override
    public void onGameEvent(final String event, String[] params) {
        Log.d(Utils.TAG, "processing game event (SHAKE): " + event);
        if(event.equals("this_shake")){
            tv.setText("SHAKE NOW");
            isShake = true;
        }else if(event.equals("change_shake")){
            isShake = false;
            tv.setText("STOP");
            ready();
        }else if(event.equals("numeric_start")){
            Intent intent = new Intent(this, NumericActivity.class);
            intent.putExtra("numeric_game", "Ready");
            startActivity(intent);
        }

    }
    public void ready() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        gcs.sendGameEvent("shake_ready", new String[]{"Ready"});
    }
    public void receiveShake(int score){
        if(isShake){
            gcs.sendGameEvent("shake_game", new String[]{"Shaking",String.valueOf(score)});
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShaker.resume();
    }

    @Override
    protected void onPause() {
        mShaker.pause();
        super.onPause();
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }
}
