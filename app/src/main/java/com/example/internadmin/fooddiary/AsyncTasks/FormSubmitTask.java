package com.example.internadmin.fooddiary.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.internadmin.fooddiary.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class FormSubmitTask extends AsyncTask<Void, Void, Bundle> {
    private String dstURL;
    private WeakReference<Context> weakContext;
    private BufferedReader reader = null;
    private WeakReference<ProgressDialog> progDialogref;
    public static final String Result = "Result";
    public static final String Success = "Success";
    private String FileName;
    Context context;
    private String CorrectPrediction;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public FormSubmitTask(String FileName, String CorrectPrediction, Context context){
        dstURL = Config.submitformAddress;
        this.context = context;
        this.FileName = FileName;
        weakContext = new WeakReference<>(context);
        this.CorrectPrediction = CorrectPrediction;
        Log.d("CName: Task", CorrectPrediction);
        this.progDialogref = new WeakReference<>(new ProgressDialog(weakContext.get()));
    }

    @Override
    protected Bundle doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        DataOutputStream outputStream;
        Bundle b = new Bundle();
        StringBuilder buffer = new StringBuilder();
        try {
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
            JSONObject objout = new JSONObject();
            objout.put("filename", FileName);
            objout.put("correct", CorrectPrediction);


            outputStream = new DataOutputStream(urlConnection.getOutputStream());
            //OutputStreamWriter osw = new OutputStreamWriter(outputStream, "UTF-8");
            outputStream.writeBytes("data={\"filename\":\"" + FileName + "\",\"correct\":\""+CorrectPrediction + "\"}");
            //osw.write(objout.toString());
            //osw.flush();
            //osw.close();
            outputStream.flush();
            outputStream.close();

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

        } catch(SocketTimeoutException e){
            b.putString(Result, "TOError: Request timed out.");
            return b;
        } catch(IOException e){
            b.putString(Result, "IOError: Could not submit form.");
            return b;
        } catch (JSONException e) {
            b.putString(Result, "JSON Error: could not create json");
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

        JSONObject FromServer;
        try {
            FromServer = new JSONObject(buffer.toString());
            String Response = FromServer.getString("success");

            if (Response.equals("true")) {

                b.putString(Result, Success);
                return b;
            } else {
                b.putString(Result, "fail");
                return b;
            }
        }catch(JSONException e){
            b.putString(Result, "ServerError: Could not parse data from server.");
            return b;
        }
    }
    @Override
    protected void onPostExecute(Bundle result) {
        super.onPostExecute(result);
    }
}
