package com.example.aplikacja3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFile extends IntentService {

    private static final String DOWNLOAD_FILE =
            "com.example.aplikacja3.action.download_file";

    private static final String URLParameter =
            "com.example.aplikacja3.extra.url_parameter";

    private static final String CHANNEL_ID = "com.example.aplikacja3.channel.1";

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    private long mDownloadedBytes = 0;

    private long mTotalSize = 0;

    int BUFFER_SIZE = 32*10^3;


    public DownloadFile() {
        super("DownloadFile");
    }

    public static void startService(Context context, String parameter){
        Intent intent = new Intent(context, DownloadFile.class);
        intent.setAction(DOWNLOAD_FILE);
        intent.putExtra(URLParameter,parameter);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        prepareNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());

        if(intent != null){
            final String action = intent.getAction();
            if (DOWNLOAD_FILE.equals(action)) {
                final String url = intent.getStringExtra(URLParameter);
                //execute task
                download(url);
            } else {
                Log.e("intent_service","nieznana akcja");
            }
        }
        Log.d("intent_service","usługa wykonała zadanie");
    }

    private void download(String urlParam) {
        FileOutputStream fileOutputStream = null;
        HttpsURLConnection connection = null;
        DataInputStream reader = null;

        try {
            URL url = new URL(urlParam);
            File workingFile = new File(url.getFile());
            File outputFile = new File(Environment.getExternalStorageDirectory() +
                    File.separator + workingFile.getName());
            if(outputFile.exists()){
                outputFile.delete();
            }


            connection = (HttpsURLConnection) url.openConnection();
            reader = new DataInputStream(connection.getInputStream());
            fileOutputStream = new FileOutputStream(outputFile.getPath());

            mTotalSize = connection.getContentLength();

            byte[] buffer = new byte[BUFFER_SIZE];
            int downloaded = reader.read(buffer,0,BUFFER_SIZE);
            while (downloaded != -1){
                fileOutputStream.write(buffer, 0, downloaded);
                mDownloadedBytes += downloaded;
                mNotificationManager.notify(NOTIFICATION_ID,createNotification());
                downloaded = reader.read(buffer, 0, BUFFER_SIZE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(connection != null) connection.disconnect();
            if(reader!= null){
                try {
                    reader.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void prepareNotificationChannel(){
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);

            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setContentTitle(getString(R.string.download_notification))
                .setProgress(100,progressValue(),false)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_HIGH);

        notificationBuilder.setOngoing(progressValue() == 100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
        }

        return notificationBuilder.build();
    }

    private int progressValue(){
        if(mTotalSize == 0 || mDownloadedBytes == 0)
            return 0;
        return (int)((mDownloadedBytes * 1.0 / mTotalSize) * 100);
    }
}
