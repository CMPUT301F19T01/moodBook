package com.example.moodbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PageFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState,
                             int layoutId) {
        View root = inflater.inflate(layoutId, container, false);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // remove all the useless menu options
        menu.clear();
        /*// remove old search action menu
        if(menu.findItem(R.id.mood_history_action_search) != null) {
            menu.removeItem(R.id.mood_history_action_search);
        }*/
    }
}