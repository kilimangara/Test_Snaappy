package com.example.asus.test_snaappy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.asus.test_snaappy.URLConnHelper.ImageLoader;

public class FullImageActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        imageView = (ImageView) findViewById(R.id.full_image);
        String item = getIntent().getExtras().getString("url");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.getImage(item, imageView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
