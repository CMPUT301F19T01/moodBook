package com.example.moodbook.ui.myRequests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.moodbook.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class myRequestsFragment extends PageFragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener  {



    // Mood History
    private RecyclerView requestListView;
    private RequestsAdapter requestAdapter;
    private CoordinatorLayout requestLayout;

    // connect to DB
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;
    private static final String TAG = HomeFragment.class.getSimpleName();

    // temporary, will be removed
    @Deprecated
    private myRequestsViewModel MyRequestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myrequests);

        // Set up recyclerView and adapter
        requestLayout = root.findViewById(R.id.request_layout);
        requestListView = root.findViewById(R.id.request_listView);
        setupAdapter(new RequestsAdapter.OnItemClickListener() {
                         @Override
                         public void onItemClick(RequestUser item) {

                         }
                     });

//        // initialize DB connector
//        mAuth = FirebaseAuth.getInstance();
//        moodDB = new DBMoodSetter(mAuth, getContext(), DBMoodSetter.getMoodHistoryListener(requestAdapter), TAG);


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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

    }

    private void setupAdapter(RequestsAdapter.OnItemClickListener itemClickListener) {
        requestAdapter = new RequestsAdapter(getContext(), new ArrayList<RequestUser>(), itemClickListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        requestListView.setLayoutManager(mLayoutManager);
        requestListView.setItemAnimator(new DefaultItemAnimator());
        requestListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        requestListView.setAdapter(requestAdapter);
    }

}