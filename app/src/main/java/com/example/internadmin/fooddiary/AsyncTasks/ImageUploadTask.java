package com.example.internadmin.fooddiary.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.internadmin.fooddiary.Config;
import com.example.internadmin.fooddiary.Interfaces.PostTaskListener;
import com.example.internadmin.fooddiary.Models.Prediction;
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

/**
 * An AsyncTask which uploads image to server,
 * and returns the predictions in a bundle.
 *
 * Used by CameraActivity.
 * Sends image to URL in Config.getpredictionAddress.
 * Uses HTTP POST request for sending image.
 * Receives the prediction results as a JSONObject, and
 * converts the predictions in the JSONObject into Prediction
 * model objects.
 *
 * The Prediction objects are serialized an put into a bundle,
 * which are then returned to CameraActivity.
 */

public class ImageUploadTask extends AsyncTask<Void, Void, Bundle> {
    private WeakReference<Context> weakContext;
    //private ProgressDialog progDialog;

    private Bitmap bmp;
    private String dstURL;
    private PostTaskListener<Bundle> postTaskListener;
    public static final String Result = "Result";
    public static final String filename = "filename";
    public static final String Success = "Success";
    public static final String noofpredictions = "NoOfPredictions";

    //Requires a PostTaskListener, bitmap for sending, and the context object
    //(Context currently unused as ProgressDialog was removed)
    public ImageUploadTask(PostTaskListener<Bundle> postTaskListener, Bitmap bmp, Context ctx) {
        this.dstURL = Config.getpredictionAddress;
        this.bmp = bmp;
        this.postTaskListener = postTaskListener;
        weakContext = new WeakReference<>(ctx);
        //this.progDialog = new ProgressDialog(weakContext.get());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*
        progDialog.setMessage("Predicting...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();*/
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

            //Compresses image into a 255*255 image to be sent to server
            //Puts the image into a ByteArrayOutputStream
            //POST request used to send image.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap.createScaledBitmap(bmp, 255, 255, false).compress(Bitmap.CompressFormat.JPEG, 100, bos);
            ContentBody contentPart = new ByteArrayBody(bos.toByteArray(), "image.jpg");

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("image", contentPart);

            URL url = new URL(dstURL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
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
                b.putString(Result, "Empty Buffer");
                return b;
            }
            forecastJsonStr = buffer.toString();

            //Input Stream String is converted into a JsonObject for processing.
            JsonParser parser = new JsonParser();
            JsonObject myJson = parser.parse(forecastJsonStr).getAsJsonObject();

            //Check if server replies that the result is successful.
            //If unsuccessful, prediction objects are not created,
            //and only result is returned in bundle.
            String myresult = myJson.get(Result).getAsString();

            if(myresult.equals(Success)){
                b.putString(Result, Success);
                b.putString(filename, myJson.get(filename).getAsString());
                Log.d("FILENAME", myJson.get(filename).getAsString());
                JsonArray Predictions = myJson.getAsJsonArray("Predictions");
                int noOfPredictions = Predictions.size();
                b.putInt(noofpredictions, noOfPredictions);

                for (int i = 0; i < noOfPredictions; i++){
                    JsonObject prediction = Predictions.get(i).getAsJsonObject();
                    Prediction mypredict = new Prediction(prediction.get("FoodName").getAsString(),
                            prediction.get("ver").getAsInt());
                    b.putSerializable(Integer.toString(i), mypredict);
                }
            }else{
                b.putString(Result, myresult);
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
        }/*
        if (progDialog.isShowing()) {
            progDialog.dismiss();
        }*/
    }
}