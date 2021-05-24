package cz.deepvision.websocket.grahpql;

import com.neovisionaries.ws.client.WebSocket;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
     protected static CustomTimer timerSingleton;
     protected Timer timer = new Timer();

     protected static CustomTimer getInstance() {
        if (timerSingleton == null)
            timerSingleton = new CustomTimer();
        return timerSingleton;
    }

    protected void checkTimer(WebSocket ws, int RECONNECT_TIME) {
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
