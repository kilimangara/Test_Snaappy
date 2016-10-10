package com.example.asus.test_snaappy.URLConnHelper;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.example.asus.test_snaappy.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {
    private static ImageLoader instance;
   // private final Object mDiscCacheLock = new Object();
   // private boolean mDiscCaheReady = false ;

    private Context  context;

    private LruCache<String, Bitmap> cache;
    public ImageLoader(){
    }

    public static ImageLoader getInstance(){
        if(instance ==  null){
            instance = new ImageLoader();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
       if(cache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cachesize = maxMemory / 8;

            cache = new LruCache<String, Bitmap>(cachesize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024;
                }
            };

        }
    }

    /**
     * putting picture from internet into definite View
     * @param url
     * url of the picture
     * @param imageView
     *into which imageView or its child Views picture will be inserted
     */
    public void getImage(String url, ImageView imageView){
        AsyncDownloading asyncDownloading = new AsyncDownloading(imageView);
        asyncDownloading.execute(url);

    }

    /**
     * putting picture from internet into definite View  with definite size
     * @param url
     * url of the picture
     * @param imageView
     * into which imageView or its child Views picture will be inserted
     * @param reqHeight
     * required height of result picture
     * @param reqWidth
     * required width of result picture
     */
    public void getImage(String url, ImageView imageView, int reqHeight, int reqWidth) {

        AsyncDownloading asyncDownloading = new AsyncDownloading(imageView, reqHeight, reqWidth);
        asyncDownloading.execute(url);

    }

    protected class AsyncDownloading extends AsyncTask<String, Void, Bitmap >{
        ImageView imageViewWeakReference;
        boolean flag;
        int height;
        int width;
        public AsyncDownloading(ImageView imageView){
            imageViewWeakReference = imageView;
            height=0;
            width = 0;
            flag = false;
        }
        public AsyncDownloading(ImageView imageView, int reqHeight, int reqWidth){
            imageViewWeakReference = imageView;
            height =reqHeight;
            width = reqWidth;
            flag = true;

        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            imageViewWeakReference = null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            imageViewWeakReference.setImageDrawable(imageViewWeakReference.getResources().getDrawable(R.drawable.progress));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(flag & height != 0 & width != 0) {
                imageViewWeakReference.setImageBitmap(getScaledBitmap(bitmap,width,height));
            }
            else{
                imageViewWeakReference.setImageBitmap(bitmap);
            }
            imageViewWeakReference = null;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap;
          if(cache.get(params[0]) == null){
              if( (bitmap = getBitmapFromDiscCache(url)) != null){
                  cache.put(url,bitmap);
                   return bitmap;
              }else {
                  bitmap = loadBitmap(params[0]);
                  if (bitmap != null) {
                      cache.put(params[0], bitmap);
                      addBitmapToDiscCache(url, bitmap);
                  }
                  return bitmap;
              }
          }else{
              return cache.get(params[0]);
          }

        }
        private void addBitmapToDiscCache(String url, Bitmap bitmap)  {
            String name = Uri.parse(url).getLastPathSegment();
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        @Nullable
        private Bitmap getBitmapFromDiscCache(String url){
            String name = Uri.parse(url).getLastPathSegment();
            Bitmap bitmap;
            try {
                FileInputStream fileInputStream = context.openFileInput(name);
                bitmap= BitmapFactory.decodeStream(fileInputStream);
                fileInputStream.close();
                return bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
        @Nullable
        private Bitmap loadBitmap(String url){
            Bitmap bmp =null;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp= BitmapFactory.decodeStream(is);
                is.close();
                if (bmp != null)
                    return bmp;

            }catch(Exception e){
                e.getMessage();
            }
            return bmp;
        }

        /**
         *
         * @param original
         * original Bitmap, which is supposed to have scaled variant
         * @param reqWidth
         * required width
         * @param reqHeight
         * required height
         * @return scaled variant of original Bitmap;
         * (This one actually doesn't work, maybe implementation would be more appropriate with usage
         * of BitmapFactory.Options in parameters instead of Bitmap original)
         */
        public  Bitmap getScaledBitmap(Bitmap original, int reqWidth, int reqHeight) {
            int inSampleSize = 1;
            int height = original.getHeight();
            int width = original.getWidth();

            if (height > reqHeight || width > reqWidth) {

                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            return Bitmap.createScaledBitmap(original, width/inSampleSize, height/inSampleSize, false);
        }
    }
}
