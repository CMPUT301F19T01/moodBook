package com.example.moodbook.ui.followers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.MoodbookUser;
import com.example.moodbook.R;

import java.util.ArrayList;
import java.util.Collections;

public class followersAdapter extends ArrayAdapter {

    private ArrayList<MoodbookUser> followers;
    private Context context;

    public followersAdapter(Context context, ArrayList<MoodbookUser> followers) {
        super(context, 0, followers);
        this.followers = followers;
        this.context = context;
    }

    public followersAdapter(Context context) {
        this(context, new ArrayList<MoodbookUser>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.followers_item, parent, false);
        }

        MoodbookUser followerUser = followers.get(position);

        // get views for username
        TextView usernameText = view.findViewById(R.id.followers_item_username);

        // show username
        usernameText.setText(followerUser.getUsername());

        return view;
    }

    @Override
    public void add(@Nullable Object object) {
        if(object == null || object instanceof MoodbookUser) {
            MoodbookUser item = (MoodbookUser)object;
            followers.add(item);
            Collections.sort(followers);
            // notify item added
            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        followers.clear();
        // notify list is cleared
        notifyDataSetChanged();
    }
}
