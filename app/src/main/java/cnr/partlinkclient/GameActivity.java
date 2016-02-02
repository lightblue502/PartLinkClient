package cnr.partlinkclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by suthon on 12/23/2015.
 */
public abstract class GameActivity extends Activity {
    protected GameCommunicationService gcs;
    protected boolean bound = false;
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
        intent.putExtra("ipAddress", getIntent().getStringExtra("ipAddress"));
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
            Intent intent = new Intent(this, ShakeActivity.class);
            intent.putExtra("shake_game", "Ready");
            startActivity(intent);
        }else if(event.equals("numeric_start")){
            Intent intent = new Intent(this, NumericActivity.class);
            intent.putExtra("numeric_game", "Ready");
            startActivity(intent);
        }else if(event.equals("qa_start")){
            Intent intent = new Intent(this, QAActivity.class);
            intent.putExtra("qa_game", "Ready");
            startActivity(intent);
        }else if(event.equals("result_start")){
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("result", "Ready");
            startActivity(intent);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Utils.TAG, "onStop");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    protected void onServiceConnected() {

    }
}
