package cz.deepvision.websocket.grahpql;

import com.neovisionaries.ws.client.WebSocket;

import java.util.Timer;
import java.util.TimerTask;

public class ManulReconnectTimer {
     protected static ManulReconnectTimer timerSingleton;
     protected Timer timer = new Timer();
     // 50 min 3 000 000
     private final int MANUAL_TIME = 300000;

    protected static ManulReconnectTimer getInstance() {
        if (timerSingleton == null)
            timerSingleton = new ManulReconnectTimer();
        return timerSingleton;
    }

    protected void checkTimer(WebSocket ws) {
        if(timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ws.disconnect("Manual re-iinit");
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
            }
        }, MANUAL_TIME);


    }
}
