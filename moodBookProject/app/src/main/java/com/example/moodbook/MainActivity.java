/**
 * Reference:
 * Linking navigation - https://guides.codepath.com/android/fragment-navigation-drawer
 */
package com.example.moodbook;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.moodbook.ui.Request.RequestFragment;
import com.example.moodbook.ui.followers.followersFragment;
import com.example.moodbook.ui.chat.ChatFragment;
import com.example.moodbook.ui.friendMood.FriendMoodFragment;
import com.example.moodbook.ui.home.HomeFragment;
import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
import com.example.moodbook.ui.myMoodMap.MyMoodMapFragment;
import com.example.moodbook.ui.myRequests.myRequestsFragment;
import com.example.moodbook.ui.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


//https://guides.codepath.com/android/fragment-navigation-drawer  - used for linking navigation

/**
 * The Main Activity is set to HomeFragment as default, which in turn lists the Mood History list
 */

public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private static String name;
    private String email;
    private  ImageView profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        name = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        profile = findViewById(R.id.profile);

        name = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            while (name == null){
                name = user.getDisplayName();
            }
            email = user.getEmail();
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
        }


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.currentEmail, R.id.currentEmail,R.id.nav_profile,
                R.id.nav_myMood,R.id.nav_friendMood,
                R.id.nav_myRequests, R.id.nav_Followers, R.id.nav_myMoodMap, R.id.nav_myFriendMoodMap,
                R.id.nav_chat,
                R.id.nav_request, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem, drawer);
                        return true;
                    }
                }
        );
        TextView profileUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.currentUsername);
        TextView profileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.currentEmail);
        final ImageView profilePicture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile);
        profileUserName.setText(name);
        profileEmail.setText(email);
        if (name!=null) {
            // Reference to an image file in Cloud Storage
             StorageReference storageReference = FirebaseStorage.getInstance().getReference();
             storageReference.child("profilepics/" + name + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()/* context */)
                                .load(uri)
                                .centerCrop()
                                .into(profilePicture);
                    }
                })
                     .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        profilePicture.setImageResource(R.drawable.purpleprofile);
                    }
                });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /**
     * This method is for switching between different intent activities


    /**
     * This method creates a new fragment based on the menu option selected
     * @param menuItem
     * @param drawer
     */
    public void selectDrawerItem(MenuItem menuItem, DrawerLayout drawer){
        Fragment fragment;
        Class fragmentClass;
        switch(menuItem.getItemId()){
            case R.id.nav_friendMood:
                fragmentClass = FriendMoodFragment.class;
                toolbar.setTitle("Friend Moods");
                break;
            case R.id.nav_Followers:
                fragmentClass = followersFragment.class;
                toolbar.setTitle("My Followers");
                break;
            case R.id.nav_myMoodMap:
                fragmentClass = MyMoodMapFragment.class;
                toolbar.setTitle("Mood History Map");
                break;
            case R.id.nav_myFriendMoodMap:
                fragmentClass = MyFriendMoodMapFragment.class;
                toolbar.setTitle("Friend History Map");
                break;
            /*case R.id.nav_chat:
                fragmentClass = ChatFragment.class;
                toolbar.setTitle("Chat");
                break;*/
            case R.id.nav_myFriends:
                fragmentClass = MyFriendsFragment.class;
                toolbar.setTitle("My Friends");
                break;
            case R.id.nav_request:
                fragmentClass = RequestFragment.class;
                toolbar.setTitle("Add Friends");
                break;
            case R.id.nav_myRequests:
                fragmentClass = myRequestsFragment.class;
                toolbar.setTitle("Friend Requests");
                break;
            case R.id.nav_profile:
                fragmentClass =null;
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                startActivity(intent);
                Log.d("Activity:" , "My profile");
                Toast.makeText(getApplicationContext(),
                        "Clicked Profile Page " ,
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                fragmentClass = null;
                logout();
                break;
            default:
                fragmentClass = HomeFragment.class;
                toolbar.setTitle("Mood History");
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
            drawer.closeDrawers();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout(){
        mAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public static String getUsername(){
        return name;
    }

}