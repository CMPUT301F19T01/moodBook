package com.example.moodbook;

import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class SelectMoodStateFragment extends DialogFragment {

    Button select_button;
    Button cancel_button;
    ListView moodView;
    SearchView sv;

    ArrayAdapter<String> statesadapter;
    ArrayAdapter<Integer> emojiadapter;
    String[] MoodStates = {"Happy", "Sad", "Afraid", "Angry"};
    int[] MoodEmoji = {R.drawable.happy,R.drawable.sad, R.drawable.afraid, R.drawable.angry };

//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.select_mood_state,null);
//        ListView moodView;
//        moodView = view.findViewById(R.id.moodListView);
//
//
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.select_mood_state,null);

        //Set Title Dialog Title
        getDialog().setTitle("Pick a Mood");

        //Button, Listview, SearchView Initializations
        moodView = rootView.findViewById(R.id.moodListView);
        select_button = rootView.findViewById(R.id.select);
        cancel_button = rootView.findViewById(R.id.cancel);
        sv = rootView.findViewById(R.id.search_my_mood);

        //Create and Set Adapter to Listview
        statesadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,MoodStates);
        moodView.setAdapter(statesadapter);

        sv.setQueryHint("Search...");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                statesadapter.getFilter().filter(s);
                return false;
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });


       return rootView;
    }



}
