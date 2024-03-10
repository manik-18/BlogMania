package com.manik.blogmania;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manik.blogmania.model.BlogPost;

public class BlogDetail extends AppCompatActivity {

    private TextView titleTextView;
    private TextView authorTextView;
    private TextView descriptionTextView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        ImageView blogImageView = findViewById(R.id.imageView3);
        titleTextView = findViewById(R.id.textView5);
        authorTextView = findViewById(R.id.textView4);
        descriptionTextView = findViewById(R.id.textView6);
        ImageView backImage = findViewById(R.id.imageView4);

        backImage.setOnClickListener(v -> {
            startActivity(new Intent(BlogDetail.this, HomeActivity.class));
            finish();
        });

        BlogPost blogPost = getIntent().getParcelableExtra("blogPost");

        FloatingActionButton share = findViewById(R.id.floatingActionButton);
        FloatingActionButton edit = findViewById(R.id.edit_blog);
        FloatingActionButton delete = findViewById(R.id.delete_blog);
        share.setOnClickListener(v -> shareBlogPost());
        edit.setOnClickListener(v -> editBlogPost(blogPost.getId()));
        delete.setOnClickListener(v -> showDeleteConfirmationDialog(blogPost.getId()));

        if (blogPost != null) {
            titleTextView.setText(blogPost.getTitle());
            authorTextView.setText(blogPost.getAuthor());
            descriptionTextView.setText(blogPost.getDescription());

            if (currentUser != null && currentUser.getEmail() != null
                    && currentUser.getEmail().equals(blogPost.getAuthor())) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }

            Glide.with(this)
                    .load(blogPost.getImageUrl())
                    .into(blogImageView);
        }
    }

    private void editBlogPost(String id) {
        BlogPost blogPost = getIntent().getParcelableExtra("blogPost");

        if (blogPost != null) {
            Intent intent = new Intent(BlogDetail.this, EditBlogPostActivity.class);
            intent.putExtra("blogPost", blogPost);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Cannot edit blog post. Missing data.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showDeleteConfirmationDialog(String blogPostId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this blog post?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteBlogPost(blogPostId);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBlogPost(String id) {
        CollectionReference blogCollection = db.collection("blogs");
        DocumentReference blogRef = blogCollection.document(id);
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + id);
        try {
            blogRef.delete()
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BlogDetail.this, "Error deleting blog post", Toast.LENGTH_SHORT).show();
                    });

            imageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BlogDetail.this, "Error deleting image", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(BlogDetail.this, "Error deleting blog", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Blog Post deleted successful", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void shareBlogPost() {
        String title = titleTextView.getText().toString();
        String author = authorTextView.getText().toString();
        String description = descriptionTextView.getText().toString();
        String shareText = "Check out this blog post by " + author + "on BlogMania:\n\n"
                + title + "\n\n"
                + description;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
