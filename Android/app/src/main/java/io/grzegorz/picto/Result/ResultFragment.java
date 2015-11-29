package io.grzegorz.picto.Result;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import io.grzegorz.picto.Http.GET;
import io.grzegorz.picto.Http.POST;
import io.grzegorz.picto.Main.MainActivity;
import io.grzegorz.picto.Main.SectionsPagerAdapter;
import io.grzegorz.picto.R;


public class ResultFragment extends Fragment {

    private ImageView userImage;
    private ImageButton backToCamera;
    private Button languageSelect;
    private TextView word;
    private TextView definition;
    private ImageButton speech;
    private TextToSpeech tts;
    private ImageView resultImage;
    private RelativeLayout resultInfo;
    private RelativeLayout loading;
    private ImageView pulse;
    private static ArrayList<String> languages;
    private static String currentLang = "English";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.result_fragment, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        return view;

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        loading = (RelativeLayout) view.findViewById(R.id.loading);

        pulse = (ImageView) view.findViewById(R.id.pulse);
        Ion.with(pulse).load("android.resource://io.grzegorz.picto/" + R.drawable.ripple);

        compressImage();

        File imageFile = new File(getActivity().getExternalFilesDir(null), "capture2.jpg");

        resultImage = (ImageView) view.findViewById(R.id.resultImage);
        resultInfo = (RelativeLayout) view.findViewById(R.id.resultInfo);

        userImage = (ImageView) view.findViewById(R.id.resultImage);

        if (imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());


            userImage.setImageBitmap(bitmap);
        }

        backToCamera = (ImageButton) view.findViewById(R.id.backToCamera);

        backToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SectionsPagerAdapter.setResultMode(false);
                MainActivity.getAdapter().notifyDataSetChanged();
            }
        });

        word = (TextView) view.findViewById(R.id.word);
        definition = (TextView) view.findViewById(R.id.definition);
        definition.setMovementMethod(new ScrollingMovementMethod());

        languageSelect = (Button) view.findViewById(R.id.languageSelect);

        // For now we just support english
        languageSelect.setText("English");

        languages = new ArrayList<String>();
        languages.add("English");
        languages.add("French");

        final DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(getActivity(), languageSelect);
        droppyBuilder.addMenuItem(new DroppyMenuItem("English")).addMenuItem(new DroppyMenuItem("French"));

        // Set Callback handler
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {


                if (!languages.get(id).equals(currentLang)) {
                    resultImage.setVisibility(View.GONE);
                    resultInfo.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    languageSelect.setText(languages.get(id));

                    if (languages.get(id).equals("English")) {
                        tts.setLanguage(Locale.ENGLISH);

                        String[] paramsWord = {word.getText().toString(), "en", currentLang, "WORD"};
                        new GET(GET.TRANSLATE, paramsWord, word, definition, resultImage, resultInfo, loading).execute();

                        String[] paramsDefinition = {definition.getText().toString(), "en", currentLang, "DEFINITION"};
                        new GET(GET.TRANSLATE, paramsDefinition, word, definition, resultImage, resultInfo, loading).execute();

                        currentLang = "English";

                    } else if (languages.get(id).equals("French")) {
                        tts.setLanguage(Locale.FRENCH);

                        String[] paramsWord = {word.getText().toString(), "fr", currentLang, "WORD"};
                        new GET(GET.TRANSLATE, paramsWord, word, definition, resultImage, resultInfo, loading).execute();

                        String[] paramsDefinition = {definition.getText().toString(), "fr", currentLang, "DEFINITION"};
                        new GET(GET.TRANSLATE, paramsDefinition, word, definition, resultImage, resultInfo, loading).execute();

                        currentLang = "French";

                    }
                }

            }
        });

        DroppyMenuPopup droppyMenu = droppyBuilder.build();

        tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    tts.setLanguage(Locale.ENGLISH);
            }
        });


        speech = (ImageButton) view.findViewById(R.id.speech);

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(word.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        new POST(getActivity(), this.word, this.definition, this.resultImage, this.resultInfo, this.loading).execute();

    }


    public String compressImage() {

        String filePath = new File(getActivity().getExternalFilesDir(null), "capture.jpg").getPath();
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        // max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        // setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        // check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out;
        String filename = new File(getActivity().getExternalFilesDir(null), "capture2.jpg").getPath();
        try {
            out = new FileOutputStream(filename);

            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


}
