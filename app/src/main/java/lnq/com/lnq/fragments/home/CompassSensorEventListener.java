package lnq.com.lnq.fragments.home;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

public class CompassSensorEventListener extends Fragment implements SensorEventListener {

    private float ALPHA = 0.97f;
    private SensorManager sensorManager;
    float arr[] = new float[]{
            0f,
            0f,
            0f
    };
    float geomagnetic[] = new float[]{0f, 0f, 0f};
    float orientation[] = new float[]{0f, 0f, 0f};

    float currentDegree = 0f;
    float degree = 0f;
    float[] R = new float[9];
    float[] I = new float[9];
    View imgCompass;


    String heading = null;
    SensorChangeEvent sensorChangeEvent;

    public CompassSensorEventListener(SensorManager sensorManager, View imgCompass,  SensorChangeEvent sensorChangeEvent) {
        this.sensorManager = sensorManager;
        this.imgCompass = imgCompass;


        this.sensorChangeEvent = sensorChangeEvent;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {

            arr[0] = ALPHA * arr[0] + (1 - ALPHA) * sensorEvent.values[0];
            arr[1] = ALPHA * arr[1] + (1 - ALPHA) * sensorEvent.values[1];
            arr[2] = ALPHA * arr[2] + (1 - ALPHA) * sensorEvent.values[2];

        }
        if (sensorEvent.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD) {

            geomagnetic[0] =
                    ALPHA * geomagnetic[0] + (1 - ALPHA) * sensorEvent.values[0];
            geomagnetic[1] =
                    ALPHA * geomagnetic[1] + (1 - ALPHA) * sensorEvent.values[1];
            geomagnetic[2] =
                    ALPHA * geomagnetic[2] + (1 - ALPHA) * sensorEvent.values[2];

        }

        if (arr != null && geomagnetic != null) {
            if (SensorManager.getRotationMatrix(R, I, arr, geomagnetic)) {
                SensorManager.getOrientation(R, orientation);

                degree = (float) Math.toDegrees(orientation[0]);
                degree = (degree + 360) % 360;

                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -degree,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );
                if (sensorChangeEvent != null)
                    sensorChangeEvent.onSensorChange(degree);
                ra.setDuration(300);
                ra.setFillAfter(true);
                imgCompass.startAnimation(ra);
                currentDegree = -degree;
            }

        }

        if (degree >= 338 || degree < 23) {
            //GOING NORTH
            heading = "N";
        } else if (degree >= 23 && degree < 68) {
            //GOING NORTH EAST
            heading = "NE";
        } else if (degree >= 68 && degree < 113) {
            //GOING EAST
            heading = "E";
        } else if (degree >= 113 && degree < 158) {
            //GOING SOUTH EAST
            heading = "SE";
        } else if (degree >= 158 && degree < 203) {
            //GOING SOUTH
            heading = "S";
        } else if (degree >= 203 && degree < 248) {
            //GOING SOUTH WEST
            heading = "SW";
        } else if (degree >= 248 && degree < 293) {
            //GOING WEST
            heading = "W";
        } else if (degree >= 293 && degree < 338) {
            //GOING NORTH WEST
            heading = "NW";
        }


    }

    interface SensorChangeEvent {
        void onSensorChange(float degree);
    }

}