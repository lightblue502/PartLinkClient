package cnr.partlinkclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

public class RegisterActivity extends GameActivity {
    private EditText nameEditText;
    private ImageView mImageView;
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
        ((Button)findViewById(R.id.registerBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

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
            imageBitmap = (Bitmap) extras.get("data");

            mImageView.setImageBitmap(imageBitmap);
        }
    }
    @Override
    public void onGameEvent(final String event, String[] params) {
        super.onGameEvent(event, params);
        Log.d(Utils.TAG, "processing game event (MAIN): " + event);
        if(event.equals("register_ok")){
            ((Button)findViewById(R.id.registerBtn)).setEnabled(false);
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
        gcs.sendGameEvent("register", new String[]{nameEditText.getText().toString()});
    }

    public void preparedPicture(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.d(Utils.TAG, "bytearray length: " + byteArray.length);
        gcs.uploadPicture(compressByteArray(byteArray));
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
