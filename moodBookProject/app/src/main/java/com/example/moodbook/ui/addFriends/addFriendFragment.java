package com.example.moodbook.ui.addFriends;

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

public class addFriendFragment extends Fragment {
    private addFriendViewModel AddFriendsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddFriendsViewModel =
                ViewModelProviders.of(this).get(addFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addfriends, container, false);
        final TextView textView = root.findViewById(R.id.text_addfriends);
        AddFriendsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;

    }
}
