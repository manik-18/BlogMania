package com.manik.blogmania.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.manik.blogmania.BlogAdapter;
import com.manik.blogmania.BlogDetail;
import com.manik.blogmania.R;
import com.manik.blogmania.model.BlogPost;

import java.util.ArrayList;
import java.util.List;

public class MyBlogsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private List<BlogPost> blogList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.rv_blogs);
        blogList = new ArrayList<>();

        blogAdapter = new BlogAdapter(blogList, new BlogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BlogPost blogPost) {
                Intent intent = new Intent(getActivity(), BlogDetail.class);
                intent.putExtra("blogPost", (Parcelable) blogPost);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(blogAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        CollectionReference blogCollection = db.collection("blogs");

        Query query = blogCollection.whereEqualTo("author", currentUser.getEmail())
                .orderBy("formattedDate", Query.Direction.DESCENDING);

        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                // Handle errors
                return;
            }

            blogList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                BlogPost blogPost = document.toObject(BlogPost.class);
                if (blogPost != null) {
                    blogList.add(blogPost);
                }
            }

            blogAdapter.notifyDataSetChanged();
        });

        return view;
    }
}
