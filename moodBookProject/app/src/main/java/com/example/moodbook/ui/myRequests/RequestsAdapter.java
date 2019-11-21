package com.example.moodbook.ui.myRequests;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.ui.Request.RequestHandler;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

import androidx.annotation.NonNull;

public class RequestsAdapter extends BaseAdapter {
    private FirebaseFirestore db;
    private Context context;
    private ArrayList<RequestUser> requestList;

    public RequestsAdapter(Context context, ArrayList<RequestUser> requestList) {
        super();
        this.context = context;
        this.requestList = requestList;
    }

    public int getCount() {
        int size = 0;
        if (requestList!=null){
            // return the number of records
            size = requestList.size();
        }
        return size;
    }

    // getView method is called for each item of ListView
    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, ViewGroup parent) {

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(R.layout.custom_lv, parent, false);


        // get the reference of textView and button
        final TextView usernameTextView = (TextView) view.findViewById(R.id.nameRequest);
        Button acceptButton = (Button) view.findViewById(R.id.accept_button);
        Button declineButton = (Button) view.findViewById(R.id.decline_button);

        // Set the name on the list
        final RequestUser frienduser =  requestList.get(position);
        usernameTextView.setText(frienduser.getUsername());
        final FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();
        final RequestHandler requestHandler = new RequestHandler(mAuth,context);
        final String username = mAuth.getCurrentUser().getDisplayName();

        // Click listener of button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,
                        "Accept",
                        Toast.LENGTH_LONG).show();
                    // When Accepts a friend requests and username is added in requestee's friend mood list
                    requestHandler.addFriend(frienduser, username);
                    //remove request once accepted
                    requestHandler.removeRequest(frienduser.getUsername());

            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,
                        "Decline",
                        Toast.LENGTH_LONG).show();
                requestHandler.removeRequest(frienduser.getUsername());
                Toast.makeText(context,
                        "deleted",
                        Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public RequestUser getItem(int position) {
        return requestList.get(position);
    }

    //not used
    public long getItemId(int position) {
        return position;
    }

    // Remove all name items
    public void clear() {
        if (requestList!= null){

            requestList.clear();
        }
        // notify list is cleared
        notifyDataSetChanged();
    }
    // Add a request name
    public void addItem(RequestUser item) {
        if (item!=null){
            requestList.add(item);
        }
        // notify item added
        notifyDataSetChanged();
    }
}
