package com.jasonkcwong.projectionmaze;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private long lastUpdate = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {

/*        Log.i("TAG", (System.currentTimeMillis() - lastUpdate) + "");
        if (System.currentTimeMillis() - lastUpdate < 1000) {
            return;
        }*/

        lastUpdate = System.currentTimeMillis();


        float orientation[] = new float[3];

        if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
            float R[] = new float[9];
            // calculate th rotation matrix
            SensorManager.getRotationMatrixFromVector(R, event.values);
            // get the azimuth value (orientation[0]) in degree

            azimut = SensorManager.getOrientation(R, orientation)[0];
            pitch = SensorManager.getOrientation(R, orientation)[1];
            roll = SensorManager.getOrientation(R, orientation)[2];
        }
        String s = String.format("Z: %.3f%nX: %.3f%nY: %.3f", Math.toDegrees(azimut), Math.toDegrees(pitch), Math.toDegrees(roll));
        displayReading.setText(s);
    }
synchronized
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
