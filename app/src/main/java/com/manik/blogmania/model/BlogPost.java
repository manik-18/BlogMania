package com.manik.blogmania.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlogPost implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String author;
    private String imageUrl;
    private Date date;

    public BlogPost() {
        this.date = new Date();
    }

    public BlogPost(String title, String description, String author, String imageUrl, String id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.imageUrl = imageUrl;
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        return sdf.format(date);
    }

    protected BlogPost(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        author = in.readString();
        imageUrl = in.readString();
        long dateMillis = in.readLong();
        date = new Date(dateMillis);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeString(imageUrl);
        dest.writeLong(date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<BlogPost> CREATOR = new Parcelable.Creator<BlogPost>() {
        @Override
        public BlogPost createFromParcel(Parcel in) {
            return new BlogPost(in);
        }

        @Override
        public BlogPost[] newArray(int size) {
            return new BlogPost[size];
        }
    };
}