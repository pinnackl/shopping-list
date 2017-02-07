package com.pinnackl.shoppinglist;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pinnackl.shoppinglist.request.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpRequest extends AsyncTask<Request, Void, String> {
    HttpURLConnection urlConnection;
    private static Context context;
    private IHttpRequestListener listener;

    public HttpRequest(Context c) {
        this.context = c;
    }

    @Override
    protected String doInBackground(Request... requestObject) {

        StringBuilder result = new StringBuilder();
        HashMap<String, String> parametersList = requestObject[0].getParameters();
        String endpoint = requestObject[0].getEndpoint();

        String parameters = "?";
        for (HashMap.Entry<String, String> entry : parametersList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(!parameters.equals("?")) {
                parameters += "&";
            }
            parameters += key + "=" + value;
        }

        try {
            URL url = new URL("http://appspaces.fr/esgi/shopping_list"+endpoint+parameters);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        //Do something with the JSON string
        Log.d("Plop", "response: " + result);
        try {
            JSONObject jsonObj = new JSONObject(result.toString());

            if(jsonObj.getString("code").equals("0")) {
                if(listener != null) {
                    listener.onSuccess(jsonObj);
                }
            } else {
                if(listener != null) {
                    listener.onFailure(jsonObj.getString("msg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public IHttpRequestListener getListener() {
        return listener;
    }

    public void setListener(IHttpRequestListener listener) {
        this.listener = listener;
    }
}
