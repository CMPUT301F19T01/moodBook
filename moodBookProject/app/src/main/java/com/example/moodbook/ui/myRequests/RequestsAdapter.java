package com.example.moodbook.ui.myRequests;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.Mood;
import com.example.moodbook.ui.request.RequestHandler;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class RequestsAdapter extends BaseAdapter {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

//    private CollectionReference userReference = db.collection("USERS");
//    private String uid = mAuth.getCurrentUser().getUid();


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
        final TextView username = (TextView) view.findViewById(R.id.nameRequest);
        Button acceptButton = (Button) view.findViewById(R.id.accept_button);
        Button declineButton = (Button) view.findViewById(R.id.decline_button);

        // Set the name on the list
        final RequestUser user =  requestList.get(position);
        username.setText(user.getUsername());
        final FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();
        final RequestHandler requestHandler = new RequestHandler(mAuth, db);
//        acceptedText = view.findViewById(R.id.acceptedFriendText);

        // Click listener of button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,
                        "Accept",
                        Toast.LENGTH_LONG).show();
//                String usernamei = username.getText().toString();
                    requestHandler.addFriend(user,uid,mAuth.getCurrentUser().getDisplayName());
//                acceptedText.setText(user.getUsername());

            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,
                        "Decline",
                        Toast.LENGTH_LONG).show();
//                RequestsAdapter.remove
                Decline(position);
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
//    public void addFriend(RequestUser acceptFriend, String uidp, String usernamep){
//        final String uid = uidp;
//        final String username = usernamep;
//        final CollectionReference collectionReference = db.collection("USERS");
//        DocumentReference docRef = db.collection("usernamelist").document(String.valueOf(acceptFriend));
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()){
//                        HashMap<String, Object> data = new HashMap<>();
//                        data.put("uid", uid);
//                        collectionReference.document(document.getString("uid")).collection("FRIENDS").document(username).set(data); // adding the request to the addUser's REQUEST collection
//                    } else {
////                        Log.d("TESTINGG", "no such doc");
//                    }
//                } else {
////                    Log.d("TESTING", "get failed with ", task.getException());
//                }
//            }
//        });
//
//
//    }
    public void Decline(int position) {

        requestList.remove(requestList.indexOf(requestList.get(position)));

        Toast.makeText(context,
                "inside remove item",
                Toast.LENGTH_LONG).show();
//        requestList.notify();
    }

}
