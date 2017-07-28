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

package com.example.androidthings.myproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;


import com.example.androidthings.myproject.models.TemperatureHistory;
import com.google.android.things.contrib.driver.bmx280.Bmx280;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readTemperature();

        // Continuously report temperature.
        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    sensorManager.registerListener(
                            new SensorEventListener() {
                                @Override
                                public void onSensorChanged(SensorEvent event) {
                                    Log.i(TAG, "sensor changed: " + event.values[0]);
                                    readTemperature();
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                                    Log.i(TAG, "accuracy changed: " + accuracy);
                                }
                            },
                            sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    private void readTemperature(){
        // Log the current temperature
        Bmx280 sensor = null;
        try {
            sensor = RainbowHat.openSensor();
            sensor.setTemperatureOversampling(Bmx280.OVERSAMPLING_1X);

            //round temperature
            BigDecimal a = new BigDecimal(sensor.readTemperature());
            BigDecimal b = a.setScale(1, RoundingMode.DOWN);

            AlphanumericDisplay segment = RainbowHat.openDisplay();
            segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
            segment.display(b.toString());
            segment.setEnabled(true);

            Log.d(TAG, "temperature:" + b.doubleValue());
            // Close the device when done.
            sensor.close();
            updateCurrentTemperature(b.doubleValue());
        } catch (IOException e) {
            Log.d(TAG, "Temperature read error (IO exception):" + e.getMessage());
        }


    }

    //display on rainbow hat LED
    private void displayOnLED(String message){

    }

    //write temperature to realtime DB
    private void updateCurrentTemperature(Double temperature){

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("current_temperature").setValue(temperature);

        //add temperature history entry
        TemperatureHistory temperatureHistory = new TemperatureHistory(temperature);
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        myRef.child("temperature_history").child(timeStamp).setValue(temperatureHistory);



    }
}
