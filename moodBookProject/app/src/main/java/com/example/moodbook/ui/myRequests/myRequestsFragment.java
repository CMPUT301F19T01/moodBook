package com.example.moodbook.ui.myRequests;

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

public class myRequestsFragment extends PageFragment {
    // temporary, will be removed
    @Deprecated
    private myRequestsViewModel MyRequestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myrequests);

        MyRequestsViewModel =
                ViewModelProviders.of(this).get(myRequestsViewModel.class);
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