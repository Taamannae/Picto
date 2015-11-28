package io.grzegorz.picto.Http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GET extends AsyncTask<String, String, String> {

    public static final int DEFINITION = 0;
    public static final int TRANSLATE = 1;
    public static final int SPEECH = 2;

    private int request;
    private String[] params;

    private String SERVER_URL = "http://picto.mybluemix.net/";

    public GET(int request, String[] params) {
        this.request = request;
        this.params = params;
    }

    protected String doInBackground(String... urls) {

        if (this.request == DEFINITION) {
            SERVER_URL += "definition?word=" + params[0];
        } else if (this.request == TRANSLATE) {
            SERVER_URL += "translate?text=" + params[0] + "&target=" + params[1];
        } else if (this.request == SPEECH) {
            SERVER_URL += "speech?word=" + params[0];
        } else {
            return "Invalid request.";
        }

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            //int response = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String input;

            StringBuffer response = new StringBuffer();

            while ((input = in.readLine()) != null) {
                response.append(input);
            }

            in.close();

            return input;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Error.";
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
    }
}