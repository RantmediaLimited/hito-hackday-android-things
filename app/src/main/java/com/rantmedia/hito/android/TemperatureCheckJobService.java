package com.rantmedia.hito.android;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

/**
 * (C) Copyright 2017 Rantmedia Ltd (http://www.rantmedia.com)
 * Created by Russell Hicks on 28/07/2017
 * russell@rantmedia.com
 * All Rights Reserved
 */

public class TemperatureCheckJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("TempCheckJob", "OnStartJob called");
        Intent tempIntent = new Intent(this, TemperatureCheckIntentService.class);
        startService(tempIntent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
