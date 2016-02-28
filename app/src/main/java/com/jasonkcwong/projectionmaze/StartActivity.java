package com.jasonkcwong.projectionmaze;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.jasonkcwong.projectionmaze.graphics.Cube;
import com.jasonkcwong.projectionmaze.graphics.MapUtils;
import com.jasonkcwong.projectionmaze.graphics.Projections;
import com.jasonkcwong.projectionmaze.graphics.RenderQueueItem;
import com.jasonkcwong.projectionmaze.graphics.Vector;

import java.util.List;

import static com.jasonkcwong.projectionmaze.graphics.Constants.X;
import static com.jasonkcwong.projectionmaze.graphics.Constants.Y;
import static com.jasonkcwong.projectionmaze.graphics.Constants.Z;

/**
 * Created by jason on 16-02-28.
 */
public class StartActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor rotationvector;
    private SensorManager sm;

    public static final String EXTRA_AZIMUT_OFFSET = "azimut offset";
    public static final String EXTRA_PITCH_OFFSET = "pitch offset";
    public static final String EXTRA_ROLL_OFFSET = "roll offset";

    private double mAzimutOffset;
    private double mPitchOffset;
    private double mRollOffset;

    private GameView mGameView;
    private TextView mGameInfo;

    private static final double FOV = Math.toRadians(90);
    private static final int RENDER_DIST = 10000;

    private static final int MAP_SIZE = 10000;
    private static final int NUM_SQUARES = 100;
    private static final int SQUARE_SIZE = 1000;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDist;
    private boolean mViewIsReady = false;

    private int mSpeed = 30;

    private Cube[] mCubes;
    private Vector mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startscreen);

        mAzimutOffset = getIntent().getFloatExtra(EXTRA_AZIMUT_OFFSET, 0);
        mPitchOffset = getIntent().getFloatExtra(EXTRA_PITCH_OFFSET, 0);
        mRollOffset = getIntent().getFloatExtra(EXTRA_ROLL_OFFSET, 0);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationvector = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mGameView = (GameView) findViewById(R.id.game_view);
        mGameInfo = (TextView) findViewById(R.id.game_info);

        mCubes = MapUtils.generateMap(MAP_SIZE, NUM_SQUARES, SQUARE_SIZE);

        ViewTreeObserver vto = mGameView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mGameView.getViewTreeObserver().removeOnPreDrawListener(this);

                mScreenWidth = mGameView.getWidth();
                mScreenHeight = mGameView.getHeight();
                mScreenDist = Projections.calculateScreenDist(mScreenWidth, FOV);
                mObserver = new Vector(-1000, 1000, -mScreenDist);
                mViewIsReady = true;

                return true;
            }
        });
    }

    protected void onResume() {
        super.onResume();

        sm.registerListener(this, rotationvector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    float azimutSum = 0;
    float pitchSum = 0;
    float rollSum = 0;
    float azimutCount = 0;
    float pitchCount = 0;
    float rollCount = 0;

    private long lastUpdate = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Log.i("TAG", (System.currentTimeMillis() - lastUpdate) + "");
        if (System.currentTimeMillis() - lastUpdate < 40) {

            float orientation[] = new float[3];

            if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
                float R[] = new float[9];
                // calculate th rotation matrix
                SensorManager.getRotationMatrixFromVector(R, event.values);
                // get the azimuth value (orientation[0]) in degree

                float azimut = SensorManager.getOrientation(R, orientation)[0];
                azimutSum += azimut;
                azimutCount++;
                float pitch = SensorManager.getOrientation(R, orientation)[1];
                pitchSum += pitch;
                pitchCount++;
                float roll = SensorManager.getOrientation(R, orientation)[2];
                rollSum += roll;
                rollCount++;
            }
        } else {
            lastUpdate = System.currentTimeMillis();
            float azimutAvg = azimutSum/azimutCount;
            float pitchAvg = pitchSum/pitchCount;
            float rollAvg = rollSum/rollCount;

            if(azimutAvg < 0){
                azimutAvg += Math.PI * 2;
            }
            if(pitchAvg < 0){
                pitchAvg += Math.PI * 2;
            }
            if(rollAvg < 0){
                rollAvg += Math.PI *2;
            }

            azimutAvg -= mAzimutOffset;
            pitchAvg -= mPitchOffset;
            rollAvg -= mRollOffset;

            String s = String.format("Angles%nY: %.3f%nZ: %.3f%nX: %.3f%n%nPosition%nX: %.3f%nY: %.3f%nZ: %.3f",
                    Math.toDegrees(azimutAvg), Math.toDegrees(pitchAvg), Math.toDegrees(rollAvg),
                    mObserver.get(X), mObserver.get(Y), mObserver.get(Z));
            mGameInfo.setText(s);

            if (mViewIsReady) {
                Vector angle = new Vector(rollAvg, azimutAvg, pitchAvg);
                Vector d = Projections.getNormal(angle).multiply(mSpeed);
                Log.i("Vector1", d.toString());
                for (int i=0; i<3; i++) {
                    double temp = mObserver.get(i)+d.get(i);
                    if (!Double.isNaN(temp)) {
                        Log.i("Vector1"+i, temp+"");
                        mObserver.set(i, temp);
                    }
                }
                Log.i("Vector2", mObserver.toString());
                List<RenderQueueItem> renderQueue = Projections.getRenderQueue(mCubes, mObserver, angle,
                        mScreenWidth, mScreenHeight, mScreenDist, RENDER_DIST);
                mGameView.setRenderQueue(renderQueue);
            }

            resetOrientation();
        }
    }

    public void resetOrientation(){
        azimutSum = 0;
        azimutCount = 0;
        pitchSum = 0;
        pitchCount = 0;
        rollSum = 0;
        rollCount = 0;
    }

    synchronized
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
