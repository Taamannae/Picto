package io.grzegorz.picto.Main;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import io.grzegorz.picto.R;


public class MainActivity extends AppCompatActivity {

    private static SectionsPagerAdapter adapter;
    private static ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.container);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager(), 1);
        viewPager.setAdapter(adapter);

    }

    public static SectionsPagerAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 0 && SectionsPagerAdapter.getResultMode()) {
            SectionsPagerAdapter.setResultMode(false);
            MainActivity.getAdapter().notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }

    }

}
