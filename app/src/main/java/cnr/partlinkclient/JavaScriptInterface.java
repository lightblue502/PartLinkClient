package cnr.partlinkclient;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;


public class JavaScriptInterface {
    private Context context;
    public JavaScriptInterface(Context context){
        this.context = context;
    }
    @JavascriptInterface
    public void makeToast(String message){
        //html ติดต่อไปหา android
        Log.d("JSI", "making toast from native code");
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
}
