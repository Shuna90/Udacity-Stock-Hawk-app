package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

public class StockIntentService extends IntentService {
    private static final String LOG_TAG = StockIntentService.class.getSimpleName();
    public static final String SERVICE_TAG = "tag";
    public static final String SERVICE_SYMBOL = "symbol";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_INIT = "init";

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra(SERVICE_TAG).equals(ACTION_ADD)) {
            args.putString(SERVICE_SYMBOL, intent.getStringExtra(SERVICE_SYMBOL));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(SERVICE_TAG), args));
    }
}
