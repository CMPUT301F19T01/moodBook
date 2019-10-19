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
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class SelectMoodStateFragment extends DialogFragment {

    Button select_button;
    Button cancel_button;
    RelativeLayout happy;
    RelativeLayout sad;
    RelativeLayout afraid;
    RelativeLayout angry;
    String selectedItem = "";
    SearchView sv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.select_mood_state,null);

        //Set Title Dialog Title
        getDialog().setTitle("Pick a Mood");


        happy = rootView.findViewById(R.id.happy_layout);
        sad = rootView.findViewById(R.id.sad_layout);
        afraid = rootView.findViewById(R.id.afraid_layout);
        angry = rootView.findViewById(R.id.angry_layout);
        select_button = rootView.findViewById(R.id.select);
        cancel_button = rootView.findViewById(R.id.cancel);

        afraid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "You have Selected Afraid", Toast.LENGTH_LONG).show();
                selectedItem = "afraid";
            }
        });
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "You have Selected Angry", Toast.LENGTH_LONG).show();
                selectedItem = "angry";
            }
        });

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "You have Selected Happy", Toast.LENGTH_LONG).show();
                selectedItem = "happy";
            }
        });

        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), " You have Selected Sad", Toast.LENGTH_LONG).show();
                selectedItem = "sad";
            }
        });
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedItem == ""){
                    Toast.makeText(getContext(), "You have not selected anything", Toast.LENGTH_LONG).show();
                }else if(selectedItem =="happy"){
                    Toast.makeText(getContext(), "Mood : Happy", Toast.LENGTH_LONG).show();
                }else if(selectedItem =="sad"){
                    Toast.makeText(getContext(), "Mood : Sad", Toast.LENGTH_LONG).show();
                }else if(selectedItem =="angry"){
                    Toast.makeText(getContext(), "Mood : Angry", Toast.LENGTH_LONG).show();
                }else if(selectedItem =="afraid"){
                    Toast.makeText(getContext(), "Mood : Afraid", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "An Error Occurred", Toast.LENGTH_LONG).show();
                }
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
