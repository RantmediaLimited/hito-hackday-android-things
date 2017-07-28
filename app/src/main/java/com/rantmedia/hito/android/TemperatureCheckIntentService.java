package com.rantmedia.hito.android;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.things.contrib.driver.bmx280.Bmx280;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rantmedia.hito.android.models.TemperatureHistory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

/**
 * (C) Copyright 2017 Rantmedia Ltd (http://www.rantmedia.com)
 * Created by Russell Hicks on 28/07/2017
 * russell@rantmedia.com
 * All Rights Reserved
 */

public class TemperatureCheckIntentService extends IntentService{

    private static final String TAG = "TempService";

    public TemperatureCheckIntentService() {
        super("TempService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Temp Intent Running:");
        readAndProcessTemperature();
    }


    private void readAndProcessTemperature() {
        BigDecimal temp = readTemperature();
        if(temp != null)
        {
            displayOnLED(temp.toString());
            updateCurrentTemperature(temp.doubleValue());
        }
    }

    private BigDecimal readTemperature(){
        // Log the current temperature
        Bmx280 sensor = null;

        try {
            sensor = RainbowHat.openSensor();
            sensor.setTemperatureOversampling(Bmx280.OVERSAMPLING_1X);

            //round temperature
            BigDecimal sensorData = new BigDecimal(sensor.readTemperature());
            BigDecimal temp = sensorData.setScale(1, RoundingMode.DOWN);

            Log.d(TAG, "temperature:" + sensorData);
            // Close the device when done.
            sensor.close();


            return temp;
        } catch (IOException e) {
            Log.e(TAG, "Temperature read error (IO exception):" + e.getMessage());
        }

        return null;

    }

    //display on rainbow hat LED
    private void displayOnLED(String message){

        AlphanumericDisplay segment = null;
        try {
            segment = RainbowHat.openDisplay();
            segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
            segment.display(message);
            segment.setEnabled(true);
            segment.close();

        } catch (IOException e) {
            Log.e(TAG, "LED Display Error (IO exception):" + e.getMessage());
        }
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
