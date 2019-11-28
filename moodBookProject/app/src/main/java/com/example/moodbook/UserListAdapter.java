package com.example.moodbook;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * This ArrayAdapter stores moodBookUsers to display on the  MyFollowersFragment and the MyFriendsFragments
 * @see com.example.moodbook.ui.followers.MyFollowersFragment
 * @see com.example.moodbook.ui.myFriends.MyFriendsFragment
 */
public class UserListAdapter extends ArrayAdapter {
    private ArrayList<MoodbookUser> users;
    private Context context;

    /**
     * This is creates a new instance of the UserListAdapter
     * @param context
     *  The current context of the application
     * @param users
     *  An array of users
     */
    public UserListAdapter(Context context, ArrayList<MoodbookUser> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * This creates a new instance of the UserListAdapter
     * @param context
     *  The current context of the application
     */

    public UserListAdapter(Context context) {
        this(context, new ArrayList<MoodbookUser>());
    }

    /**
     * This overridden method gets the views to display in fragments
     * @param position
     *  The position for a specific moodBook User in arrayList
     * @param convertView
     *  A view object to display fragments
     * @param parent
     *   A viewgroup Object
     * @return
     *  Returns the updated view Object
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        MoodbookUser user = users.get(position);
        TextView usernameText = view.findViewById(R.id.user_item_username);
        final ImageView userdp = view.findViewById(R.id.user_item_dp);
        usernameText.setText(user.getUsername());
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
     * This overridden method adds a moodBookUser item to the users list and sorts the list
     * @param object
     *  A moodBookUser object
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
     * This method clears the users list
     */
    @Override
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    /**
     * This overridden method gets the User at a specific position
     * @param position
     *  The position to get the user from
     * @return
     *  The user at the given position
     */
    @Nullable
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }
}
