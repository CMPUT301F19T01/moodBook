package com.example.moodbook.ui.myFriendMoodMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moodbook.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class myFriendMoodMapFragment extends Fragment {
    private myFriendMoodMapViewModel MyFriendMoodMapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyFriendMoodMapViewModel =
                ViewModelProviders.of(this).get(myFriendMoodMapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friendmoodmap, container, false);
        final TextView textView = root.findViewById(R.id.text_friendMoodmoodmap);
        MyFriendMoodMapViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
