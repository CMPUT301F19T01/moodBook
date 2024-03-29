package com.example.moodbook.ui.Request;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.MoodbookUser;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

/**
 * This class handles the display of requests that the user has received
 */
public class RequestsAdapter extends BaseAdapter implements RequestHandler.VerifyFollowerInterface{
    private FirebaseFirestore db;
    private Context context;
    private ArrayList<MoodbookUser> requestList;

    private RequestHandler requestHandler;
    private FirebaseUser user;

    public RequestsAdapter(Context context, ArrayList<MoodbookUser> requestList,
                           RequestHandler requestHandler) {
        super();
        this.context = context;
        this.requestList = requestList;
        this.requestHandler = requestHandler;
    }

    /**
     * This method is called for each item of ListView
     * @param position
     * This is the position in the list
     * @param view
     * This is the view
     * @param parent
     * This is the parent of ViewGroup
     * @return view
     */
    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, ViewGroup parent) {

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(R.layout.request_item, parent, false);


        // get the reference of textView and button
        final TextView usernameTextView = view.findViewById(R.id.nameRequest);
        Button acceptButton = view.findViewById(R.id.accept_button);
        Button declineButton = view.findViewById(R.id.decline_button);

        // Set the name on the list
        final MoodbookUser frienduser =  requestList.get(position);
        usernameTextView.setText(frienduser.getUsername());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final String username = user.getDisplayName();

        final RequestsAdapter thisInstance = this;

        // Click listener of button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When Accepts a friend requests and username is added in requestee's friend mood list
                requestHandler.addFriend(frienduser, username);
                requestHandler.addToFollowerList(frienduser, username);

                //remove request once accepted
                requestHandler.removeRequest(frienduser.getUsername());
                Log.i("Accepted.", "Accepted Request"); //testing purposes
                requestHandler.startFollowBack(frienduser, thisInstance);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Decline Request
                requestHandler.removeRequest(frienduser.getUsername());
                Log.i("Declined", "Declined Request");
            }
        });

        return view;
    }

    /**
     * This method returns a MoodbookUser based on their position in the adapter
     * @param position
     * This is the position of the user in the list
     * @return MoodbookUser
     */
    public MoodbookUser getItem(int position) {
        return requestList.get(position);
    }

    @Deprecated
    /**
     * Inherited from the BaseAdapter class.
     * Not used for this app.
     */
    public long getItemId(int position) {
        return position;
    }

    @Override
    /**
     * This is method is used to count all the items in the listView.
     */
    public int getCount() {
        int size = 0;
        if (requestList!=null){
            // return the number of records
            size = requestList.size();
        }
        return size;
    }

    /**
     * This method is used to clear the adapter.
     */
    public void clear() {
        if (requestList!= null){

            requestList.clear();
        }
        // notify list is cleared
        notifyDataSetChanged();
    }

    /**
     * This method is used to add a user into the adapter.
     * @param item
     */
    public void addItem(MoodbookUser item) {
        if (item!=null){
            requestList.add(item);
        }
        // notify item added
        notifyDataSetChanged();
    }
    /**
     * It is used to verify if follower is already your friend.
     */

    @Override
    public void onVerifyingFollowerAsFriend(boolean isFriend, final MoodbookUser acceptedFriend) {
        if(isFriend) return;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Follow Back");
        builder.setMessage("Would you like to follow back?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestHandler.sendRequest(acceptedFriend.getUsername(),
                       user.getUid(), user.getDisplayName() );
            }
        })
                .setNegativeButton("No", null)
                .show();

    }
}
