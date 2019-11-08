package com.example.moodbook.ui.myMood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@Deprecated
public class myMoodViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public myMoodViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is myMood fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
