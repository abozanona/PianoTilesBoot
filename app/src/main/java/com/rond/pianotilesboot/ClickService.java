package com.rond.pianotilesboot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;

/**
 * Created by Nullsky on 5/10/2017.
 */

public class ClickService extends Service {
    Handler h;
    Runnable r;
    public static boolean b =true;

    public static MainActivity.deviceScale ds;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        if (h != null) {
            h.removeCallbacks(r);
        }
        super.onDestroy();
    }
    private void stopService() {
        if (h != null) h.removeCallbacks(r);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        h = new Handler();
        final Context context = getApplicationContext();

        ClickService.ds = MainActivity.takeScreenShot();


        final int delay = 1000; //milliseconds

        r = new Runnable(){
            public void run(){
                MainActivity.getPixleColorAt();
                h.postDelayed(this, delay);
            }
        };

        h.postDelayed(r, delay);

        return super.onStartCommand(intent, flags, startId);
    }
}
