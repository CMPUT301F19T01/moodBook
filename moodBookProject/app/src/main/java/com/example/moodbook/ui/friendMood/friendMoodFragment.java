package com.example.moodbook.ui.friendMood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moodbook.ui.friendMood.friendMoodViewModel;
import com.example.moodbook.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class friendMoodFragment extends Fragment {

    private friendMoodViewModel FriendMoodViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FriendMoodViewModel =
                ViewModelProviders.of(this).get(friendMoodViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friendmood, container, false);
        final TextView textView = root.findViewById(R.id.text_friendMood);
        FriendMoodViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
