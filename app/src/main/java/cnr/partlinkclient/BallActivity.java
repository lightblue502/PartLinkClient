package cnr.partlinkclient;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallActivity extends GameActivity{
    private Intent intent;
    private String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball);
        super.initialServiceBinding();

        intent = getIntent();
        event = intent.getStringExtra("ball_game");

//        if (savedInstanceState == null) {
//            StandbyFragment fragment = new StandbyFragment();
//            getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
//        }

        super.container = R.id.fragment_container;
        intent = getIntent();
        event = intent.getStringExtra("ball_game");

        onGameEvent(event, null);
    }

    public void changeToEnemyFragment(String[] params){
        EnemyFragment fragment = new EnemyFragment();
        fragment.setGameCommunicationService(gcs);
        fragment.setParams(params);
        replaceFragment(fragment);
    }

    public void changeToPlayerFragment(String[] params){
        PlayerFragment fragment = new PlayerFragment();
        fragment.setGameCommunicationService(gcs);
        fragment.setActivity(this);
        fragment.setParams(params);
        replaceFragment(fragment);
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment); // f1_container is your FrameLayout container
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    @Override
    public void onGameEvent(final String event, String[] params) {
        super.onGameEvent(event, params);
        Log.d(Utils.TAG, "processing game event (BALL): " + event);
       if(event.equals("change_ball")){
            ready();
       }else if(event.equals("enemy_start")){
           changeToEnemyFragment(params);
       }else if(event.equals("player_start")){
           changeToPlayerFragment(params);
       }
    }

    @Override
    public void ready() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        gcs.sendGameEvent("ball_ready", new String[]{"Ready"});
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isResumeAfterPause) {
            isResumeAfterPause = false;
            super.initialServiceBinding();
            gcs.sendGameEvent("game_resume", new String[]{});
        }
        Log.d(Utils.TAG, "IN onResume");
    }

    public static class EnemyFragment extends Fragment {
        public GameCommunicationService gcs;
        public String[] params;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_ball_enemy, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Button bomb = (Button)view.findViewById(R.id.bombBtn);
            bomb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(Utils.TAG, "bombEvent");
                    gcs.sendGameEvent("bombEvent", new String[]{Long.toString(System.currentTimeMillis())});
                }
            });
//            generateButton();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
//            randomSetValueButton(btns, params);
        }

        public void setGameCommunicationService(GameCommunicationService gcs){
            this.gcs = gcs;
        }

        public void setParams(String[] params){
            this.params = params;
        }
    }
    public static class PlayerFragment extends Fragment  implements SensorEventListener {
        public GameCommunicationService gcs;
        public Context context;
        public String[] params;

        //sensor
        private SensorManager sensorManager;
        private Sensor accelerometer;
        private float currentDeltaY = 0;
        private float deltaX = 0;
        private float deltaY = 0;
        private float deltaZ = 0;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_ball_player, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            //Event
            Button jump = (Button)view.findViewById(R.id.jumpBtn);
            jump.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(Utils.TAG,"jumpsEvent");
                    gcs.sendGameEvent("jumpEvent",new String[]{Long.toString(System.currentTimeMillis())});
                }
            });

//            generateButton();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            createSensor();
//            randomSetValueButton(btns, params);
        }

        public void setActivity(Context context){
            this.context = context;
        }
        public void setGameCommunicationService(GameCommunicationService gcs){
            this.gcs = gcs;
        }

        public void setParams(String[] params){
            this.params = params;
        }

        public void createSensor(){
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                // success! we have an accelerometer

                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // fail! we dont have an accelerometer!
            }

        }
        @Override
        public void onSensorChanged(SensorEvent event) {

            deltaX = event.values[0];
            deltaY = event.values[1];
            deltaZ = event.values[2];


            if(deltaY > 10)
                deltaY = 10;
            else if(deltaY < -10){
                deltaY = -10;
            }

            if(currentDeltaY == 0){
                currentDeltaY = deltaY;
            }
            if(deltaY > currentDeltaY+0.5 || deltaY < currentDeltaY-0.5){
                gcs.sendGameEvent("moveEvent", new String[]{Float.toString(deltaY)});
                currentDeltaY = deltaY;
            }
        }



        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onResume() {
            super.onResume();
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onPause() {
            super.onPause();
            sensorManager.unregisterListener(this);

        }

    }

}
