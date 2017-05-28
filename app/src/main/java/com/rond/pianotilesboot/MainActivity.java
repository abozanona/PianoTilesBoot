package com.rond.pianotilesboot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/*
how to get file size
=>name file
$filename = "/storage/emulated/0/Download/img.dump"

=>make file
screencap $filename
screencap /storage/emulated/0/Download/img.dump

=>find size
$size_of_file = -s $filename;
$size_of_file = -s /storage/emulated/0/Download/img.dump;

=>print size
print $size_of_file . " Bytes"."\n";
print $size_of_file . " Bytes"."\n";

=>height*width = 921600
=>My file size is => 3686412
=> bs = 3686412 / (921600) = 4
=> offset = 3686412 - (921600)*4 = 12
 */
public class MainActivity extends AppCompatActivity {

    public static class deviceScale{
        public int width;//720
        public int height;//1280
    }

    public static String RunAsRoot(String[] cmds, String outPath) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        for (String tmpCmd : cmds) {
            os.writeBytes(tmpCmd+"\n");
        }
        os.writeBytes("exit\n");
        os.flush();
        //os.close();
        //p.waitFor();
        String result = readFile(outPath);
        return result;
    }

    //https://stackoverflow.com/a/24583529
    public static void getPixleColorAt(){
        if(ClickService.ds == null){
            return;
        }
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            path += "/img.dump";

            String outPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            outPath += "/imgResult.txt";
            // path => /storage/emulated/0/Download/img.dump

            Process sh = Runtime.getRuntime().exec("su", null,null);
            OutputStream  os = sh.getOutputStream();
            os.write(("/system/bin/screencap " + path).getBytes("ASCII"));
            // => /system/bin/screencap /storage/emulated/0/Download/img.dump
            os.flush();

            os.close();
            sh.waitFor();

            int width = ClickService.ds.width;
            int[] p = {
                width/8,                                //90
                // => dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360090 2>/dev/null | hd
                width/8 + width/4,                      //270
                // => dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360270 2>/dev/null | hd
                width/8 + width/4 + width/4,            //450
                // => dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360450 2>/dev/null | hd
                width/8 + width/4 + width/4 + width/4,  //630
                // => dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360630 2>/dev/null | hd
            };
            boolean[] b = {false, false, false, false};

            int i = 500;

            {
                String[] arr ={
                    "su",
                    "echo '' > /storage/emulated/0/Download/imgResult.txt",
                    "dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360093 2>/dev/null | hd >> /storage/emulated/0/Download/imgResult.txt",
                    "dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360273 2>/dev/null | hd >> /storage/emulated/0/Download/imgResult.txt",
                    "dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360453 2>/dev/null | hd >> /storage/emulated/0/Download/imgResult.txt",
                    "dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360633 2>/dev/null | hd >> /storage/emulated/0/Download/imgResult.txt"
                };

                String result = RunAsRoot(arr, outPath);
                result += "";
            }

            for(int counter = 0; counter<4; counter++)
                if(b[counter]) {
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                    String cmd = "/system/bin/input tap " + i +" " + p[counter] +"\n";
                    dos.writeBytes(cmd);
                    dos.writeBytes("exit\n");
                    dos.flush();
                    dos.close();
                    //process.waitFor();
                    Log.d("GAMEBOT", i +" " + p[counter]);
                }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GAMEBOT", e.getLocalizedMessage());
        }

    }

    private static String readFile(String fileName){
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            return "ERROR@!#";
        }

        return text.toString();
    }

    public static deviceScale takeScreenShot(){
        int height = 0;
        int width = 0;
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            path += "/img.png";

            // path => /storage/emulated/0/Download/img.png

            Process sh = Runtime.getRuntime().exec("su", null,null);
            OutputStream  os = sh.getOutputStream();
            os.write(("/system/bin/screencap " + path).getBytes("ASCII"));
            os.flush();

            os.close();
            sh.waitFor();

            final Bitmap x = BitmapFactory.decodeFile(path);

            height = x.getHeight();//1280
            width = x.getWidth();//720

            deviceScale ds = new deviceScale();
            ds.height = height;//1280
            ds.width = width;//720
            return ds;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GAMEBOT", e.getLocalizedMessage());
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            Process p = Runtime.getRuntime().exec("su", null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(this, ClickService.class));

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,ClickService.class));
        super.onDestroy();
    }
}
