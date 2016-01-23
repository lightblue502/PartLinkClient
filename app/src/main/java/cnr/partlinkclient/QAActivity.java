package cnr.partlinkclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QAActivity extends GameActivity {
    private List<Button> btns;
    private Intent intent;
    private String event;
    private boolean isAsk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);
        initialServiceBinding();

        if (savedInstanceState == null) {
            StandbyFragment fragment = new StandbyFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            Log.d("QA", "gen");
        }

        intent = getIntent();
        event = intent.getStringExtra("qa_game");

        onGameEvent(event, null);
    }

    public void changeToAnswerFragment(String[] params){
        AnswerFragment fragment = new AnswerFragment();
        fragment.setGameCommunicationService(gcs);
        fragment.setParams(params);
        replaceFragment(fragment);


    }

    public void changeToAskFragment(String[] params){
        AskFragment fragment = new AskFragment();
        fragment.setParams(params);
        replaceFragment(fragment);
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment); // f1_container is your FrameLayout container
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.addToBackStack(null);
        ft.commit();

    }


    public void ready() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        gcs.sendGameEvent("qa_ready", new String[]{"Ready"});
    }

    @Override
    public void onGameEvent(String event, String[] params) {
        Log.d(Utils.TAG, "processing game event (QA): " + event);
        Log.d(Utils.TAG, "isAsk(QA): " + isAsk);
        if (event.equals("qa_choices") && !isAsk) {
            Log.d("QA", "params" + params);
            changeToAnswerFragment(params);
        } else if (event.equals("qa_change")) {
            isAsk = false;
            ready();
        } else if (event.equals("qa_question")) {
            isAsk = true;
            Log.d("QA", "params" + params[0]);
            changeToAskFragment(params);
        }
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }

    public static class AnswerFragment extends Fragment {
        public GameCommunicationService gcs;
        public List<Button> btns;
        public String[] params;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.answer, container, false);


        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            generateButton();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            randomSetValueButton(btns, params);
        }

        public void setGameCommunicationService(GameCommunicationService gcs){
            this.gcs = gcs;
        }

        public void setParams(String[] params){
            this.params = params;
        }

        public List<Button> generateButton() {
            btns = new ArrayList<>();
            btns.add((Button) getView().findViewById(R.id.button1));
            btns.add((Button) getView().findViewById(R.id.button2));
            btns.add((Button) getView().findViewById(R.id.button3));
            btns.add((Button) getView().findViewById(R.id.button4));
            createEventButton(btns);
            return btns;
        }

        public void createEventButton(List<Button> btns) {
            for (final Button btn : btns) {
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = (String) btn.getText();
                        gcs.sendGameEvent("qa_ans", new String[]{text});
                    }
                });
            }
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


    }

    public static class AskFragment extends Fragment {
        public String[] params;
        public TextView tv;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.asker, container, false);

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getTextView();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setTextView(tv, params);

        }
        public void setParams(String[] params){
            this.params = params;
        }
        public void getTextView(){
            tv = (TextView)getView().findViewById(R.id.textView);
        }
        public void setTextView(TextView t, String[] params){
            tv.setText(params[0]);
        }

    }

    public static class StandbyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.standby, container, false);

        }
    }
}

