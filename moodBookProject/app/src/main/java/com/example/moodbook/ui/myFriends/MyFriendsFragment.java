package com.example.moodbook.ui.myFriends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.moodbook.UserListFragment;

/**
 * This class is used to show the list of friends/followings that the user has.
 * This class inherits the attributes and methods from the UserListFragment
 */
public class MyFriendsFragment extends UserListFragment {

    private static final String TAG = MyFriendsFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, TAG);

        return root;
    }
}

