package com.example.moodbook.ui.myRequests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodbook.CreateMoodActivity;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.EditMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodListAdapter;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.RecyclerItemTouchHelper;
import com.example.moodbook.ui.Request.RequestHandler;
import com.example.moodbook.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class myRequestsFragment extends PageFragment {

    private ListView requestListView;
    private ArrayList<String> requestDataList;
    private RequestsAdapter rAdapter;

    // connect to DB
    private RequestHandler moodDB;
    private FirebaseAuth mAuth;
    private static final String TAG = HomeFragment.class.getSimpleName();

    // temporary, will be removed
    @Deprecated
    private myRequestsViewModel MyRequestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myrequests);

        requestListView = (ListView) root.findViewById(R.id.request_listView);

        // Set some data to array list
       // requestDataList = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666,777,888".split(",")));

//        // Initialize adapter and set adapter to list view
         rAdapter = new RequestsAdapter(getContext(), requestDataList);
//        requestListView.setAdapter(rAdapter);
//        rAdapter.notifyDataSetChanged();

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        moodDB = new RequestHandler(mAuth, getContext(), RequestHandler.getRequests(rAdapter), TAG);


//
//        MyRequestsViewModel =
//                ViewModelProviders.of(this).get(myRequestsViewModel.class);
//        final TextView textView = root.findViewById(R.id.text_myrequests);
//        MyRequestsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }


}