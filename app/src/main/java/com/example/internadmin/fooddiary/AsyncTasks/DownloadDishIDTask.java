package com.example.internadmin.fooddiary.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import com.example.internadmin.fooddiary.Config;
import com.example.internadmin.fooddiary.Interfaces.PostTaskListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * An AsyncTask which downloads the DishID information from the server.
 *
 * It is used by DishID model, under method populateFromOnline.
 * It makes a POST HTTP request to the server, using the address in
 * Config.getinfoAddress.
 *
 * It returns a bundle onPostExecute, which is used by DishID model
 * to populate the required variables.
 */

public class DownloadDishIDTask extends AsyncTask<Void, Void, Bundle> {

    private String dstURL;
    private BufferedReader reader = null;
    private WeakReference<Context> weakContext;
    private String FoodName;
    public static final String Result = "Result";
    public static final String Success = "Success";
    private WeakReference<ProgressDialog> progDialogref;
    private PostTaskListener<Bundle> ptl;

    //Requires a PostTaskListener(implemented by DishID), context, and FoodName to request from
    //server.
    public DownloadDishIDTask(PostTaskListener<Bundle> ptl, Context context, String FoodName) {
        this.dstURL = Config.getinfoAddress;
        weakContext = new WeakReference<>(context);
        this.FoodName = FoodName;
        this.ptl = ptl;
        this.progDialogref = new WeakReference<>(new ProgressDialog(weakContext.get()));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog progDialog = progDialogref.get();
        progDialog.setMessage("Downloading Resources...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();
    }


    @Override
    protected Bundle doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        DataOutputStream outputStream;

        StringBuilder buffer = new StringBuilder();
        Bundle b = new Bundle();



        try {
            //Send JSON to server
            URL url = new URL(dstURL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            //urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.connect();

            outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes("dishname=" + FoodName);

            outputStream.flush();
            outputStream.close();

            // Read the input stream from dishes.txt into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                b.putString(Result, "NullError: Null Response Received. (InputStream = Null)");
                return b;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                b.putString(Result, "NullError: Null Response Received. (BufferLength = 0)");
                return b;
            }

        }catch (SocketTimeoutException e){
            b.putString(Result, "TOError: Request timed out.");
            return b;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error " + e.getMessage());
            b.putString(Result, "IOError: Could not download dishes.");
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

        //Convert String from server to JSON
        //If update required, update Dishes JSON and store back into dishes.txt
        JSONObject FromServer;

        try {
            FromServer = new JSONObject(buffer.toString());
            String Response = FromServer.getString("Response");

            if (Response.equals(Success)) {

                b.putString(Result, Success);
                b.putString("FoodName", FoodName);
                b.putInt("Version", FromServer.getInt("version"));
                b.putSerializable("FoodImg",
                        GetImageFromURL(FoodName, FromServer.getString("image_url")));
                b.putString("Nutrition", FromServer.getJSONObject("nutrition").toString());
                b.putString("Ingredients", FromServer.getJSONArray("ingredients").toString());

                //Write JSON back to file
                return b;
            } else {
                b.putString(Result, Response);
                return b;
            }
        }catch(JSONException e){
            Log.e("DLDishTask", "unexpected JSON exception", e);
            b.putString(Result, "ServerError: Could not parse data from server.");
            return b;
        }
    }

    //Once Task is completed, the progress dialog is dismissed, and the result returned to the
    //PostTaskListener.
    @Override
    protected void onPostExecute(Bundle result) {
        super.onPostExecute(result);
        if(result != null && ptl != null){
            ptl.onPostTask(result);
        }
        if (progDialogref.get() != null && progDialogref.get().isShowing()) {
            progDialogref.get().dismiss();
        }
    }

    //Method which gets URL from the JSON returned from server
    //Opens image as bitmap and saves it into private directory.
    private File GetImageFromURL(String FoodName, String src) {

        FileOutputStream fos = null;
        File file;
        try{
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            ContextWrapper cw = new ContextWrapper(weakContext.get());
            File directory = cw.getDir("DishIDDir", Context.MODE_PRIVATE);
            file = new File(directory, "DishID" + FoodName + ".jpg");
            fos = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;

    }


}

