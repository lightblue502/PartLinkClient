package cnr.partlinkclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class GameCommunicationService extends Service implements GameCommunicationListener {
    private final IBinder binder = new GameCommunicationServiceBinder();
    private GameCommunicator gameCommunicator = null;
    private PictureUploader pictureUploader = null;
    private String ipAddress;
    private Integer port;
    public GameCommunicationService() {

    }

    @Override
    public void onCreate() {
    }
    public void makeToast(String text){
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    public boolean isConnectedToServer() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+ipAddress);
            Log.d(Utils.TAG,"IP :"+ipAddress);
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            if(!reachable)
                makeToast("Connection Failed");
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
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

    @Override
    public void createPictureUploader(int clientId){
        Log.d(Utils.TAG, "Create PictureUploader");
        if(pictureUploader == null) {
            pictureUploader = new PictureUploader(this, ipAddress, port + 1, clientId);
            pictureUploader.start();
        }
    }

    public void uploadPicture(byte[] picture){
        pictureUploader.sendData(picture);
    }

    public class GameCommunicationServiceBinder extends Binder {
        GameCommunicationService getService(){
            return GameCommunicationService.this;
        }
    }
}
