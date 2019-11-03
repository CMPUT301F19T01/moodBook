package com.example.moodbook.ui.friendMood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class friendMoodViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public friendMoodViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is frnd Mood fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
