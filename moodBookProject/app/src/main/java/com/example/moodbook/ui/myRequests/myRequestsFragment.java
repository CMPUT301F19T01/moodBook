package com.example.moodbook.ui.myRequests;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.Mood;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.ui.Request.RequestHandler;
import com.example.moodbook.ui.Request.RequestsAdapter;
import com.example.moodbook.ui.friendMood.FriendMood;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class myRequestsFragment extends PageFragment implements DBCollectionListener {
    private static final String TAG = myRequestsFragment.class.getSimpleName();

    private RequestsAdapter requestsAdapter;
    //private CoordinatorLayout requestListLayout;
    private ListView requestListView;
    private TextView hiddenMsg;

    // connect to DB
    private RequestHandler requestDB;
    //private FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myrequests);
        //db = FirebaseFirestore.getInstance();

        hiddenMsg = root.findViewById(R.id.request_empty_msg);

        //requestListLayout = root.findViewById(R.id.request_layout);
        requestListView = root.findViewById(R.id.request_listView);
        requestsAdapter=  new RequestsAdapter(getContext(), new ArrayList<MoodbookUser>());
        requestListView.setAdapter(requestsAdapter);

        // initialize DB connector
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        requestDB = new RequestHandler(mAuth, getContext(), TAG);
        requestDB.setRequestListListener(this);

        return root;
    }

    @Override
    public void beforeGettingList() {
        hiddenMsg.setVisibility(View.INVISIBLE);   // hide empty message
        requestsAdapter.clear();
    }

    @Override
    public void onGettingItem(Object item) {
        if(item instanceof MoodbookUser) {
            requestsAdapter.addItem((MoodbookUser) item);
        }
    }

    @Override
    public void afterGettingList() {
        if (requestsAdapter.getCount() == 0){
            hiddenMsg.setVisibility(View.VISIBLE); // show empty message
        }
    }
}