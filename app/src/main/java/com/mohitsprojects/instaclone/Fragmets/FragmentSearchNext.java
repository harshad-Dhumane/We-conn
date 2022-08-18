package com.mohitsprojects.instaclone.Fragmets;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mohitsprojects.instaclone.Activities.User;
import com.mohitsprojects.instaclone.Adaptors.UserAdapter;
import com.mohitsprojects.instaclone.Models.RandomSearchPostModel;
import com.mohitsprojects.instaclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearchNext extends Fragment {
    private RecyclerView recyclerView;
    private List<RandomSearchPostModel> RandomPostList= new ArrayList<>();
    ProgressBar progressBar;

    private List<User> mUsers;
    private UserAdapter userAdapter;

    //hereon
    private LinearLayout searchBarLL;
    private ImageView backIV;
//    private TagAdapter tagAdapter;

    //    private SearchView searchView;
    private EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_next, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        recyclerView = view.findViewById(R.id.idRandomPostRVNext);
        progressBar = view.findViewById(R.id.idRP_PBNext);
        search_bar = view.findViewById(R.id.searchNext);
        searchBarLL = view.findViewById(R.id.linearLayout2Next);
        backIV = view.findViewById(R.id.backButtonSearchNext);

        mUsers = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        search_bar.setCursorVisible(true);


        readUsers();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        backIV.setOnClickListener(i ->{
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        });

    }


    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())){
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(getActivity(), mUsers, true);
                    recyclerView.setAdapter(userAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void Filter(String text){
        ArrayList<User> temp = new ArrayList();
        ArrayList<User> nList = new ArrayList<>();
        text = text.toLowerCase();
        for(User d: mUsers){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            String name = d.name.toLowerCase();
            String userName = d.userName.toLowerCase();
            if (name.startsWith(text) || userName.startsWith(text)){
                temp.add(d);
            }else if(name.contains(text) || userName.contains(text)){
                nList.add(d);
            }
        }
        temp.addAll(nList);
        //update recyclerview

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getActivity(), temp, true);
        recyclerView.setAdapter(userAdapter);
    }
}