package cnr.partlinkclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.zip.Deflater;

/**
 * Created by lucksikalosuvalna on 3/7/16 AD.
 */
public class PictureUploader extends Thread {
    private GameCommunicationListener listener;
    private Socket gameSocket = null;
    private BufferedReader reader;
    private PrintWriter writer;
    private DataOutputStream dos;
    private String ipAddress = null;
    private Integer port = null;
    private int clientId;

    public PictureUploader(GameCommunicationListener listener, String ipAddress, Integer port, int clientId) {
        this.listener = listener;
        this.ipAddress = ipAddress;
        this.port = port;
        this.clientId = clientId;
    }
    public void processData(String line){
        listener.onIncomingEvent(line);
    }

    public void sendData(byte[] compress_data){
        try {
            Log.d(Utils.TAG, "sending :" + compress_data);
            dos.writeInt(compress_data.length);
            dos.write(compress_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(){
        Log.d(Utils.TAG, " IP: " + ipAddress + " Port: " + port);
        if(ipAddress != null && port != null) {
            Log.d(Utils.TAG, "In Picture-Uploader || IP :"+ipAddress + " | "+ port);
            while (true) {
                try {
                    gameSocket = new Socket(ipAddress, port);
                    reader = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
                    writer = new PrintWriter(gameSocket.getOutputStream());
                    dos = new DataOutputStream(gameSocket.getOutputStream());
                    Log.d(Utils.TAG, reader.toString());
                    if(gameSocket != null && reader != null){
                        writer.println("ID=" + clientId);
                        writer.flush();
                    }

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        Log.d(Utils.TAG, "readLine()" + line);
                        processData(line);
                    }

                } catch (Exception e) {
                    Log.d(Utils.TAG, "connection failed:" + e.toString());
                }
            }
        }else{
            Log.d(Utils.TAG, "Can't receive ipAddress");
        }

    }
}
