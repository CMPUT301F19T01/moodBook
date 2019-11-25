package com.example.moodbook.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moodbook.DBFriend;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageAdapter extends ArrayAdapter {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    // connect to DB
    private DBFriend friendDB;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private ArrayList<Chat> chat;
    private Context context;
    private ListView friendListView;
    TextView messageShow;


    public MessageAdapter(Context context, ArrayList<Chat> chat) {
        super(context, 0, chat);
        this.chat = chat;
        this.context = context;
    }

    public MessageAdapter(Context context) {
        this(context, new ArrayList<Chat>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(getItemViewType(position) == MSG_RIGHT){
            view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);

        }
        Chat _chat = chat.get(position);
        messageShow.setText(_chat.getMessage());

        return view;
    }


    @Override
    public void add(@Nullable Object object) {
        if(object == null || object instanceof Chat) {
            Chat item = (Chat)object;
            chat.add(item);
//            Collections.sort(Chat);
//            // notify item added
//            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        chat.clear();
        // notify list is cleared
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(chat.get(position).getSender().equals(user.getUid())) {
            return MSG_RIGHT;
        }
        else{
            return MSG_LEFT;
        }
    }
}
