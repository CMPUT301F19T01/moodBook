package com.example.moodbook.ui.request;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moodbook.R;
import com.example.moodbook.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RequestFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RequestViewModel requestViewModel;
    private EditText requestText;
    private Button requestButton;
    private RequestHandler requestHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        requestViewModel =
                ViewModelProviders.of(this).get(RequestViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_request, container, false);
        /*final TextView textView = root.findViewById(R.id.text_send);
        requestViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        requestText = root.findViewById(R.id.usernameEditText);
        requestButton = root.findViewById(R.id.requestButton);

        mAuth = FirebaseAuth.getInstance();
        requestHandler = new RequestHandler(mAuth);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestHandler.verifyRequest(requestText.getText().toString())){ // check if username exists in db
                    Log.d("REQUEST", "exists");
                    Toast.makeText(root.getContext(), "Sent request",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(root.getContext(), "User does not exist",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;

    }
}