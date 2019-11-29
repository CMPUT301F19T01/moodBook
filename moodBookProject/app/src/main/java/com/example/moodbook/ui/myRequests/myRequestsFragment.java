package com.example.moodbook.ui.myRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ui.Request.RequestHandler;
import com.example.moodbook.ui.Request.RequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

/**
 * This fragment is used to display a list of pending requests that the user has.
 * This class implements the DBCollectionListener to provide functionality for the methods declared in that interface
 */
public class myRequestsFragment extends PageFragment implements DBCollectionListener {

    private static final String TAG = myRequestsFragment.class.getSimpleName();
    private RequestsAdapter requestsAdapter;
    private ListView requestListView;
    private TextView hiddenMsg;
    private RequestHandler requestDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myrequests);

        hiddenMsg = root.findViewById(R.id.request_empty_msg);
        requestListView = root.findViewById(R.id.request_listView);
        requestsAdapter=  new RequestsAdapter(getContext(), new ArrayList<MoodbookUser>());
        requestListView.setAdapter(requestsAdapter);

        // initialize DB connector
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        requestDB = new RequestHandler(mAuth, getContext(), TAG);
        requestDB.setRequestListListener(this);

        return root;
    }

    /**
     * This is used by RequestHandler to perform task before getting all the requests to users
     */
    @Override
    public void beforeGettingList() {
        hiddenMsg.setVisibility(View.INVISIBLE);   // hide empty message
        requestsAdapter.clear();
    }

    /**
     * This is used by RequestHandler to perform task when getting all the requests to users
     */
    @Override
    public void onGettingItem(Object item) {
        if(item instanceof MoodbookUser) {
            requestsAdapter.addItem((MoodbookUser) item);
        }
    }

    /**
     * This is used by RequestHandler to perform task after getting all the requests to users
     */
    @Override
    public void afterGettingList() {
        if (requestsAdapter.getCount() == 0){
            hiddenMsg.setVisibility(View.VISIBLE); // show empty message
        }
    }
}