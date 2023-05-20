package com.example.aplikacja3.DownloadServie;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.aplikacja3.MainActivity;
import com.example.aplikacja3.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFileService extends IntentService {

    private static final String DOWNLOAD_FILE =
            "com.example.aplikacja3.action.download_file";

    private static final String URLParameter =
            "com.example.aplikacja3.extra.url_parameter";

    private static final String CHANNEL_ID = "com.example.aplikacja3.channel.1";

    private static final int NOTIFICATION_ID = 1;

    public static final String NOTIFICATION = "com.example.aplikacja3.DownloadFile.NOTIFICATION";
    private NotificationManager mNotificationManager;

    private long mDownloadedBytes = 0;

    private long mTotalSize = 0;

    int BUFFER_SIZE = 8192;


    public DownloadFileService() {
        super("DownloadFile");
    }


    public static void startService(Context context, String parameter){
        Intent startIntent = new Intent(context, DownloadFileService.class);
        startIntent.setAction(DOWNLOAD_FILE);
        startIntent.putExtra(URLParameter, parameter);
        context.startService(startIntent);
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
                Log.e("download","undefined action");
            }
        }
        Log.d("download","Service Task done");
    }

    private void download(String urlParam) {
        FileOutputStream fileOutputStream = null;
        HttpsURLConnection connection = null;
        DataInputStream reader = null;

        try {
            URL url = new URL(urlParam);

            connection = (HttpsURLConnection) url.openConnection();
            reader = new DataInputStream(connection.getInputStream());
            mTotalSize = connection.getContentLength();
            if(mTotalSize == -1){
                sendBroadcast();
                return;
            }

            File workingFile = new File(url.getFile());
            File outputFile = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    File.separator + workingFile.getName());
            if(outputFile.exists()){
                if(outputFile.delete())
                    Log.d("download", "File deleted");
            }
            fileOutputStream = new FileOutputStream(outputFile.getPath());

            byte[] buffer = new byte[BUFFER_SIZE];
            int downloaded = reader.read(buffer,0,BUFFER_SIZE);

            while (downloaded != -1){
                fileOutputStream.write(buffer, 0, downloaded);
                mDownloadedBytes += downloaded;
                mNotificationManager.notify(NOTIFICATION_ID,createNotification());
                sendBroadcast();
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
            mDownloadedBytes = 0;
        }
    }

    private void prepareNotificationChannel(){
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        //Test
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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
        return (int)(((double)mDownloadedBytes / mTotalSize) * 100);
    }


    public static final String downloadedBytes = "com.example.aplikacja3.broadcast.downloaded";
    public static final String totalBytes = "com.example.aplikacja3.broadcast.total";
    private void sendBroadcast(){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(downloadedBytes , mDownloadedBytes);
        intent.putExtra(totalBytes, mTotalSize);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
