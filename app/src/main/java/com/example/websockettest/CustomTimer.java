package com.example.websockettest;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    private static CustomTimer timerSingleton;
    private Timer timer = new Timer();

    public static CustomTimer getInstance() {
        if (timerSingleton == null)
            timerSingleton = new CustomTimer();
        return timerSingleton;
    }

    public void checkTimer(WebSocket ws, int RECONNECT_TIME) {
        if(timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ws.disconnect();
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
            }
        }, RECONNECT_TIME);


    }
}
