package falcon.assassin.ph.foodblog.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import falcon.assassin.ph.foodblog.R;
import falcon.assassin.ph.foodblog.model.Post;
import falcon.assassin.ph.foodblog.view.adapter.post.PostAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView postListView;
    private List<Post> postList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private PostAdapter postAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postList = new ArrayList<>();

        postListView = view.findViewById(R.id.post_list_view);
        postAdapter = new PostAdapter(postList);
        postListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        postListView.setAdapter(postAdapter);


        // Initialize database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("post").orderByChild("timeStamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();

/*                Log.d("tag", postList.get(0).getThumbnailUri());
                Log.d("tag", postList.get(0).getUserId());
                Log.d("tag", postList.get(0).getOriginalImageUri());
                Log.d("tag", postList.get(0).getDescription());*/

                Log.d("tag", "success retrive data");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });/*.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if  (dataSnapshot.exists()) {

                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);

                    postAdapter.notifyDataSetChanged();

*//*                    Log.d("tag", post.getDescription());
                    Log.d("tag", post.getOriginalImageUri());
                    Log.d("tag", post.getUserId());
                    Log.d("tag", post.getThumbnailUri());*//*
                    Log.d("tag", postList.get(0).getDescription());

                    Log.d("tag", "success retrive data");

                }
                else {

                    Log.d("tag", "Something went wrong");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        // Inflate the layout for this fragment
        return view;
    }

}
