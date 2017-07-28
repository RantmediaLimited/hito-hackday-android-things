package com.rantmedia.hito.android;

import android.os.Build;

/**
 * (C) Copyright 2017 Rantmedia Ltd (http://www.rantmedia.com)
 * Created by Russell Hicks on 28/07/2017
 * russell@rantmedia.com
 * All Rights Reserved
 */

@SuppressWarnings("WeakerAccess")
public final class BoardDefaults {

    private static final String DEVICE_RPI3 = "rpi3";
    private static final String DEVICE_IMX7 = "imx7d_pico";

    public static String getI2cBus() {
        switch (Build.DEVICE) {
            case DEVICE_RPI3:
                return "I2C1";
            case DEVICE_IMX7:
                return "I2C1";
            default:
                throw new IllegalArgumentException("Unsupported device: " + Build.DEVICE);
        }
    }

    public static String getSpiBus() {
        switch (Build.DEVICE) {
            case DEVICE_RPI3:
                return "SPI0.0";
            case DEVICE_IMX7:
                return "SPI3.1";
            default:
                throw new IllegalArgumentException("Unsupported device: " + Build.DEVICE);
        }
    }
}
