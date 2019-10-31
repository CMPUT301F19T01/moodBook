package com.example.moodbook;


import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;

import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.login.UsernameFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
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


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(

                R.id.nav_myMood,R.id.nav_FriendMood,
                R.id.nav_addFriendsMood, R.id.nav_myRequests, R.id.nav_myMoodMap, R.id.nav_myFriendMoodMap, R.id.nav_logout, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


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
//    displayView(R.id.nav_logout);
//    }
//    public void displayView(int viewId) {
//
//        Fragment fragment = null;
//        String title = getString(R.string.app_name);
//        switch (viewId){
//            case R.id.nav_logout:
//                fragment = new UsernameFragment();
//                break;
//        }
//
//        if (fragment != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.nav_host_fragment, fragment);//double check the which id to use
//            ft.commit();
//        }
//
//        // set the toolbar title
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
    }
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        displayView(item.getItemId());
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId() == R.id.action_settings){
            logout();
         }
         return true;
    }
    private void logout(){
        mAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
