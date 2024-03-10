package com.manik.blogmania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manik.blogmania.model.BlogPost;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<BlogPost> blogList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BlogPost blogPost);
    }

    public BlogAdapter(List<BlogPost> blogList, OnItemClickListener listener) {
        this.blogList = blogList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogPost blogPost = blogList.get(position);

        if (blogPost != null) {
            holder.titleTextView.setText(blogPost.getTitle());
            holder.authorTextView.setText(blogPost.getAuthor());
            holder.dateTextView.setText(blogPost.getFormattedDate());

            if (blogPost.getImageUrl() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(blogPost.getImageUrl())
                        .into(holder.imageView);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(blogPost);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        TextView dateTextView;
        ImageView imageView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView9);
            authorTextView = itemView.findViewById(R.id.textView8);
            dateTextView = itemView.findViewById(R.id.t_date);
            imageView = itemView.findViewById(R.id.imageView3);
        }
    }
}
