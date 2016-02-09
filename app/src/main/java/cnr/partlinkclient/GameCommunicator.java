package cnr.partlinkclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by suthon on 12/23/2015.
 */
public class GameCommunicator extends Thread {
    private GameCommunicationListener listener;
    private Socket gameSocket = null;
    private BufferedReader reader;
    private PrintWriter writer;
    private String ipAddress = null;
    private Integer port = null;

    public GameCommunicator(GameCommunicationListener listener, String ipAddress, int port){
        this.listener = listener;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void processData(String line){
        listener.onIncomingEvent(line);
    }

    public void sendData(String line){
        writer.println(line);
        Log.d(Utils.TAG, "sending " + line);
        writer.flush();
    }

    public void run(){
        Log.d(Utils.TAG," IP: " + ipAddress + " Port: " + port);
        if(ipAddress != null && port != null) {
            Log.d(Utils.TAG, "In GAME-COME || IP :"+ipAddress + " | "+ port);
            while (true) {
                try {

                    gameSocket = new Socket(ipAddress, port);
                    reader = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
                    writer = new PrintWriter(gameSocket.getOutputStream());
                    Log.d(Utils.TAG, reader.toString());
                    if (gameSocket != null && reader != null) {
                        Log.d(Utils.TAG, "gameSocket" + gameSocket.getLocalSocketAddress().toString());
                        writer.println(listener.getAndroidId());
                        writer.flush();
                        Log.d(Utils.TAG, "Sending Android ID Data");
                    }

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        Log.d(Utils.TAG, "readLine()" + line);
                        processData(line);
                    }
                    if((line = reader.readLine()) == null){
                        Log.d(Utils.TAG, "readLine() is NULL");
                    }
                    Log.d(Utils.TAG, "outside Loop!!!!!");
                } catch (Exception e) {
                    Log.d(Utils.TAG, "connection failed:" + e.toString());
                }
            }
        }else{
                Log.d(Utils.TAG, "Can't receive ipAddress");
        }

    }
}
