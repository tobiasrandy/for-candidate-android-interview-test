package com.tokopedia.testproject.problems.androidView.slidingImagePuzzle;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.UtilKt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Url;

public class Solution {
    public interface onSuccessLoadBitmap{
        void onSliceSuccess(List<Bitmap> bitmapList);
        void onSliceFailed(Throwable throwable);
    }
    public static void sliceTo4x4(Context context, onSuccessLoadBitmap onSuccessLoadBitmap, String imageUrl) {
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        // TODO, download the image, crop, then sliced to 15 Bitmap (4x4 Bitmap). ignore the last Bitmap
        // below is stub, replace with your implementation!

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Downloading image file...");

        URL url = null;
        try{
            url = new URL(imageUrl);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        new DownloadTask(context, onSuccessLoadBitmap, progressDialog).execute(url);
    }

    private static class DownloadTask extends AsyncTask<URL, Void, Bitmap> {
        Context context;
        onSuccessLoadBitmap onSuccessLoadBitmap;
        ProgressDialog progressDialog;

        public DownloadTask(Context context, onSuccessLoadBitmap onSuccessLoadBitmap, ProgressDialog progressDialog) {
            this.context = context;
            this.onSuccessLoadBitmap = onSuccessLoadBitmap;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                return bmp;
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressDialog.dismiss();
            ArrayList<Bitmap> bitmapList = new ArrayList<>();
            if(bitmap != null){
               bitmap = compressBitmap(bitmap);

               //check if bitmap is a square
               if(bitmap.getWidth() != bitmap.getHeight()){
                   //if not a square, crop it
                   bitmap = cropBitmap(bitmap);
               }
               //validate bitmap resolution. min 400x400 px
               if(bitmap.getHeight() < 400 || bitmap.getWidth() < 400){
                   bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
               }

               onSuccessLoadBitmap.onSliceSuccess(sliceBitmap(bitmap));
            }
            else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT);
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample11));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample12));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample13));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample14));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample21));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample22));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample23));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample24));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample31));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample32));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample33));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample34));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample41));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample42));
                bitmapList.add(UtilKt.toBitmap(context, R.drawable.sample43));
                bitmapList.add(Bitmap.createBitmap(150, 150, Bitmap.Config.ALPHA_8));
                onSuccessLoadBitmap.onSliceSuccess(bitmapList);
            }
        }
    }

    public static Bitmap compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return bitmap;
    }

    public static Bitmap cropBitmap(Bitmap bitmap){
        Bitmap croppedBitmap;
        if (bitmap.getWidth() >= bitmap.getHeight()){
            croppedBitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2 - bitmap.getHeight()/2, 0, bitmap.getHeight(), bitmap.getHeight());
        }else{
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2 - bitmap.getWidth()/2, bitmap.getWidth(), bitmap.getWidth());
        }
        return croppedBitmap;
    }

    public static ArrayList<Bitmap> sliceBitmap(Bitmap bitmap){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        int tileWidth = bitmap.getWidth() / 4;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Bitmap tile = Bitmap.createBitmap(bitmap, j * tileWidth, i * tileWidth, tileWidth, tileWidth);
                bitmaps.add(tile);
            }
        }
        bitmaps.remove(bitmaps.size()-1);
        bitmaps.add(Bitmap.createBitmap(tileWidth, tileWidth, Bitmap.Config.ALPHA_8));
        return bitmaps;
    }

    public static int dpToPx(int dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static float pxToDp(float px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, metrics);
    }
}
