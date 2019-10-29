package com.example.moodbook.ui.request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.moodbook.R;
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
        View root = inflater.inflate(R.layout.fragment_request, container, false);
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
                requestHandler.verifyRequest(requestText.getText().toString());
            }
        });
        return root;

    }
}