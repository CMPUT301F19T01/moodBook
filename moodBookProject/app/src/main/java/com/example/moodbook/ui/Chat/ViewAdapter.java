package com.example.moodbook.ui.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;

import com.example.moodbook.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewAdapter extends BaseAdapter {
    private FirebaseFirestore db;
    private Context context;
    private ArrayList<Fragment> fragments;
    private ArrayList<String> name;


    public ViewAdapter(Context context, ArrayList<Fragment> fragments, ArrayList<String> name) {
        super();
        this.context= context;
        this.fragments = fragments;
        this.name = name;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (name!=null){
            // return the number of records
            size = name.size();
        }
        return size;
    }
    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, ViewGroup parent) {

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(R.layout.chat_view, parent, false);

        TableLayout tableLayout = view.findViewById(R.id.table);
        ViewPager viewPager = view.findViewById(R.id.View);



        return view;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public void addFragment(Fragment fragment, String name) {
        fragments.add(fragment);
//        name.add(name);
    }


    public CharSequence getPageTitle(int position) {
        return name.get(position);
    }


}


