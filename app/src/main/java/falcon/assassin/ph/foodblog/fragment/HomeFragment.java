package falcon.assassin.ph.foodblog.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    private static final int TOTAL_MAX_RESULTS = 3;
    private int itemPos = 0;
    private boolean loadMorePosts = true;
    private String mLastKey;
    public String mprevKey;
    public String mOnResumePrevKey;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postList = new ArrayList<>();

        postListView = view.findViewById(R.id.post_list_view);
        postAdapter = new PostAdapter(postList);
        postListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        postListView.setAdapter(postAdapter);

        loadMorePosts = true;
        postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    if (loadMorePosts) {
                        itemPos = 0;

                        onLoadMorePosts();

                        Toast.makeText(container.getContext(), "Bottom reached", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Initialize database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        onloadPosts();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mLastKey = mOnResumePrevKey;
        //Log.d("tag", "onResume " + mprevKey);
    }

    @Override
    public void onStart() {
        super.onStart();

        mLastKey = mOnResumePrevKey;
        //Log.d("tag", "onStart " + mprevKey);

    }

    @Override
    public void onPause() {
        super.onPause();

        mLastKey = mOnResumePrevKey;
        //Log.d("tag", "onPause " + mprevKey);
    }

    private void onLoadMorePosts() {

            mprevKey = mLastKey;
            Log.d("tag", "Previous key" + mprevKey);
            final List<Post> tempPost = new ArrayList<>();

            Query query = myRef.child("post").orderByKey().endAt(mLastKey).limitToLast(TOTAL_MAX_RESULTS);

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot.exists()) {

                        Post post = dataSnapshot.getValue(Post.class);
                        postList.add(post);

                        itemPos++;
                        Log.d("tag", "Post list size: " + postList.size());
                        Log.d("tag", "Item count: " + itemPos);
                        if (itemPos == 1) {
                            Log.d("tag", "****************** Get the Key Item is 1  *******************");
                            mLastKey = dataSnapshot.getKey();
                            Log.i("tag", "Last Key" + mLastKey);
                            Log.d("tag", "****************** End of get Key *******************");
                        }


                        Log.i("tag", " Previous Key: " + mprevKey);
                        Log.i("tag", " Node Key " + dataSnapshot.getKey());

                        if (mprevKey.equalsIgnoreCase(dataSnapshot.getKey())) {
                            Log.d("tag", "****************** Remove item from the list *******************");
                            //Collections.reverse(postList);
                            postList.remove(postList.size() -1);
                            Log.d("tag", "Remove: " + dataSnapshot.getKey() + " in the list");
                            Log.d("tag", "Post list size: " + postList.size());
                            Log.d("tag", "****************** End of Remove *******************");
                        }

                        postAdapter.notifyDataSetChanged();

                    }
                    else {
                        loadMorePosts = false;
                    }


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
            });
    }

    protected  void onloadPosts() {

        Log.d("tag", "************* First Load of Post ********************");
        Query query = myRef.child("post").orderByKey().limitToLast(TOTAL_MAX_RESULTS);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);

                    itemPos++;
                    Log.d("tag", "Item count: " + itemPos);
                    if (itemPos == 1) {
                        mLastKey = dataSnapshot.getKey();
                        mOnResumePrevKey = mLastKey;
                        Log.d("tag", "First Key" + mLastKey);
                    }


                    postAdapter.notifyDataSetChanged();

                    //Collections.reverse(postList);
                    //postList.remove(postList.size() -1);
                }

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
        });

    }

}
