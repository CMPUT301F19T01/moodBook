package com.example.moodbook.ui.Request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.data.UsernameList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This fragment is shown to allow a user to send requests to other users
 */
public class RequestFragment extends PageFragment
        implements DBCollectionListener, UsernameList.UsernameListListener {

    private AutoCompleteTextView usernameText;
    private ImageView usernameArrow;
    private Button requestButton;

    private FirebaseUser user;
    private RequestHandler requestHandler;
    private UsernameList usernameList;
    private ArrayAdapter<String> usersAdapter;

    private DBFriend friendDB;
    private ArrayList<String> friends;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_request);

        usernameText = root.findViewById(R.id.send_request_username_text);
        usernameArrow = root.findViewById(R.id.send_request_username_arrow);
        requestButton = root.findViewById(R.id.send_request_button);

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        friendDB = new DBFriend(mAuth, getContext());
        friendDB.setFriendListListener(this);

        friends = new ArrayList<>();

        setupUsername();

        requestHandler = new RequestHandler(mAuth, getContext());

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addUser = usernameText.getText().toString();

                if(addUser.equals(user.getDisplayName())){ // check if adding self
                    usernameText.setError("Cannot add yourself");
                } else if (friends.contains(addUser)) { // check if user already added
                    usernameText.setError("User already added");
                } else if (usernameList.isUser(addUser)){ // check if username exists in db
                    requestHandler.sendRequest(addUser, user.getUid(), user.getDisplayName());
                    Toast.makeText(root.getContext(), "Sent request",
                            Toast.LENGTH_LONG).show();
                } else {
                    usernameText.setError("User does not exist");
                }
            }
        });

        return root;
    }

    /**
     * This is used by DBFriend to perform task before getting all the friend users
     */
    @Override
    public void beforeGettingList(){
        friends.clear();
    }

    /**
     * This is used by DBFriend to perform task when getting a friend user
     * @param item
     */
    @Override
    public void onGettingItem(Object item){
        if(item instanceof MoodbookUser) {
            friends.add(((MoodbookUser) item).getUsername());
        }
    }

    /**
     * This is used by DBFriend to perform task after getting all the friend users
     */
    @Override
    public void afterGettingList(){
        if(usersAdapter.getCount() > 0) {
            for(String friendUsername : friends) {
                usersAdapter.remove(friendUsername);
            }
        }
    }

    /**
     * This is used by UsernameList to perform task after getting all the usernames
     */
    @Override
    public void afterGettingUsernameList() {
        usersAdapter.clear();
        usersAdapter.addAll(usernameList.getUsernameList());
        usersAdapter.remove(user.getDisplayName());
        if(friends.size() > 0) {
            afterGettingList();
        }
    }

    /**
     * This is a method used to show a list of users in the dropdown menu when a user wants send a request to another user.
     * Offers more convenience and could also act as a suggestions list.
     */
    private void setupUsername() {
        usernameList = new UsernameList(FirebaseFirestore.getInstance());
        usernameList.setUsernameListListener(this);

        usersAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line) {
            /**
             * This limits the total number of dropdown items to 100
             * @return  the number of dropdown items to be displayed
             */
            @Override
            public int getCount() {
                return Math.min(100, super.getCount());
            }
        };
        usernameText.setAdapter(usersAdapter);

        usernameArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                usernameText.showDropDown();
            }
        });
    }
}