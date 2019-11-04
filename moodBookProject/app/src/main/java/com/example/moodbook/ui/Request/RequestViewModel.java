package com.example.moodbook.ui.Request;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RequestViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RequestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is request fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}