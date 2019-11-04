package com.example.moodbook.ui.myMoodMap;

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

public class myMoodMapFragment extends PageFragment {
    // temporary, will be removed
    @Deprecated
    private myMoodMapViewModel MyMoodMapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_mymoodmap);

        MyMoodMapViewModel =
                ViewModelProviders.of(this).get(myMoodMapViewModel.class);
        final TextView textView = root.findViewById(R.id.text_mymoodmap);
        MyMoodMapViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}
