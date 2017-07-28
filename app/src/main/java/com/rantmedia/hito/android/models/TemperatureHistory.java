package com.rantmedia.hito.android.models;

/**
 * Model for a temperature history object
 * For easy sync with RealTime DB
 *
 *
 * (C) Copyright 2017 Rantmedia Ltd (http://www.rantmedia.com)
 * Created by Russell Hicks on 28/07/2017
 * russell@rantmedia.com
 * All Rights Reserved
 */



public class TemperatureHistory {

    private Double temperature;
    private Double fakeTemperature;


    public TemperatureHistory() {
    }

    public TemperatureHistory(Double temperature) {
        this.temperature = temperature;
    }

    public TemperatureHistory(Double temperature, Double fakeTemperature) {
        this.temperature = temperature;
        this.fakeTemperature = fakeTemperature;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getFakeTemperature() {
        return fakeTemperature;
    }

    public void setFakeTemperature(Double fakeTemperature) {
        this.fakeTemperature = fakeTemperature;
    }
}
