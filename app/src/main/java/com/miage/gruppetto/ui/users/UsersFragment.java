package com.miage.gruppetto.ui.users;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miage.gruppetto.MyusersListRecyclerViewAdapter;
import com.miage.gruppetto.R;
import com.miage.gruppetto.dummy.DummyContent;
import com.miage.gruppetto.ui.login.LoginActivity;
import com.miage.gruppetto.usersListFragment;

public class UsersFragment extends Fragment  {

    private FirebaseAuth mAuth;
    private View root;
    private usersListFragment.OnListFragmentInteractionListener mListener;
    private int mColumnCount = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_userslist_list, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Log.d("[INFO]", "mAuth.email:"+mAuth.getCurrentUser().getEmail());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyusersListRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof usersListFragment.OnListFragmentInteractionListener) {
            mListener = (usersListFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}