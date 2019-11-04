package com.example.moodbook.ui.myMoodMap;

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

public class myMoodMapFragment extends Fragment {
    private myMoodMapViewModel MyMoodMapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyMoodMapViewModel =
                ViewModelProviders.of(this).get(myMoodMapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mymoodmap, container, false);
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
