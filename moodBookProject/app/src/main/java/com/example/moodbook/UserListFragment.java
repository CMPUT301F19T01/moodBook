package com.example.moodbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.example.moodbook.ui.followers.MyFollowersFragment;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
import com.example.moodbook.ui.profile.ProfileViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;

/**
 * This is a subclass of the PageFragment it show displays the list of Users in the MyFriendsFragment and the MyFollowersFragment
 * @see PageFragment
 * @see MyFriendsFragment
 * @see MyFollowersFragment
 */
public abstract class UserListFragment extends PageFragment implements DBCollectionListener {
    private static final String[] SUBCLASSES_NAMES = {
            MyFriendsFragment.class.getSimpleName(),
            MyFollowersFragment.class.getSimpleName()
    };
    protected String TAG;
    private DBFriend friendDB;
    protected ListView userListView;
    protected UserListAdapter userListAdapter;
    private TextView hiddenMsg;

    /**
     * This method instantiates the UserListFragment view
     * @param inflater
     *  The LayoutInflater object that is used to inflate the view
     * @param container
     *  This is the parent view that the fragments UI should be attached to
     * @param savedInstanceState
     *  This is the saved Instance state from the previous state
     * @param fragmentName
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState,
                             String fragmentName) {
        if(!Arrays.asList(SUBCLASSES_NAMES).contains(fragmentName)){
            throw new IllegalArgumentException(fragmentName+" is not a permitted subclass!");
        }
        this.TAG = fragmentName;
        int layoutId = fragmentName.equals(SUBCLASSES_NAMES[0]) ?
                R.layout.fragment_myfriends : R.layout.fragment_followers;
        View root = super.onCreateView(inflater, container, savedInstanceState, layoutId);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);
        int listViewId;
        final String listType;
        if(TAG.equals(SUBCLASSES_NAMES[0])) {
            friendDB.setFriendListListener(this);
            listViewId = R.id.friends_listView;
            hiddenMsg = root.findViewById(R.id.friends_empty_msg);
            listType = "friend";
        }
        else {
            friendDB.setFollowersListListener(this);
            listViewId = R.id.followers_listView;
            hiddenMsg = root.findViewById(R.id.followers_empty_msg);
            listType = "follower";
        }
        userListView = root.findViewById(listViewId);
        final MoodbookUser currentUser = new MoodbookUser(
                mAuth.getCurrentUser().getDisplayName(),
                mAuth.getCurrentUser().getUid());
        setupAdapter(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int pos, long id) {
                final MoodbookUser selectedUser = (MoodbookUser) userListAdapter.getItem(pos);
                new AlertDialog.Builder(getActivity())
                        .setTitle(selectedUser.getUsername())
                        .setMessage("Would you like to view this User's Profile or delete this User")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(TAG.equals(SUBCLASSES_NAMES[0])) {
                                    friendDB.removeFriend(currentUser, selectedUser);
                                }
                                else {
                                    friendDB.removeFollower(currentUser, selectedUser);
                                }
                            }
                        })
                        .setNegativeButton("View", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent viewProfile = new Intent(getContext(), ProfileViewActivity.class);
                                viewProfile.putExtra("username",selectedUser.getUsername());
                                viewProfile.putExtra("userID",selectedUser.getUid());
                                startActivity(viewProfile);
                            }
                        })
                        .setNeutralButton("Cancel",null)
                        .show();
            }
        });

        return root;
    }

    /**
     * This method hides the empty message for the adapter
     */
    @Override
    public void beforeGettingList() {
        hiddenMsg.setVisibility(View.INVISIBLE);   // hide empty message
        userListAdapter.clear();
    }

    /**
     *  This overridden method add the MoodBookUser instance to the userListAdapter
     * @param item
     *  A MoodBookUser object
     */
    @Override
    public void onGettingItem(Object item) {
        if(item instanceof MoodbookUser) {
            userListAdapter.add(item);
        }
    }

    /**
     * This method checks if user List adapter is empty and shows a message if it is
     */
    @Override
    public void afterGettingList() {
        if (userListAdapter.isEmpty()){
            hiddenMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This opens dialog to ask user whether to remove the follower method When a friend/follower item is clicked
     * @param itemClickListener
     *  An onclick listener for detecting item clicks on the fragment
     */

    private void setupAdapter(AdapterView.OnItemClickListener itemClickListener) {
        userListAdapter = new UserListAdapter(getContext());
        userListView.setAdapter(userListAdapter);
        userListView.setOnItemClickListener(itemClickListener);
    }
}
