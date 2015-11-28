package io.grzegorz.picto.Http;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private RelativeLayout loading;

    private ImageView resultImage;
    private RelativeLayout resultInfo;

    private String SERVER_URL = "http://picto.mybluemix.net/";

    public GET(int request, String[] params, TextView word, TextView definition, TextToSpeech tts, ImageView resultImage, RelativeLayout resultInfo, RelativeLayout loading) {
        this.request = request;
        this.params = params;
        this.definition = definition;
        this.tts = tts;
        this.resultImage = resultImage;
        this.resultInfo = resultInfo;
        this.word = word;
        this.loading = loading;
    }

    protected String doInBackground(String... urls) {

        if (this.request == DEFINITION) {
            try {
                SERVER_URL += "info/definition?word=" + URLEncoder.encode(params[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "Error";
            }
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
            this.loading.setVisibility(View.GONE);
            this.resultImage.setVisibility(View.VISIBLE);
            this.resultInfo.setVisibility(View.VISIBLE);
        } else if (this.request == TRANSLATE && result != "Error") {
            if (this.params[3] == "WORD") {
                this.word.setText(result);
            } else if (this.params[3] == "DEFINITION") {
                this.definition.setText(result);
                this.loading.setVisibility(View.GONE);
                this.resultImage.setVisibility(View.VISIBLE);
                this.resultInfo.setVisibility(View.VISIBLE);
            }
        } else {
            // show err layout
        }
        System.out.println(result);
    }
}