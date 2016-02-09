package cnr.partlinkclient;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumericActivity extends GameActivity implements SuicidalFragmentListener{
    private TextView tv;
    private List<Button> btns;
    private Intent intent;
    private String event;
    private int ans;
    private boolean canAnswer;
    private boolean isResumeAfterPause = false;
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeric);
        super.initialServiceBinding();
        btns = generateButton();
        resetTextButton();

        intent = getIntent();
        event = intent.getStringExtra("numeric_game");
        onGameEvent(event, null);
    }

    private List<Button> generateButton(){
        List<Button> btns = new ArrayList<>();
        btns.add((Button) findViewById(R.id.button1));
        btns.add((Button)findViewById(R.id.button2));
        btns.add((Button) findViewById(R.id.button3));
        btns.add((Button) findViewById(R.id.button4));
        btns.add((Button) findViewById(R.id.button5));
        btns.add((Button)findViewById(R.id.button6));
        btns.add((Button) findViewById(R.id.button7));
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
                    if (canAnswer)
                        gcs.sendGameEvent("numeric_ans", new String[]{text});
                }
            });
        }
    }

    private  void resetTextButton(){
        Log.d("DEBUG_resetTextButton", "can i reset?");
        for (Button btn :btns){
            btn.setText("");
        }
    }
    @Override
    public void onGameEvent(String event, String[] params) {
        Log.d(Utils.TAG, "EVENT :" + event);
        super.onGameEvent(event, params);
        if(event.equals("numeric_question")){
            canAnswer = true;
            Log.d(Utils.TAG, "processing game event (NUMERIC): " + event);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            randomSetValueButton(btns, params);
        }else if(event.equals("numeric_again")) {
            canAnswer = false;
            ready();
        }else if(event.equals("numeric_newRound")){
            resetTextButton();
        }else if(event.equals("numeric_restart")){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }else if(event.equals("numeric_pause")) {

        }else if (event.equals("player_reconnect")){
                Log.d("DEBUG_QAactivity","user has reconnect and ready");
                ready();
        }else if(event.equals("endGame")){
            Log.d(Utils.TAG,"endGame");
        }else if(event.equals("game_resume")){
            Log.d(Utils.TAG,"resumeGame");
            ready();
        }
    }

    public void ready() {
        gcs.sendGameEvent("numeric_ready", new String[]{"Ready"});
    }

    @Override
    protected void onServiceConnected() {
        Log.d(Utils.TAG, "onServiceConnected");
        ready();
    }

    public void pauseTheGame(View view){
        Log.d(Utils.TAG, "Game is pause");
        changeToPauseFragment();
        gcs.sendGameEvent("game_pause", new String[]{});

    }
    /////////////////////// Pause Fragment /////////////////////////////////
    public void changeToPauseFragment(){
        PauseFragment fragment = new PauseFragment();
        fragment.setGameCommunicationService(gcs);
        addFragment(fragment);

    }
    public void addFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onSuicidePauseFragment() {
        Log.d(Utils.TAG, "Suicide Fragment");
        getFragmentManager().popBackStack();
    }
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onPause() {
        super.onPause();
        isResumeAfterPause = true;
        gcs.sendGameEvent("game_pause", new String[]{});
        Log.d(Utils.TAG, "IN onPause");
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
}
