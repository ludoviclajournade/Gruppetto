package com.miage.gruppetto.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miage.gruppetto.R;

public class UserFragment extends Fragment {

    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Log.d("[INFO]", "mAuth.email:"+mAuth.getCurrentUser().getEmail());


        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        root = inflater.inflate(R.layout.fragment_user, container, false);

        Button button = (Button) root.findViewById(R.id.button_saveUser);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("[INFO]", "UserFragment:onCreateView:OnClick(button_saveUser)");
                updateUser();
            }
        });

        initUI();
        return root;
    }

    private void updateUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        EditText email = (EditText) root.findViewById(R.id.editText_email);
        EditText password = (EditText) root.findViewById(R.id.editText_password);
        EditText passwordCheck = (EditText) root.findViewById(R.id.editText_passwordcheck);

        if (password.getText().toString().equals(passwordCheck.getText().toString())) {
            user.updateEmail(email.getText().toString());
            user.updatePassword(password.getText().toString());
            Toast.makeText(this.getContext(), "Mise à jour réussie", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getContext(), "Mot de passe différents", Toast.LENGTH_LONG).show();
        }
    }

    private void initUI() {

        FirebaseUser user = mAuth.getCurrentUser();

        EditText email = (EditText) root.findViewById(R.id.editText_email);

        email.setText(user.getEmail());

    }
}