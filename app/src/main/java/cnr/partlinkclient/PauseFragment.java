package cnr.partlinkclient;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PauseFragment extends Fragment {
    public GameCommunicationService gcs;
    public SuicidalFragmentListener suicideListener;
    public Button resumeBtn;
    public PauseFragment() {

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            suicideListener = (SuicidalFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(getActivity().getClass().getSimpleName() + " must implement the suicide listener to use this fragment", e);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pause, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        generateButton();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeToGame();
            }
        });
    }

    public void setGameCommunicationService(GameCommunicationService gcs){
        this.gcs = gcs;
    }
//    public void generateButton(){
//        resumeBtn = (Button) getView().findViewById(R.id.resumeBtn);
//    }
    public void resumeToGame(){
        gcs.sendGameEvent("game_resume", new String[]{});
        suicideListener.onSuicidePauseFragment();
    }

}
