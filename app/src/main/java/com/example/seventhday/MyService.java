package com.example.seventhday;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyService extends Service {

    private static final String NOTIFICATION_Service_CHANNEL_ID = "service_channel";
    private Handler handler;
    private String url;
    private Bitmap bitmap;
    public static final String FILE_NAME = "InternalFile";

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "onCreate:  Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i("TAG", "Downloaded");

                Bitmap image = (Bitmap) msg.obj;
                saveBitmap(image);
                sendBroadcast();
                stopSelf();
            }
        };

        new Thread() {
            @Override
            public void run() {
                bitmap = downloadImage(url);

                Message message = new Message();
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }.start();


        return super.onStartCommand(intent, flags, startId);
    }




    public void saveBitmap(Bitmap pictureBitmap) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendBroadcast() {
        Intent intent = new Intent();
        intent.putExtra("file",FILE_NAME);
        intent.setAction("com.example.SendBroadcast");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.i("TAG", "onBind: ");

        return null;
    }


    public Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        URL url;
        HttpsURLConnection httpsURLConnection = null;

        try {
            url = new URL(imageUrl);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.connect();

            if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpsURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


}