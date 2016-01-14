package cnr.partlinkclient;

/**
 * Created by nuhwi_000 on 14/1/2559.
 */
public class ManageAddress {
    private String address;
    private int port;

    public ManageAddress(String address, int port){
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
