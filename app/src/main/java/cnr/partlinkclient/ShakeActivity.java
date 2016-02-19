package cnr.partlinkclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShakeActivity extends GameActivity {
    private TextView tv;
    private RelativeLayout layout;
    private Intent intent;
    private boolean isShake;
    private String event;

    private int[] colorList = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.CYAN};
    private int currColor = 0;

    private ShakeListener mShaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        super.initialServiceBinding();

        super.container = R.id.bgLayout;
        intent = getIntent();
        event = intent.getStringExtra("shake_game");
        tv = (TextView)findViewById(R.id.shakeView);
        layout = (RelativeLayout) findViewById(R.id.bgLayout);
        layout.setBackgroundColor(Color.LTGRAY);


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
        super.onGameEvent(event, params);
        Log.d(Utils.TAG, "processing game event (SHAKE): " + event);
        if(event.equals("this_shake")){
            tv.setText("SHAKE NOW");
            layout.setBackgroundColor(Color.GREEN);
            currColor = 0;
            isShake = true;
        }else if(event.equals("change_shake")){
            isShake = false;
            tv.setText("STOP");
            layout.setBackgroundColor(Color.LTGRAY);
            ready();
        }else if(event.equals("numeric_start")){
            Intent intent = new Intent(this, NumericActivity.class);
            intent.putExtra("numeric_game", "Ready");
            startActivity(intent);
        }else if(event.equals("qa_start")){
            Intent intent = new Intent(this,QAActivity.class);
            intent.putExtra("qa_game", "Ready");
            startActivity(intent);
        }
    }

    @Override
    public void ready() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        gcs.sendGameEvent("shake_ready", new String[]{"Ready"});
    }
    public void receiveShake(int score){
        if(isShake){
            gcs.sendGameEvent("shake_game", new String[]{"Shaking", String.valueOf(score)});
            layout.setBackgroundColor(colorList[currColor++]);
            if(currColor >= colorList.length) currColor = 0;
        }
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }


    @Override
    protected void onPause() {
        mShaker.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShaker.resume();
        if(isResumeAfterPause) {
            isResumeAfterPause = false;
            super.initialServiceBinding();
            gcs.sendGameEvent("game_resume", new String[]{});
        }
        Log.d(Utils.TAG, "IN onResume");
    }
}
