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
    private GameCommunicator gameCommunicator;
    private String android_id;
    private String ipAddress;
    private Integer port;
    public GameCommunicationService() {

    }

    @Override
    public void onCreate() {
        setAndroidId(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
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

    @Override
    public void onIncomingEvent(String event, String[] params) {
        sendEvent(event, params);
    }

    private void sendEvent(String name, String[] params){
        Intent intent = new Intent("game-event");
        intent.putExtra("name", name);
        Log.d(Utils.TAG, "broadcasting game event: " + name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        ipAddress = intent.getStringExtra("ipAddress");
        port = intent.getIntExtra("port", 0);

        gameCommunicator = new GameCommunicator(this, ipAddress, port);
        gameCommunicator.start();
        return binder;
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
