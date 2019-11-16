package com.example.moodbook.ui.friendMood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.moodbook.PageFragment;
import com.example.moodbook.R;

public class FriendMoodFragment extends PageFragment {

    //private friendMoodViewModel FriendMoodViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_friendmood);



        return root;
    }
}
