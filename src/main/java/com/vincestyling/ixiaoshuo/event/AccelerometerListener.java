package com.vincestyling.ixiaoshuo.event;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/**
 * code from : http://snipplr.com/view/14890/handling-shake-events-on-android-15/
 * also work sample : http://stackoverflow.com/a/11972661/1294681
 */
public class AccelerometerListener implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;

    private long lastUpdateTime = -1;
    private float lastAxisX, lastAxisY, lastAxisZ;
    private static final int FORCE_THRESHOLD = 1200;

    private OnShakeListener onShakeListener;

    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }

    public AccelerometerListener(Activity parent) {
        sensorManager = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) sensor = sensors.get(0);
    }

    public void start() {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor s, int valu) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length < 3) return;

        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - lastUpdateTime;

        if (diffTime > 100) {
            lastUpdateTime = currentTime;

            float currentAxisX = event.values[0];
            float currentAxisY = event.values[1];
            float currentAxisZ = event.values[2];

            float currenForce = Math.abs(currentAxisX + currentAxisY + currentAxisZ - lastAxisX - lastAxisY - lastAxisZ) / diffTime * 10000;

            if (currenForce > FORCE_THRESHOLD) {
                onShakeListener.onShake();
            }

            lastAxisX = currentAxisX;
            lastAxisY = currentAxisY;
            lastAxisZ = currentAxisZ;
        }
    }

    public interface OnShakeListener {
        void onShake();
    }
}
