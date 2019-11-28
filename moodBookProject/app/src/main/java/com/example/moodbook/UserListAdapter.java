package com.example.moodbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class UserListAdapter extends ArrayAdapter {
    private ArrayList<MoodbookUser> users;
    private Context context;

    /**
     *
     * @param context
     * @param users
     */
    public UserListAdapter(Context context, ArrayList<MoodbookUser> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     *
     * @param context
     */

    public UserListAdapter(Context context) {
        this(context, new ArrayList<MoodbookUser>());
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        MoodbookUser user = users.get(position);

        // get views for username, profile picture
        TextView usernameText = view.findViewById(R.id.user_item_username);
        final ImageView userdp = view.findViewById(R.id.user_item_dp);

        // show username
        usernameText.setText(user.getUsername());

        // show profile picture

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profilepics/" + user.getUsername() + ".jpeg" );
        try{
            final File localFile = File.createTempFile("Images", "jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap obtainedImg = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    userdp.setImageBitmap(obtainedImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     *
     * @param object
     */

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

    /**
     *
     */
    @Override
    public void clear() {
        users.clear();
        // notify list is cleared
        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     * @return
     */
    @Nullable
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }
}
