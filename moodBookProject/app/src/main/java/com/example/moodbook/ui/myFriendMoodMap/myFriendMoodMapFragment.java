package com.example.moodbook.ui.myFriendMoodMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moodbook.PageFragment;
import com.example.moodbook.R;

public class myFriendMoodMapFragment extends PageFragment {
    // temporary, will be removed
    @Deprecated
    private myFriendMoodMapViewModel MyFriendMoodMapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_friendmoodmap);

        MyFriendMoodMapViewModel =
                ViewModelProviders.of(this).get(myFriendMoodMapViewModel.class);
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
