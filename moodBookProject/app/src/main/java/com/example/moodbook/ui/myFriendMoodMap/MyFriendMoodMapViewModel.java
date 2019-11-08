package com.example.moodbook.ui.myFriendMoodMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@Deprecated
class myFriendMoodMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public myFriendMoodMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is friend Mood Map  fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
