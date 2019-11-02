package com.example.moodbook.ui.addFriends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class addFriendViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public addFriendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add friend  fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
