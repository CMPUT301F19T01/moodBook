package com.example.moodbook.ui.myRequests;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodbook.Mood;
import com.example.moodbook.MoodListAdapter;
import com.example.moodbook.R;
import com.example.moodbook.ui.myRequests.RequestUser;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestHolder> {
    private Context context;
    private ArrayList<RequestUser> requestList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RequestUser item);
    }

    public class RequestHolder extends RecyclerView.ViewHolder {


        public RequestHolder(@NonNull View view) {
            super(view);
        }

        public void bind(final RequestUser item, final RequestsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
    public RequestsAdapter(Context context, ArrayList<RequestUser> requestList, OnItemClickListener listener){
        this.context = context;
        this.listener = listener;
        this.requestList = requestList;
    }


    @NonNull
    @Override
    public RequestsAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_lv, parent, false);
        return new RequestHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.RequestHolder holder, int position) {
        final RequestUser requestee = requestList.get(position);

      //  holder.dateText.setText(mood.getDateText());
        // show item background color

        // bind item with onItemClick listener
        //holder.bind(requestee, listener);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public RequestUser getItem(int position) {
        return requestList.get(position);
    }

}
