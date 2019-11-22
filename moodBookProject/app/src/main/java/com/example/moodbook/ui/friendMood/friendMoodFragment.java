package com.example.moodbook.ui.friendMood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ui.myMood.myMoodViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

@Deprecated
public class friendMoodFragment extends PageFragment {
    // temporary, will be removed
    @Deprecated
    private myMoodViewModel MymoodViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_mymood);

        MymoodViewModel =
                ViewModelProviders.of(this).get(myMoodViewModel.class);
        final TextView textView = root.findViewById(R.id.text_myMood);
        MymoodViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}

