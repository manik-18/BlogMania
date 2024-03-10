package com.manik.blogmania;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manik.blogmania.model.BlogPost;

public class EditBlogPostActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonPublish;
    private ProgressBar progressBar;
    private ImageView imageViewBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blog_post);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonPublish = findViewById(R.id.buttonPublish);
        progressBar = findViewById(R.id.progress);
        imageViewBlog = findViewById(R.id.imageViewBlog);

        BlogPost blogPost = getIntent().getParcelableExtra("blogPost");

        if (blogPost != null) {
            editTextTitle.setText(blogPost.getTitle());
            editTextDescription.setText(blogPost.getDescription());

            Glide.with(this)
                    .load(blogPost.getImageUrl())
                    .into(imageViewBlog);

            buttonPublish.setOnClickListener(v -> updateBlogPost(blogPost.getId()));
        } else {
            Toast.makeText(this, "Cannot edit blog post. Missing data.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateBlogPost(String id) {
        String updatedTitle = editTextTitle.getText().toString().trim();
        String updatedDescription = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(updatedTitle) || TextUtils.isEmpty(updatedDescription)) {
            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference blogRef = db.collection("blogs").document(id);

        blogRef.update("title", updatedTitle, "description", updatedDescription)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditBlogPostActivity.this, "Blog post updated successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    progressBar.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditBlogPostActivity.this, "Error updating blog post", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}
