package com.example.moodbook.ui.myMoodMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@Deprecated
public class myMoodMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public myMoodMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my Mood map fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
