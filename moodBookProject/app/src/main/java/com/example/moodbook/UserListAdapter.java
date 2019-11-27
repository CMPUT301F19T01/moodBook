package com.example.moodbook;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class UserListAdapter extends ArrayAdapter {
    private ArrayList<MoodbookUser> users;
    private Context context;

    public UserListAdapter(Context context, ArrayList<MoodbookUser> friends) {
        super(context, 0, friends);
        this.users = friends;
        this.context = context;
    }

    public UserListAdapter(Context context) {
        this(context, new ArrayList<MoodbookUser>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        MoodbookUser user = users.get(position);

        // get views for username
        TextView usernameText = view.findViewById(R.id.user_item_username);

        final ImageView frienddp = view.findViewById(R.id.user_item_dp);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profilepics/" + user.getUsername() + ".jpeg" ).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()/* context */)
                        .load(uri)
                        .centerCrop()
                        .into(frienddp);
            }
        }) ;
        // show username
        usernameText.setText(user.getUsername());

        return view;
    }

    @Override
    public void add(@Nullable Object object) {
        if(object == null || object instanceof MoodbookUser) {
            MoodbookUser item = (MoodbookUser)object;
            users.add(item);
            Collections.sort(users);
            // notify item added
            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        users.clear();
        // notify list is cleared
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }
}
