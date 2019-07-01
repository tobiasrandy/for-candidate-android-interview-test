package com.tokopedia.testproject.problems.androidView.slidingImagePuzzle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tokopedia.testproject.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SlidingImageGameActivity extends AppCompatActivity {
    public static final String X_IMAGE_URL = "x_image_url";
    public static final int GRID_NO = 4;
    private String imageUrl;
    ImageView[][] imageViews = new ImageView[4][4];
    private GridLayout gridLayout;
    ArrayList<Bitmap> initialBitmaps = new ArrayList<>();
    ArrayList<Bitmap> savedBitmaps = new ArrayList<>();
    Bitmap emptyBitmap;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };

    public static Intent getIntent(Context context, String imageUrl) {
        Intent intent = new Intent(context, SlidingImageGameActivity.class);
        intent.putExtra(X_IMAGE_URL, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getIntent().getStringExtra(X_IMAGE_URL);
        setContentView(R.layout.activity_sliding_image_game);
        gridLayout = findViewById(R.id.gridLayout);

        if(savedInstanceState != null){
            initialBitmaps.addAll(savedInstanceState.<Bitmap>getParcelableArrayList("initialBitmaps"));
            savedBitmaps.addAll(savedInstanceState.<Bitmap>getParcelableArrayList("savedBitmaps"));
            emptyBitmap = savedInstanceState.getParcelable("emptyBitmap");

        }
        else{

        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                final ImageView view = (ImageView) inflater.inflate(R.layout.item_image_sliding_image,
                        gridLayout, false);

                gridLayout.addView(view);
                imageViews[i][j] = view;

                final int finalI = i;
                final int finalJ = j;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("koordinat", finalI + "," +finalJ);

                        for (int[] delta : NEIGHBOUR_COORDS){
                            int nullX = finalI + delta[0];
                            int nullY = finalJ + delta[1];
                            if (nullX >= 0 && nullX < GRID_NO && nullY >= 0 && nullY < GRID_NO){
                                if(((BitmapDrawable)imageViews[nullX][nullY].getDrawable()).getBitmap().sameAs(emptyBitmap)){
                                    Bitmap temp = ((BitmapDrawable)imageViews[finalI][finalJ].getDrawable()).getBitmap();
                                    imageViews[finalI][finalJ].setImageBitmap(emptyBitmap);
                                    imageViews[nullX][nullY].setImageBitmap(temp);

                                    if(isSolved()){
                                        Toast.makeText(getApplicationContext(), "Congratulations, you just solved the puzzle!", Toast.LENGTH_SHORT).show();
                                    }

                                    return;
                                }
                            }
                        }
                    }
                });
            }
        }

        if(emptyBitmap == null) {
            Solution.sliceTo4x4(this, new Solution.onSuccessLoadBitmap() {
                @Override
                public void onSliceSuccess(List<Bitmap> bitmapList) {
                    emptyBitmap = bitmapList.get(bitmapList.size() - 1);
                    initialBitmaps.addAll(bitmapList);
                    //TODO will randomize placement to grid. Note: the game must be solvable.
                    //isSolveable?
                    Collections.shuffle(bitmapList);
//                    shuffleTiles(3000);
                    //replace below implementation to your implementation.
                    int counter = 0;
                    int bitmapSize = bitmapList.size();
                    for (int i = 0; i < GRID_NO; i++) {
                        for (int j = 0; j < GRID_NO; j++) {
                            if (counter >= bitmapSize) break;
                            imageViews[i][j].setImageBitmap(bitmapList.get(counter));
                            counter++;
                        }
                        if (counter >= bitmapSize) break;
                    }
                }

                @Override
                public void onSliceFailed(Throwable throwable) {
                    Toast.makeText(SlidingImageGameActivity.this,
                            throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, imageUrl);
        }
        else{
            int counter = 0;
            int bitmapSize = savedBitmaps.size();
            for (int i = 0; i < GRID_NO; i++) {
                for (int j = 0; j < GRID_NO; j++) {
                    if (counter >= bitmapSize) break;
                    imageViews[i][j].setImageBitmap(savedBitmaps.get(counter));
                    counter++;
                }
                if (counter >= bitmapSize) break;
            }
        }

        // TODO add implementation of the game.
        // There is image adjacent to blank space (either horizontal or vertical).
        // If that image is clicked, it will swap to the blank space
        // if the puzzle is solved (the image in the view is aligned with the original image), then show a "success" dialog

        // TODO add handling for rotation to save the user input.
        // If the device is rotated, it should retain user's input, so user can continue the game.
    }

    public boolean isSolved(){
        int counter = 0;
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                if(!((BitmapDrawable)imageViews[i][j].getDrawable()).getBitmap().sameAs(initialBitmaps.get(counter))) {
                    return false;
                }
                counter++;
            }
        }
        return true;
    }

    public void saveImageViews() {
        try {
            savedBitmaps.clear();
            for (int i = 0; i < GRID_NO; i++) {
                for (int j = 0; j < GRID_NO; j++) {
                    savedBitmaps.add(((BitmapDrawable) imageViews[i][j].getDrawable()).getBitmap());
                }
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

//    public void shuffleTiles(int moves){
//        Random r = new Random();
//        for (int i = 0; i < moves; i++) {
//            int randomI = r.nextInt(4);
//            int randomJ = r.nextInt(4);
//            imageViews[randomI][randomJ].performClick();
//        }
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveImageViews();
        outState.putParcelableArrayList("initialBitmaps", initialBitmaps);
        outState.putParcelableArrayList("savedBitmaps", savedBitmaps);
        outState.putParcelable("emptyBitmap", emptyBitmap);

        super.onSaveInstanceState(outState);
    }
}
