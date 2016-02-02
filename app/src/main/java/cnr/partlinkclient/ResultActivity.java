package cnr.partlinkclient;

import android.content.Intent;
import android.os.Bundle;

public class ResultActivity extends GameActivity {
    private Intent intent;
    private String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initialServiceBinding();
        setContentView(R.layout.activity_result);

        intent = getIntent();
        event = intent.getStringExtra("result");
        onGameEvent(event, null);
    }
    public void ready() {
        gcs.sendGameEvent("result_ready", new String[]{"Ready"});
    }

    public void onGameEvent(String event, String[] params) {
        super.onGameEvent(event, params);
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }
}
