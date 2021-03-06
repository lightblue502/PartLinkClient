package cnr.partlinkclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

public class RegisterActivity extends GameActivity {
    private EditText nameEditText;
    private ImageView mImageView;
    private Button registerBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialServiceBinding();
        nameEditText = (EditText)findViewById(R.id.nameEditText);

        mImageView = (ImageView)findViewById(R.id.mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        registerBtn = ((Button)findViewById(R.id.registerBtn));
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent();
            }
        });

//        nameEditText.setOnEdit
        nameEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId ==EditorInfo.IME_ACTION_DONE) {
                    sendEvent();
                    // Do whatever you need
                }
                return true;
            }
        });
//        nameEditText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(event.getAction() == KeyEvent.ACTION_DOWN){
//                    switch (keyCode)
//                    {
//                        case KeyEvent.KEYCODE_DPAD_CENTER:
//                        case KeyEvent.KEYCODE_ENTER:
//                            sendEvent();
//                            return true;
//                        default:
//                            break;
//                    }
//                }
//                return false;
//            }
//        });

        nameEditText.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
        registerBtn.setVisibility(View.INVISIBLE);

    }

    public void sendEvent(){
        if (gcs.isConnectedToServer()){
            register();
            registerBtn.setEnabled(false);
            mImageView.setEnabled(false);
            nameEditText.setEnabled(false);

            //Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void dispatchTakePictureIntent() {
        openCamera = true;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        openCamera = false;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap roundBitmap =  getCroppedBitmap((Bitmap) extras.get("data"), 200);
            imageBitmap = roundBitmap;

            mImageView.setImageBitmap(imageBitmap);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(Color.parseColor("#BAB399"));
        paint.setColor(color);
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }
    @Override
    public void onGameEvent(final String event, String[] params) {
        super.onGameEvent(event, params);
        Log.d(Utils.TAG, "processing game event (MAIN): " + event);
        if(event.equals("register_ok")){
            registerBtn.setEnabled(false);
        }else if(event.equals("uiServerReady")){
            nameEditText.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
        }

        if(event.equals("your_team")){
            //params[0]
        }

    }

    @Override
    public void ready() {

    }

    public void register() {
        preparedPicture();
        gcs.sendGameEvent("register", new String[]{nameEditText.getText().toString(), imageBitmap == null ? "empty" : "picture"});
    }

    public void preparedPicture(){
        if(imageBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Log.d(Utils.TAG, "bytearray length: " + byteArray.length);
            gcs.uploadPicture(compressByteArray(byteArray));
        }
    }

    public byte[] compressByteArray(byte[] data){
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        compressor.setInput(data);
        compressor.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }




    @Override
    protected void onPause() {
        super.onPause();
    }
}
