package com.manik.blogmania.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manik.blogmania.HomeActivity;
import com.manik.blogmania.R;
import com.manik.blogmania.model.BlogPost;

import java.util.Calendar;
import java.util.Locale;

public class PublishFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView upload;
    private Button publish;
    private EditText title, description;
    private ProgressBar progressBar;

    private String author_name;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);

        upload = view.findViewById(R.id.imageViewBlog);
        title = view.findViewById(R.id.editTextTitle);
        description = view.findViewById(R.id.editTextDescription);
        publish = view.findViewById(R.id.buttonPublish);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        author_name = user.getEmail();
        progressBar = view.findViewById(R.id.progress);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri == null || title.getText().toString().isEmpty() ||
                        description.getText().toString().isEmpty()) {
                    showToast("Please fill in all fields and select an image");
                } else {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    String currentDateAndTime = getCurrentDateAndTime();
                    String userEmail = author_name.replace("@", "_").replace(".", "_");
                    String customDocumentId = "blog_" + currentDateAndTime + "_" + userEmail;
                    String imageName = customDocumentId;
                    String path = "images/";
                    StorageReference imageRef = storageRef.child(path + imageName);

                    // Upload the image to Firebase Storage
                    progressBar.setVisibility(View.VISIBLE);
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully, now save its URL to Firestore
                                imageRef.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            String imageUrl = uri.toString();
                                            String blogTitle = title.getText().toString();
                                            String blogDescription = description.getText().toString();

                                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                                            // Create a new document in the "blogs" collection with the custom document ID
                                            DocumentReference blogRef = firestore.collection("blogs").document(customDocumentId);

                                            BlogPost blog = new BlogPost(blogTitle, blogDescription, author_name, imageUrl, customDocumentId);

                                            blogRef.set(blog)
                                                    .addOnSuccessListener(aVoid -> {
                                                        showToast("Blog post published");
                                                        progressBar.setVisibility(View.GONE);
                                                        clearFields();
                                                        startActivity(new Intent(getContext(), HomeActivity.class));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        showToast("Error publishing blog post: " + e.getMessage());
                                                        progressBar.setVisibility(View.GONE);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            showToast("Error getting image URL: " + e.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                showToast("Error uploading image: " + e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            });
                }
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                upload.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        return String.format(Locale.US, "%04d%02d%02d%02d%02d%02d%03d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND));
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        upload.setImageResource(R.drawable.upload);
        title.getText().clear();
        description.getText().clear();
        selectedImageUri = null;
    }
}
