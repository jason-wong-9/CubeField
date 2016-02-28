package com.jasonkcwong.projectionmaze;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    Sensor rotationvector;
    SensorManager sm;

    TextView displayReading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayReading = (TextView) findViewById(R.id.displayReading);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        rotationvector = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }

    protected void onResume() {
        super.onResume();

        sm.registerListener(this, rotationvector, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }


    float azimut;
    float pitch;
    float roll;
    float azimutSum = 0;
    float pitchSum = 0;
    float rollSum = 0;
    float azimutCount = 0;
    float pitchCount = 0;
    float rollCount = 0;

    private long lastUpdate = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.i("TAG", (System.currentTimeMillis() - lastUpdate) + "");
        if (System.currentTimeMillis() - lastUpdate < 400) {

            float orientation[] = new float[3];

            if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
                float R[] = new float[9];
                // calculate th rotation matrix
                SensorManager.getRotationMatrixFromVector(R, event.values);
                // get the azimuth value (orientation[0]) in degree

                azimut = SensorManager.getOrientation(R, orientation)[0];
                azimutSum += azimut;
                azimutCount++;
                pitch = SensorManager.getOrientation(R, orientation)[1];
                pitchSum += pitch;
                pitchCount++;
                roll = SensorManager.getOrientation(R, orientation)[2];
                rollSum += roll;
                rollCount++;
            }
        } else {
            lastUpdate = System.currentTimeMillis();
            float azimutAvg = azimutSum/azimutCount;
            float pitchAvg = pitchSum/pitchCount;
            float rollAvg = rollSum/rollCount;
            String s = String.format("Z: %.3f%nX: %.3f%nY: %.3f", Math.toDegrees(azimutAvg), Math.toDegrees(pitchAvg), Math.toDegrees(rollAvg));
            displayReading.setText(s);
            azimutSum = 0;
            azimutCount = 0;
            pitchSum = 0;
            pitchCount = 0;
            rollSum = 0;
            rollCount = 0;
        }
    }
synchronized
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
