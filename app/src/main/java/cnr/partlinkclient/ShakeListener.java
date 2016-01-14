package cnr.partlinkclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by lucksikalosuvalna on 1/5/2016 AD.
 */
public class ShakeListener implements SensorEventListener {
    private Context mContext;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private OnShakeListener mShakeListener;

    public ShakeListener(Context context){
        mContext = context;
        resume();
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public interface OnShakeListener {
        public void onShake(int score);
    }
    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt((double)(x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if(mAccel > 5) {
            mShakeListener.onShake( (int)(mAccel - 4.99f));
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resume() {
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pause() {
        mSensorManager.unregisterListener(this);
    }
}
