package com.example.moodbook.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moodbook.CreateMoodActivity;
import com.example.moodbook.CustomAdapter;
import com.example.moodbook.MainActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private FloatingActionButton add_mood_button;
    private ListView moodListView;
    private ArrayAdapter<Mood> moodAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        // Add a mood: when floating add button is clicked, start add activity
        add_mood_button = root.findViewById(R.id.mood_history_add_button);
        add_mood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateMoodActivity.class);
                startActivity(intent);
            }
        });

        // Set up ListView
        moodListView = root.findViewById(R.id.mood_history_listView);
        moodAdapter = new CustomAdapter(getContext(), new ArrayList<Mood>());
        // test adding
        Mood testItem = new Mood(null, null, "sad");
        moodAdapter.add(testItem);
        moodListView.setAdapter(moodAdapter);

        // Edit a mood: When a mood item is clicked, start edit activity
        moodListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the selected mood
                Mood selectedMood = (Mood)adapterView.getItemAtPosition(i);
                System.out.println("Selected: "+selectedMood.getEmotionText());
                // TODO: start edit activity to edit the selected mood
            }
        });

        return root;
    }
}