package com.example.asus.test_snaappy;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;

import com.example.asus.test_snaappy.PreferencesHelper.SharedPreferencesHelper;
import com.example.asus.test_snaappy.URLConnHelper.ImageLoader;
import com.example.asus.test_snaappy.adapter.GridViewAdapter;
import com.example.asus.test_snaappy.models.Images;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


//Если этот код работает, то его писал Злаин Никита
//а если нет, то не знаю , кто его писал
public class MainActivity extends AppCompatActivity implements OnFileLoadingListener {
    private static final String URL_FILE ="https://docs.google.com/uc?authuser=0&id=0B-yx9N-CwvshMi1Kc3pxbEJUdGM&export=download";
    public GridView gridView;//
    public Images images;
    public Gson gson;// Использую Gson для перевода json объекта в строку и обратно для сохранения в префах
    public ImageLoader imageLoader;
    public SharedPreferencesHelper preferencesHelper;


    @Override
    public void fileLoaded() {
        final GridViewAdapter adapter = new GridViewAdapter(images.getImages(), this);
        gridView.setAdapter(adapter);


    }

    @Override
    public void loadingError() {

    }
    public int getScreen(){
        return getResources().getConfiguration().orientation;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gson = new GsonBuilder().setPrettyPrinting().create();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = (GridView) findViewById(R.id.gridView1);
        if(getScreen()== Configuration.ORIENTATION_LANDSCAPE){
            gridView.setColumnWidth(gridView.getWidth()/2);
        }
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(this);
        preferencesHelper = SharedPreferencesHelper.getInstance();
        preferencesHelper.init(this);
        if(!preferencesHelper.getBoolean()) {
            new DownloadBaseFile(this).execute(URL_FILE);
        }
        else{
            images = gson.fromJson(preferencesHelper.getImages(), Images.class);
            fileLoaded();
        }

    }


    protected class DownloadBaseFile extends AsyncTask<String,String, String>{
        WeakReference<Context> context;
        OnFileLoadingListener listener ;

        DownloadBaseFile(Context context){
            this.context = new WeakReference<>(context);
            listener = (OnFileLoadingListener) context;
        }

        @Override
        protected void onPreExecute() {
            if(listener == null){
                listener = (OnFileLoadingListener) context.get();
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if(aVoid != null) {
                preferencesHelper.putBoolean(true);
                preferencesHelper.putImages(aVoid);
                images = gson.fromJson(aVoid, Images.class);

                listener.fileLoaded();
            }
            else{
                images = null;
            }
            listener = null;
        }

        @Override
        protected String doInBackground(String... params) {
            byte buffer[];
            String ret;
            try{
                URL ulrn = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                buffer = new byte[con.getContentLength()];
                is.read(buffer);
                ret = new String(buffer, "UTF-8");
                    return ret;

            }catch(Exception e){
                e.printStackTrace();

            }
            return null;
        }
    }

}
