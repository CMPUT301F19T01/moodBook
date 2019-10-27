package com.example.moodbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class MoodEditor {

    // for setting a photo for the mood
    public static void setImage(View view, CreateMoodActivity myActivity) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageIntent.resolveActivity(myActivity.getPackageManager()) != null) {
            myActivity.startActivityForResult(imageIntent, CreateMoodActivity.REQUEST_IMAGE);
        }
    }

    public static void getImageResult(int requestCode, int resultCode, @Nullable Intent data,
                                      ImageView image_view_photo) {
        if (requestCode == CreateMoodActivity.REQUEST_IMAGE
                && resultCode == CreateMoodActivity.RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_view_photo.setImageBitmap(imageBitmap);
        }
        else {

        }
    }
}
