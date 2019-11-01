package com.example.moodbook.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UsernameList {

    private ArrayList<String> list;
    private FirebaseFirestore db;

    public UsernameList(FirebaseFirestore db){
        this.list = new ArrayList<>();
        this.db = db;
    }

    /**
     * @return
     *      ArrayList of usernames that have been registered
     */
    public ArrayList<String> getUsernameList(){ return this.list; }

    /**
     * This method verifys a given username for uniqueness and length
     * @param username
     * @return
     *      true: username is unique and > length 0
     *      false: username is not unique and/or is not > length 0
     */
    public Boolean verifyUsername(String username){
        return (!list.contains(username) && username.length() > 0); }

    /**
     * This method gets all the currently used usernames
     * @return
     *      an ArrayList of usernames
     * https://firebase.google.com/docs/auth/android/manage-users#update_a_users_profile Used to update username
     */
    public void updateUsernameList(){
        list.clear();
        db.collection("usernamelist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                                Log.d("TESTINGG", document.getId());
                            }
                        } else {
                            Log.w("Email", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This method verifys that a username is registered in the db
     * @param username
     * @return
     *      true: username exists
     *      false: username does not exist/username is the same as the user's username
     */
    public Boolean isUser(String username){
        Boolean result = list.contains(username);
        this.updateUsernameList();
        return result;
    }
}
