package cnr.partlinkclient;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumericActivity extends GameActivity {
    private TextView tv;
    private List<Button> btns;
    private Intent intent;
    private String event;
    private int ans;
    private boolean isAnswering;
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeric);
        super.initialServiceBinding();
        btns = generateButton();

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
//        wv = (WebView)findViewById(R.id.webView);
//        wv.loadData("<h3> Hello world</h3>", "text/html","UTF-8");
//        wv.loadUrl("file:///android_asset/numeric.html");
//        wv.getSettings().setJavaScriptEnabled(true); // ทำให้ java script รันได้ใน java
//        wv.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        intent = getIntent();
        event = intent.getStringExtra("numeric_game");
        onGameEvent(event, null);
    }

    private List<Button> generateButton(){
        List<Button> btns = new ArrayList<>();
        btns.add((Button)findViewById(R.id.button1));
        btns.add((Button)findViewById(R.id.button2));
        btns.add((Button) findViewById(R.id.button3));
        btns.add((Button) findViewById(R.id.button4));
        btns.add((Button) findViewById(R.id.button5));
        btns.add((Button)findViewById(R.id.button6));
        btns.add((Button)findViewById(R.id.button7));
        btns.add((Button)findViewById(R.id.button8));
        btns.add((Button) findViewById(R.id.button9));
        createEventButton(btns);
        return btns;
    }

    private void randomSetValueButton(List<Button> btns, String[] params){
        List<Button> tempBtns = new ArrayList<>();
        tempBtns.addAll(btns);
        for (String ans : params){
            int randomBtn = new Random().nextInt(tempBtns.size());
            tempBtns.get(randomBtn).setText(ans);
            tempBtns.remove(randomBtn);
        }
    }

    private void createEventButton(List<Button> btns){
        for (final Button btn: btns) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) btn.getText();
                    gcs.sendGameEvent("numeric_ans", new String[]{text});
                }
            });
        }
    }
    @Override
    public void onGameEvent(String event, String[] params) {
        if(event.equals("numeric_question")){
            Log.d(Utils.TAG, "processing game event (NUMERIC): " + event);
            randomSetValueButton(btns, params);
            Log.d(Utils.TAG, "params" + params[0]);
        }else if(event.equals("numeric_change")){
            isAnswering = false;
            ready();
        }
    }

    public void ready() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        gcs.sendGameEvent("numeric_ready", new String[]{"Ready"});
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }
}
