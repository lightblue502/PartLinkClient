package cnr.partlinkclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GameCommunicationService extends Service implements GameCommunicationListener {
    private final IBinder binder = new GameCommunicationServiceBinder();
    private GameCommunicator gameCommunicator = null;
    private String android_id;
    private String ipAddress;
    private Integer port;
    public GameCommunicationService() {

    }

    @Override
    public void onCreate() {
        setAndroidId(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    @Override
    public void onIncomingEvent(String line) {
        if(line != null){
            int idx = line.indexOf('|');
            String event = null;
            String[] params = null;
            if(idx > 0){
                event = line.substring(0, idx );
                params = line.substring(idx + 1).split(",");
            }else if(idx < 0 && line.length() > 0){
                event = line;
                params = new String[]{};
//                params =  new HashMap<String, Object>();
            }
            if(event != null)
                sendEvent(event, params);
        }

    }
    public void sendGameEvent(String event, String[] params){
        StringBuilder sb = new StringBuilder(event);
        sb.append('|');
        for(int i = 0; i < params.length; i++){
            sb.append(params[i]);
            if(i != params.length - 1){
                sb.append(',');
            }
        }
        gameCommunicator.sendData(sb.toString());
    }

    private void sendEvent(String name, String[] params){
        Intent intent = new Intent("game-event");
        intent.putExtra("name", name);
        intent.putExtra("params", params);
        Log.d(Utils.TAG, "broadcasting game event: " + name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        ipAddress = intent.getStringExtra("ipAddress");
        port = intent.getIntExtra("port", 0);
        startGameCommunicator();
        Log.d(Utils.TAG, "OnBind");

        return binder;
    }

    public void startGameCommunicator(){
        gameCommunicator = new GameCommunicator(this, ipAddress, port);
        gameCommunicator.start();
    }

    public void setAndroidId(String android_id){
        this.android_id = android_id;
    }

    public String getAndroidId(){
        return android_id;
    }

    public class GameCommunicationServiceBinder extends Binder {
        GameCommunicationService getService(){
            return GameCommunicationService.this;
        }
    }
}
