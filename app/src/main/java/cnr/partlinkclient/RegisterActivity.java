package cnr.partlinkclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends GameActivity {
    private EditText nameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialServiceBinding();
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        ((Button)findViewById(R.id.registerBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    @Override
    public void onGameEvent(final String event, String[] params) {
        super.onGameEvent(event, params);
        Log.d(Utils.TAG, "processing game event (MAIN): " + event);
        if(event.equals("register_ok")){
            ((Button)findViewById(R.id.registerBtn)).setEnabled(false);
        }

        if(event.equals("your_team")){
            //params[0]
        }

    }

    @Override
    public void ready() {

    }

    public void register() {
        gcs.sendGameEvent("register", new String[]{nameEditText.getText().toString()});
    }
}
