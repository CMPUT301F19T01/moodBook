package com.example.moodbook.ui.friendMood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.R;
import com.example.moodbook.ui.myRequests.RequestUser;
import com.example.moodbook.ui.request.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class friendMoodAdapter {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private ArrayList<RequestUser> friendList;

    private TextView acceptedText;
    private ListView acceptedListView;

    public friendMoodAdapter(Context context, ArrayList<RequestUser> friendList) {
        super();
        this.context = context;
        this.friendList = friendList;
    }


    public View getView(int position, View view, ViewGroup parent) {

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(R.layout.custom_lv, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();
        final RequestHandler requestHandler = new RequestHandler(mAuth, db);


        acceptedListView = view.findViewById(R.id.accepted_listView);
        acceptedText = view.findViewById(R.id.acceptedFriendText);
        final RequestUser user =  friendList.get(position);
        acceptedText.setText(mAuth.getCurrentUser().getDisplayName());




        return view;
    }
}
