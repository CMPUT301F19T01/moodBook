/**
 * Reference:
 * Linking navigation - https://guides.codepath.com/android/fragment-navigation-drawer
 */
package com.example.moodbook;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

import com.example.moodbook.ui.Request.RequestFragment;
import com.example.moodbook.ui.friendMood.friendMoodFragment;
import com.example.moodbook.ui.home.HomeFragment;
import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment;
import com.example.moodbook.ui.myMoodMap.MyMoodMapFragment;
import com.example.moodbook.ui.myRequests.myRequestsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;



//https://guides.codepath.com/android/fragment-navigation-drawer  - used for linking navigation

/**
 * The Main Activity is set to HomeFragment as default, which in turn lists the Mood History list
 */

public class MainActivity extends AppCompatActivity   {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(

                R.id.nav_myMood,R.id.nav_FriendMood,
                R.id.nav_addFriends, R.id.nav_myRequests, R.id.nav_myMoodMap, R.id.nav_myFriendMoodMap, R.id.nav_logout)
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
            case R.id.nav_FriendMood:
                fragmentClass = friendMoodFragment.class;
                toolbar.setTitle("Friend Mood History");
                break;
            case R.id.nav_addFriends:
                fragmentClass = RequestFragment.class;
                toolbar.setTitle("Add Friends");
                break;
            case R.id.nav_myRequests:
                fragmentClass = myRequestsFragment.class;
                toolbar.setTitle("Friend Requests");
                break;
            case R.id.nav_myMoodMap:
                fragmentClass = MyMoodMapFragment.class;
                toolbar.setTitle("Mood History Map");
                break;
            case R.id.nav_myFriendMoodMap:
                fragmentClass = MyFriendMoodMapFragment.class;
                toolbar.setTitle("Friend History Map");
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

<<<<<<< HEAD
=======

    /**
     * This method allows the user to log out of the application.
     */

>>>>>>> prod
    private void logout(){
        mAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
