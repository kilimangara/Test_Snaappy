package com.example.asus.test_snaappy.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesHelper  {
    private static final String PREF_NAME = "valoidationPref";
    private static final String KEY = "bool";
    private static  final String IMAGES= "images";
    private static SharedPreferencesHelper instance;

    private SharedPreferences prefs;

    public SharedPreferencesHelper(){

    }
    public static SharedPreferencesHelper getInstance(){
        if(instance == null){
            instance = new SharedPreferencesHelper();
        }
        return instance;
    }

    public void init(Context context){
        this.prefs= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Saving flag to determine if urlImages is saved
     * @param bool
     *
     */
    public void putBoolean(boolean bool){
       prefs.edit().putBoolean(KEY, bool).apply();
    }
    public boolean getBoolean(){return prefs.getBoolean(KEY, false);}

    /**
     *
     * @param images
     * json String saving instance of Images model(urls which we save only once)
     */
    public void putImages(String images){
        prefs.edit().putString(IMAGES, images).apply();
    }
    public String getImages(){
        return prefs.getString(IMAGES,"");
    }
}
