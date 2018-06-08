package com.example.internadmin.fooddiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


public class ImageUploadTask extends AsyncTask<Void, Void, Bundle> {
    private WeakReference<Context> weakContext;
    private ProgressDialog progDialog;

    private Bitmap bmp;
    private String dstURL;
    private PostTaskListener<Bundle> postTaskListener;
    public static final String Result = "Result";


    ImageUploadTask(PostTaskListener<Bundle> postTaskListener, String addr, Bitmap bmp, Context ctx) {
        this.dstURL = addr;
        this.bmp = bmp;
        this.postTaskListener = postTaskListener;
        weakContext = new WeakReference<>(ctx);
        this.progDialog = new ProgressDialog(weakContext.get());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Predicting...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();
    }

    @Override
    protected Bundle doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        OutputStream outputStream;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr;
        Bundle b = new Bundle();

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            ContentBody contentPart = new ByteArrayBody(bos.toByteArray(), "image.jpg");

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("image", contentPart);

            URL url = new URL(dstURL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            urlConnection.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

            outputStream = urlConnection.getOutputStream();
            reqEntity.writeTo(outputStream);
            outputStream.close();


            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                b.putString(Result, "NullResponseError: No reply received.");
                return b;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            JsonParser parser = new JsonParser();
            JsonObject myJson = parser.parse(forecastJsonStr).getAsJsonObject();
            JsonArray Predictions = myJson.getAsJsonArray("Predictions");
            int noOfPredictions = Predictions.size();
            b.putInt("NoOfPredictions", noOfPredictions);

            for (int i = 0; i < noOfPredictions; i++){
                JsonObject prediction = Predictions.get(i).getAsJsonObject();
                Prediction mypredict = new Prediction(prediction.get("FoodName").getAsString(),
                                        prediction.get("ver").getAsInt());
                b.putSerializable(Integer.toString(i), mypredict);
            }

            return b;
        }catch (SocketTimeoutException e){
            b.putString(Result, "TOError: Request timed out.");
            return b;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            b.putString(Result, "IOError: Could not send file.");
            return b;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Bundle result) {
        super.onPostExecute(result);
        if(result != null && postTaskListener != null){
            postTaskListener.onPostTask(result);
        }
        if (progDialog.isShowing()) {
            progDialog.dismiss();
        }
    }
}