package com.example.moodbook;


import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;

import com.example.moodbook.ui.addFriends.addFriendFragment;
import com.example.moodbook.ui.friendMood.friendMoodFragment;
import com.example.moodbook.ui.home.HomeFragment;
import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.login.UsernameFragment;
import com.example.moodbook.ui.myFriendMoodMap.myFriendMoodMapFragment;
import com.example.moodbook.ui.myMood.myMoodFragment;
import com.example.moodbook.ui.myMoodMap.myMoodMapFragment;
import com.example.moodbook.ui.myRequests.myRequestsFragment;
import com.example.moodbook.ui.request.RequestFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity   {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(MainActivity.this, " in main" ,Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
//        setupDrawerContent(navigationView);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Toast.makeText(this, "juuust checking", Toast.LENGTH_SHORT).show();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(

                R.id.nav_myMood,R.id.nav_FriendMood,
                R.id.nav_addFriendsMood, R.id.nav_myRequests, R.id.nav_myMoodMap, R.id.nav_myFriendMoodMap, R.id.nav_logout)
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


        /* Mood History */
        // Add mood: when floating add button is clicked, start add activity to add a new mood item
        /*FloatingActionButton add_mood_button = findViewById(R.id.mood_history_add_button);
        add_mood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("click");
                Intent intent = new Intent(MainActivity.this, CreateMoodActivity.class);
                startActivity(intent);
            }
        });
        // Set up ListView
        moodListView = findViewById(R.id.mood_history_listView);
        moodAdapter = new CustomAdapter(this, new ArrayList<Mood>());
        // test adding
        Mood testItem = new Mood(null, null, "happy");
        moodAdapter.add(testItem);
        moodListView.setAdapter(moodAdapter);
        // Edit mood: When a mood item is clicked, start edit activity to edit the selected mood
        moodListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the selected mood
                Mood selectedMood = (Mood)adapterView.getItemAtPosition(i);
                System.out.println("Selected: "+selectedMood.getEmotionText());
                // TODO
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//         super.onOptionsItemSelected(item);
//
//         if (item.getItemId() == R.id.action_settings){
//            logout();
//         }
//         return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void selectDrawerItem(MenuItem menuItem, DrawerLayout drawer){
        Fragment fragment = new Fragment();
        Class fragmentClass;
        switch(menuItem.getItemId()){
            case R.id.nav_FriendMood:
                fragmentClass = friendMoodFragment.class;
                Toast.makeText(MainActivity.this, " friends Mood clicked",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_addFriendsMood:
                fragmentClass = addFriendFragment.class;
                Toast.makeText(MainActivity.this, "add friends clicked",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_myRequests:
                Toast.makeText(MainActivity.this, "Request clicked",
                        Toast.LENGTH_SHORT).show();
                fragmentClass = RequestFragment.class;
//                Toast.makeText(MainActivity.this, "Request here",
//                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_myMoodMap:
                fragmentClass = myMoodMapFragment.class;
                Toast.makeText(MainActivity.this, "My mood map clicked",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_myFriendMoodMap:
                fragmentClass = myFriendMoodMapFragment.class;
                Toast.makeText(MainActivity.this, " friend mood map clicked",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                logout();
                Toast.makeText(MainActivity.this, " log out",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                fragmentClass = HomeFragment.class;
        }
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawers();

    }






//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                drawer.openDrawer(GravityCompat.START);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//
//                        selectDrawerItem(menuItem);
//
//
//                        return true;
//                    }
//                });
//    }
//
//
//
//    public void selectDrawerItem(MenuItem menuItem) {
//        Toast.makeText(this, "in nav", Toast.LENGTH_SHORT).show();
//
//        // Create a new fragment and specify the fragment to show based on nav item clicked
//        Fragment fragment = null;
//        Class fragmentClass;
//        switch(menuItem.getItemId()) {
//            case R.id.nav_request:
//                                Toast.makeText(this, "Request", Toast.LENGTH_SHORT).show();
//
//                fragmentClass = RequestFragment.class;
//                break;
//            case R.id.nav_myMood:
//                                Toast.makeText(this, "mymood", Toast.LENGTH_SHORT).show();
//
//                fragmentClass = HomeFragment.class;
//                break;
//            case R.id.nav_FriendMood:
//                Toast.makeText(this, "frnd mood", Toast.LENGTH_SHORT).show();
//
//                fragmentClass = friendMoodFragment.class;
//                break;
//        }

//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.nav_myMood, fragment).commit();
//
//        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(true);
//        // Set action bar title
//        setTitle(menuItem.getTitle());
//        // Close the navigation drawer
//        drawer.closeDrawers();}




//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.nav_FriendMood:
//                Toast.makeText(this, "FM", Toast.LENGTH_SHORT).show();
//
//                break;
//            case R.id.nav_myRequests:
//                Toast.makeText(this, "Request", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_logout:
//                Toast.makeText(this, "logout clicked", Toast.LENGTH_SHORT).show();
//                logout();
//                return true;
//        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private void logout(){
        mAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }






}
