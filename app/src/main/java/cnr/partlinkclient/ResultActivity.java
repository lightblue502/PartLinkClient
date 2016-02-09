package cnr.partlinkclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        ((Button)findViewById(R.id.nextGame)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextGame();
            }
        });
    }
    public void ready() {
        gcs.sendGameEvent("result_ready", new String[]{"Ready"});
    }

    public void nextGame() {
        ((Button)findViewById(R.id.nextGame)).setEnabled(false);
        gcs.sendGameEvent("playerConfirm", new String[]{});
    }

    public void onGameEvent(String event, String[] params) {
        super.onGameEvent(event, params);
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }
}
