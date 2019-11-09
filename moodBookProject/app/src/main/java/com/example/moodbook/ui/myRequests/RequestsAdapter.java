package com.example.moodbook.ui.myRequests;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.moodbook.R;

import java.util.ArrayList;
import java.util.Collections;

public class RequestsAdapter extends BaseAdapter {

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
    public View getView(int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(R.layout.custom_lv, parent, false);


        // get the reference of textView and button
        TextView username = (TextView) view.findViewById(R.id.nameRequest);
        Button acceptButton = (Button) view.findViewById(R.id.accept_button);
        Button declineButton = (Button) view.findViewById(R.id.decline_button);

        // Set the name on the list
        RequestUser user =  requestList.get(position);
        username.setText(user.getUsername());

        // Click listener of button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // accept
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // decline
            }
        });

        return view;
    }

    public RequestUser getItem(int position) {
        // TODO Auto-generated method stub
        return requestList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
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
        requestList.add(item);
        // notify item added
        notifyDataSetChanged();
    }

}
