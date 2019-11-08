package com.example.moodbook.ui.myFriendMoodMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyFriendMoodMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyFriendMoodMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is friend Mood Map  fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
