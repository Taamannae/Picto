package io.grzegorz.picto.Http;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;


public class GET extends AsyncTask<String, String, String> {

    public static final int DEFINITION = 0;
    public static final int TRANSLATE = 1;

    private int request;
    private String[] params;

    private TextView definition;
    private TextToSpeech tts;
    private TextView word;

    private LinearLayout resultLayout;

    private String SERVER_URL = "http://picto.mybluemix.net/";

    public GET(int request, String[] params, TextView word, TextView definition, TextToSpeech tts, LinearLayout resultLayout) {
        this.request = request;
        this.params = params;
        this.definition = definition;
        this.tts = tts;
        this.resultLayout = resultLayout;
        this.word = word;
    }

    protected String doInBackground(String... urls) {

        if (this.request == DEFINITION) {
            SERVER_URL += "info/definition?word=" + params[0];
            System.out.println(SERVER_URL);
        } else if (this.request == TRANSLATE) {
            try {
                SERVER_URL += "info/translate?text=" + URLEncoder.encode(params[0], "utf-8") + "&target=" + params[1] + "&source=" + params[2];
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "Error";
            }
        }  else {
            return "Error";
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
        if (this.request == DEFINITION && result != "Error") {
            this.definition.setText(result);
            this.resultLayout.setVisibility(View.VISIBLE);
        } else if (this.request == TRANSLATE && result != "Error") {
            if (this.params[3] == "WORD") {
                this.word.setText(result);
            } else if (this.params[3] == "DEFINITION") {
                this.definition.setText(result);
                this.resultLayout.setVisibility(View.VISIBLE);
            }
            // set the new language here
        } else {
            // show err layout
        }
        System.out.println(result);
    }
}