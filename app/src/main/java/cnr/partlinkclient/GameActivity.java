package cnr.partlinkclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by suthon on 12/23/2015.
 */
public abstract class GameActivity extends Activity {
    protected PauseFragment fragment;
    protected boolean openCamera = false;
    protected int container;
    protected boolean isEndActivity = false;
    protected boolean isBackPress = false;
    protected boolean isResumeAfterPause = false;
    protected GameCommunicationService gcs;
    protected boolean bound = false;
    private String ipAddress;
    private ServiceConnection serviceConnection;
    private BroadcastReceiver broadcastReceiver;

    public void initialServiceBinding(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                GameCommunicationService.GameCommunicationServiceBinder binder = (GameCommunicationService.GameCommunicationServiceBinder)service;
                gcs = binder.getService();
                bound = true;
                GameActivity.this.onServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };

        Intent intent = new Intent(this, GameCommunicationService.class);
        ipAddress = getIntent().getStringExtra("ipAddress");
        intent.putExtra("ipAddress", ipAddress);
        intent.putExtra("port", getIntent().getIntExtra("port", 0));
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String event = intent.getStringExtra("name");
                String[] params = intent.getStringArrayExtra("params");
                onGameEvent(event, params);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("game-event"));
    }

    public void onGameEvent(String event, String[] params){
        if (event.equals("shake-start")) {
            Log.d(Utils.TAG, "GameActivity Shake-Start");
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, ShakeActivity.class);
            intent.putExtra("shake_game", "Ready");
            startActivity(intent);
        }else if(event.equals("numeric_start")){
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, NumericActivity.class);
            intent.putExtra("numeric_game", "Ready");
            startActivity(intent);
        }else if(event.equals("ball_start")){
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, BallActivity.class);
            intent.putExtra("ball_game", "Ready");
            startActivity(intent);
        }else if(event.equals("qa_start")){
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, QAActivity.class);
            intent.putExtra("qa_game", "Ready");
            startActivity(intent);
        }else if(event.equals("result_start")){
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("result", "Ready");
            startActivity(intent);
        } else if(event.equals("end_start")) {
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, EndActivity.class);
            intent.putExtra("end", "Ready");
            startActivity(intent);
        }else if(event.equals("blank_start")) {
            isBackPress = false;
            isEndActivity = true;
            Intent intent = new Intent(this, BlankActivity.class);
            intent.putExtra("blank", "Ready");
            startActivity(intent);
        }
        else if(event.equals("game_pause")){
            Log.d(Utils.TAG,"game Pause");
            if(!isBackPress) {
                changeToPauseFragment();
            }
        }else if(event.equals("resume_ok")) {
            try {
                PauseFragment fragment = (PauseFragment) getFragmentManager().findFragmentById(container);
                fragment.areadyClickResume();
            }catch (Exception e){
                Log.d(Utils.TAG, e.toString());
            }
        }else if(event.equals("game_resume")){
            isBackPress = false;
            fragment = null;
            onSuicidePauseFragment();
            ready();
        }else if(event.equals("game_resume_ball")){
            isBackPress = false;
            fragment = null;
            onSuicidePauseFragment();
        }
    }
    public abstract void ready();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(Utils.TAG, "onResume Game Activity");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Utils.TAG, "onPause Game Activity");
        if(!openCamera) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
        if(!openCamera && !isEndActivity && fragment == null) {
            changeToPauseFragment();
            isResumeAfterPause = true;
            gcs.sendGameEvent("game_pause", new String[]{});
        }
    }

    protected void onServiceConnected() {}

    @Override
    public void onBackPressed() {
        isBackPress = true;
        if(fragment == null) {
            changeToPauseFragment();
            gcs.sendGameEvent("game_pause", new String[]{});
        }
    }

    protected void changeToPauseFragment(){
        try {
            Bundle bundle = new Bundle();
            fragment = PauseFragment.newInstance(bundle);
            Log.d(Utils.TAG, "create new Instance");
        }catch (Exception e){
            Log.d(Utils.TAG, e.toString());
        }
        fragment.setGameCommunicationService(gcs);
        addFragment(fragment, container);

    }
    private void addFragment(Fragment fragment, int container){
        FragmentTransaction ft = getFragmentManager().beginTransaction().add(container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void onSuicidePauseFragment() {
        getFragmentManager().popBackStack();
    }
}
