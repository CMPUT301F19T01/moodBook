package com.example.moodbook.ui.myMoodMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyMoodMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyMoodMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my Mood map fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
