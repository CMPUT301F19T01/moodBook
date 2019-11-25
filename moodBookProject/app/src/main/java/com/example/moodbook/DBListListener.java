package com.example.moodbook;

public interface DBListListener {
    void beforeGettingList();
    void onGettingItem(Object item);
    void afterGettingList();
}
