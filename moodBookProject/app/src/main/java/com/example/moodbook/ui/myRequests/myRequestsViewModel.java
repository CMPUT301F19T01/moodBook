package com.example.moodbook.ui.myRequests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@Deprecated
public class myRequestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public myRequestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my Request fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
