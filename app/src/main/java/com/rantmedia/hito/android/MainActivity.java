/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rantmedia.hito.android;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;


import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rantmedia.hito.android.models.TemperatureHistory;

import java.io.IOException;

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Bmx280SensorDriver mEnvironmentalSensorDriver;
    private SensorManager mSensorManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get reference to sensor manager
        mSensorManager = getSystemService(SensorManager.class);

        // Initialize temperature/pressure sensors
        try {
            mEnvironmentalSensorDriver = new Bmx280SensorDriver(BoardDefaults.getI2cBus());
            // Register the drivers with the framework
            mEnvironmentalSensorDriver.registerTemperatureSensor();
            mEnvironmentalSensorDriver.registerPressureSensor();

            Log.d(TAG, "Initialized I2C BMP280");
        } catch (IOException e) {
            throw new RuntimeException("Error initializing BMP280", e);
        }

        // Register the BMP280 temperature sensor
        Sensor temperature = mSensorManager
                .getDynamicSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE).get(0);
        mSensorManager.registerListener(mSensorEventListener, temperature,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    // Callback when SensorManager delivers new data.
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            final float value = event.values[0];

            if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                Log.d(TAG, "Temp change noted: " + value);
                updateCurrentTemperature(value);
            }
            /*if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                updateBarometerDisplay(value);
            }*/
        }



        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "accuracy changed: " + accuracy);
        }
    };

    //write temperature to realtime DB
    private void updateCurrentTemperature(Float temperature){


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("current_temperature").setValue(temperature);

        //add temperature history entry
        TemperatureHistory temperatureHistory = new TemperatureHistory(temperature.doubleValue(), temperature.doubleValue());
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        myRef.child("temperature_history").child(timeStamp).setValue(temperatureHistory);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


}
