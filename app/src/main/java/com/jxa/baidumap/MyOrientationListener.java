package com.jxa.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by jxa on 2016/7/2.
 */
public class MyOrientationListener implements SensorEventListener {
    private SensorManager mSensorManager;
    private Context mContext;
    private Sensor mSensor;
    private float lastX;
    private OnOrientationLisenter orientationLisenter;

    public  MyOrientationListener(Context context) {
        this.mContext = context;
    }

    public void start() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            //获取方向传感器
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float value = sensorEvent.values[SensorManager.DATA_X];
            if (Math.abs(value - lastX) > 1.0) {
                if (orientationLisenter != null) {
                    orientationLisenter.onOrientationChange(value);
                }
            }
            lastX = value;
        }
    }

    public void setOrientationLisenter(OnOrientationLisenter orientationLisenter) {
        this.orientationLisenter = orientationLisenter;
    }


    public interface OnOrientationLisenter {
        void onOrientationChange(float value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
