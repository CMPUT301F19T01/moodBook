package com.example.moodbook.ui.myRequests;

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

public class myRequestsFragment extends Fragment {
    private myRequestsViewModel MyRequestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyRequestsViewModel =
                ViewModelProviders.of(this).get(myRequestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myrequests, container, false);
        final TextView textView = root.findViewById(R.id.text_myrequests);
        MyRequestsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}