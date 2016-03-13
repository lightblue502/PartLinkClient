package cnr.partlinkclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EndActivity extends GameActivity {
    private Intent intent;
    private String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initialServiceBinding();
        setContentView(R.layout.activity_end);

        intent = getIntent();
        event = intent.getStringExtra("end");
        onGameEvent(event, null);

        ((Button)findViewById(R.id.endGame)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextGame();
            }
        });
    }

    @Override
    public void ready() {
        gcs.sendGameEvent("end_ready", new String[]{"Ready"});
    }

    public void nextGame() {
        ((Button)findViewById(R.id.endGame)).setEnabled(false);
        gcs.sendGameEvent("playerRestartGame", new String[]{});

    }

//    public void restartGame(){
//        Intent restart = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(restart);
//
//    }

    public void restartGame(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Following code will restart your application after 2 seconds
        AlarmManager mgr = (AlarmManager) getBaseContext()
                .getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                pendingIntent);

        //This will finish your activity manually
        this.finish();

        //This will stop your application and take out from it.
        System.exit(2);
    }

    public void onGameEvent(String event, String[] params) {
        super.onGameEvent(event, params);
        if (event.equals("restartGame")) {
            restartGame();
            gcs.onDestroy();
        }
    }

    @Override
    protected void onServiceConnected() {
        ready();
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
}
