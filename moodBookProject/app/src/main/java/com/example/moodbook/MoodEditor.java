package com.example.moodbook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class MoodEditor {

    // Date editor
    // for showing calendar so user could select a date
    public static void showCalendar(final Button view, CreateMoodActivity myActivity){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(myActivity,
                new DatePickerDialog.OnDateSetListener() {
            //sets formatted date on the button
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String formattedDate = String.format("%d-%02d-%02d", y, m+1, d);
                view.setText(formattedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // Time editor
    // for showing time so user could select a time
    public static void showTime(final Button view, CreateMoodActivity myActivity){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(myActivity,
                new TimePickerDialog.OnTimeSetListener() {
            //sets formatted time on the button
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                String formattedTime = String.format("%02d:%02d", h, m);
                view.setText(formattedTime);
            }
        }, hour, minute,false);
        timePickerDialog.show();
    }


    // Image editor
    // for setting a photo for the mood
    public static void setImage(CreateMoodActivity myActivity) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageIntent.resolveActivity(myActivity.getPackageManager()) != null) {
            myActivity.startActivityForResult(imageIntent, CreateMoodActivity.REQUEST_IMAGE);
        }
    }

    // gets the photo that was taken and let the image be shown in the page
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
