package com.example.aplikacja3;
import android.os.AsyncTask;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class DownloadInfo extends AsyncTask<String,Void,InfoWrapper> {
    public AsyncResponse delegate;

   public DownloadInfo(AsyncResponse delegate){
       this.delegate = delegate;
   }
    @Override
    protected InfoWrapper doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        InfoWrapper result = new InfoWrapper();
        try {
            String urlAddress = strings[0];
            URL url = new URL(urlAddress);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            result.size = connection.getContentLength();
            result.type = connection.getContentType();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(InfoWrapper infoWrapper) {
        delegate.setInfoResult(infoWrapper);
    }
}
