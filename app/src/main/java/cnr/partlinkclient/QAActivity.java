package cnr.partlinkclient;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QAActivity extends GameActivity {
    private List<Button> btns;
    private Intent intent;
    private String event;
    private boolean isAsk = false;
    private AnswerFragment answerFragment;
    private AskFragment askFragment;
    private Typeface superspaceType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);
        superspaceType = Typeface.createFromAsset(getAssets(), "fonts/Superspace.otf");
        initialServiceBinding();

        if (savedInstanceState == null) {
            StandbyFragment fragment = new StandbyFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            Log.d("QA", "gen");
        }

        super.container = R.id.fragment_container;
        intent = getIntent();
        event = intent.getStringExtra("qa_game");

        onGameEvent(event, null);
    }

    public void changeToAnswerFragment(String[] params){
        answerFragment = new AnswerFragment();
        answerFragment.setGameCommunicationService(gcs);
        answerFragment.setParams(params);
        answerFragment.setActivity(this);
        replaceFragment(answerFragment);


    }

    public void changeToAskFragment(String[] params){
        askFragment = new AskFragment();
        askFragment.setActivity(this);
        askFragment.setParams(params);
        askFragment.setGameCommunicationService(gcs);
        replaceFragment(askFragment);
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment); // f1_container is your FrameLayout container
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void ready() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        gcs.sendGameEvent("qa_ready", new String[]{"Ready"});
    }

    @Override
    public void onGameEvent(String event, String[] params) {
        super.onGameEvent(event, params);
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
        } else if (event.equals("player_reconnect")){
            Log.d("DEBUG_QAactivity","user has reconnect and ready");
            isAsk = false;
            ready();
        }else if(event.equals("choiceOpen")){
            answerFragment.setVisibilityButtons(View.VISIBLE);
            answerFragment.setVisiblityTextView(View.INVISIBLE);
        }
    }

    @Override
    protected void onServiceConnected() {
        ready();
    }


    public static class AnswerFragment extends Fragment {
        public GameCommunicationService gcs;
        public QAActivity qaActivity;
        public List<Button> btns;
        public TextView tv;
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
            tv = (TextView) view.findViewById(R.id.listenTv);
            tv.setTypeface(qaActivity.getTypeFace());
            setVisiblityTextView(View.VISIBLE);
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
                btn.setVisibility(View.INVISIBLE);
                btn.setTypeface(qaActivity.getTypeFace());
            }
        }

        private void setVisibilityButtons(int value){
            for (Button btn : btns) {
                btn.setVisibility(value);
            }
        }

        private void setVisiblityTextView(int value){
            tv.setVisibility(value);
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


        public void setActivity(QAActivity activity) {
            this.qaActivity = activity;
        }
    }

    private Typeface getTypeFace() {
        return superspaceType;
    }

    public static class AskFragment extends Fragment {
        public String[] params;
        public GameCommunicationService gcs;
        public TextView tv;
        public TextView questionIcon;
        private QAActivity qaActivity;

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

            init(view);

            final Button readyQuestion = (Button)view.findViewById(R.id.readyBtn);
            readyQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gcs.sendGameEvent("readyQuestion", new String[]{});
                    setVisibleView();
                    readyQuestion.setVisibility(View.INVISIBLE);
                }
            });

        }
        public void setVisibleView(){
            tv.setVisibility(View.VISIBLE);
            questionIcon.setVisibility(View.VISIBLE);

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setTextView(tv, params);

        }
        public void setParams(String[] params){
            this.params = params;
        }
        public void setGameCommunicationService(GameCommunicationService gcs){
            this.gcs = gcs;
        }
        public void init(View v){
            tv = (TextView)v.findViewById(R.id.questionText);
            tv.setTypeface(qaActivity.getTypeFace());
            tv.setVisibility(View.INVISIBLE);

            questionIcon = (TextView)getView().findViewById(R.id.questionIcon);
            questionIcon.setVisibility(View.INVISIBLE);
        }
        public void setTextView(TextView t, String[] params){
            tv.setText(params[0]);
        }

        public void setActivity(QAActivity qaActivity) {
            this.qaActivity = qaActivity;
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
}

