package com.example.moodbook.ui.Request;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.data.UsernameList;
import com.example.moodbook.ui.myRequests.RequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This fragment is shown to allow the user to send requests to other users
 */
public class RequestFragment extends PageFragment {
    //private RequestViewModel requestViewModel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private EditText requestText;
    private Button requestButton;

    private RequestHandler requestHandler;
    private RequestsAdapter requestsAdapter;
    private UsernameList usernameList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_request);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.i("ads;lfkj", user.getDisplayName());

        usernameList = new UsernameList(FirebaseFirestore.getInstance());
        usernameList.updateUsernameList();

        requestText = root.findViewById(R.id.usernameEditText);
        requestButton = root.findViewById(R.id.requestButton);

        requestHandler = new RequestHandler(mAuth, getContext());
        requestsAdapter = new RequestsAdapter(getContext(), new ArrayList<MoodbookUser>());
        requestHandler.setRequestListListener(requestsAdapter);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addUser = requestText.getText().toString();

                if (usernameList.isUser(addUser)){ // check if username exists in db
                    if(!(addUser.equals(user.getDisplayName()))){ // check if user is try to send request to themselves
                        requestHandler.sendRequest(addUser, user.getUid(), user.getDisplayName());
                        Toast.makeText(root.getContext(), "Sent request",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(root.getContext(), "cannot send request to yourself",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    requestText.setError("User does not exist");
                }
            }
        });

        return root;
    }
}