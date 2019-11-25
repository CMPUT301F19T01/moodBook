package com.example.moodbook.ui.Request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBListListener;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.data.UsernameList;
import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.example.moodbook.ui.myRequests.RequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This fragment is shown to allow the user to send requests to other users
 */
public class RequestFragment extends PageFragment implements DBListListener{
    //private RequestViewModel requestViewModel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private EditText requestText;
    private Button requestButton;

    private com.example.moodbook.ui.Request.RequestHandler requestHandler;
    private RequestsAdapter requestsAdapter;
    private UsernameList usernameList;
    private ArrayList<MoodbookUser> friends;
    private DBFriend friendDB;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_request);
        db = FirebaseFirestore.getInstance();



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        friendDB = new DBFriend(mAuth, getContext());
        friendDB.setFriendListListener(this);

        friends = new ArrayList<>();

        usernameList = new UsernameList(FirebaseFirestore.getInstance());
        usernameList.updateUsernameList();

        requestText = root.findViewById(R.id.usernameEditText);
        requestButton = root.findViewById(R.id.requestButton);

        requestHandler = new com.example.moodbook.ui.Request.RequestHandler(mAuth, getContext());
        requestsAdapter = new RequestsAdapter(getContext(), new ArrayList<MoodbookUser>());
        requestHandler.setRequestListListener(requestsAdapter);



        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String addUser = requestText.getText().toString();

                if(addUser.equals(user.getDisplayName())){ // check if adding self
                    requestText.setError("Cannot add yourself");
                } else if (friends.contains(addUser)) { // check if user already added
                    requestText.setError("User already added");
                }else if (usernameList.isUser(addUser)){ // check if username exists in db
                    requestHandler.sendRequest(addUser, user.getUid(), user.getDisplayName());
                    Toast.makeText(root.getContext(), "Sent request",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestText.setError("User does not exist");
                }
            }
        });

        return root;
    }

    @Override
    public void beforeGettingList(){
        friends.clear();
    }

    @Override
    public void onGettingItem(Object item){
        if(item instanceof MoodbookUser) {
            friends.add((MoodbookUser)item);
        }
    }

    @Deprecated
    @Override
    public void afterGettingList(){

    }
}