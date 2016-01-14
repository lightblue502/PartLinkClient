package cnr.partlinkclient;

/**
 * Created by suthon on 12/23/2015.
 */
public interface GameCommunicationListener {
    public void onIncomingEvent(String event, String[] params);
    public String getAndroidId();

}
