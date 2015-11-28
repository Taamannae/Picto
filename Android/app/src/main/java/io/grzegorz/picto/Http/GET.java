package io.grzegorz.picto.Http;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


public class GET extends AsyncTask<String, String, String> {

    public static final int DEFINITION = 0;
    public static final int TRANSLATE = 1;

    private int request;
    private String[] params;

    private TextView definition;
    private TextToSpeech tts;

    private String SERVER_URL = "http://picto.mybluemix.net/";

    public GET(int request, String[] params, TextView definition, TextToSpeech tts) {
        this.request = request;
        this.params = params;
        this.definition = definition;
        this.tts = tts;
    }

    protected String doInBackground(String... urls) {

        if (this.request == DEFINITION) {
            SERVER_URL += "info/definition?word=" + params[0];
            System.out.println(SERVER_URL);
        } else if (this.request == TRANSLATE) {
            SERVER_URL += "info/translate?text=" + params[0] + "&target=" + params[1];
        }  else {
            return "Invalid request";
        }

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            System.out.println(conn.getResponseCode());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String input;

            StringBuffer response = new StringBuffer();

            while ((input = in.readLine()) != null) {
                response.append(input);
            }

            in.close();

            return response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Error";
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
        if (this.request == DEFINITION) {
            this.definition.setText(result);
        } else if (this.request == TRANSLATE) {
            // set the new language here
            //tts.setLanguage(Locale.CHINESE);
        }
        System.out.println(result);
    }
}