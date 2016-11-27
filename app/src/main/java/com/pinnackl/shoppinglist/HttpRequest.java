package com.pinnackl.shoppinglist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest extends AsyncTask<String, String, String> {
    HttpURLConnection urlConnection;

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();
        int count = args.length;
        String parameters = "?";
        for (int i = 0; i < count; i++) {
            parameters += args[i];
        }
        Log.d("Plop", "nb args: " + count);
        Log.d("Plop", "parameters : " + parameters);
        try {
            URL url = new URL("http://appspaces.fr/esgi/shopping_list/account/subscribe.php"+parameters);
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
    }
}
