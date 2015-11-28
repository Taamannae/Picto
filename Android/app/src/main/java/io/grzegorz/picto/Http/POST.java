package io.grzegorz.picto.Http;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class POST extends AsyncTask<String, String, String> {
    private Activity activity;
    private TextView word;
    private TextView definition;
    private TextToSpeech tts;
    private ImageView resultImage;
    private RelativeLayout resultInfo;
    private RelativeLayout loading;

    private static boolean primary = true;
    private final String SERVER_URL = "http://picto.mybluemix.net/info/upload";
    private final String SERVER_URL_SECONDARY = "http://picto.mybluemix.net/upload";

    public POST(Activity activity, TextView word, TextView definition, TextToSpeech tts, ImageView resultImage, RelativeLayout resultInfo, RelativeLayout loading) {
        this.activity = activity;
        this.word = word;
        this.definition = definition;
        this.tts = tts;
        this.resultImage = resultImage;
        this.resultInfo = resultInfo;
        this.loading = loading;
    }

    protected String doInBackground(String... urls) {
        Bitmap bm = BitmapFactory.decodeFile(new File(this.activity.getExternalFilesDir(null), "capture.jpg").getPath());
        String fileName = "capture.jpg";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 0, bos);
        ContentBody contentPart = new ByteArrayBody(bos.toByteArray(), fileName);

        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("image", contentPart);

        String response;

        if (primary) {
            response = multipost(SERVER_URL, reqEntity);
        } else {
            response = multipost(SERVER_URL_SECONDARY, reqEntity);
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            System.out.println("Using secondary");
            primary = false;
            new POST(this.activity, this.word, this.definition, this.tts, this.resultImage, this.resultInfo, this.loading).execute();
        } else {
            primary = true;
            System.out.println(result);
            if (result.equals("NO_TAGS".trim())) {
                this.word.setText("Cannot identify.");
            } else {

                this.word.setText(result);

                String[] definitionParams = {result};
                new GET(GET.DEFINITION, definitionParams, this.word, this.definition, this.tts, this.resultImage, this.resultInfo, this.loading).execute();

            }
        }
    }

    private static String multipost(String urlString, MultipartEntity reqEntity) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

            OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            os.close();
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readStream(conn.getInputStream());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}