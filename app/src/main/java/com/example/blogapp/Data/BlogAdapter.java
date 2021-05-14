package com.example.blogapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapp.Model.Blog;
import com.example.blogapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        String imageURL = null;

        holder.title.setText(blog.getTitle());
        holder.description.setText(blog.getDesc());

        String formattedDate = DateFormat.getDateInstance().format(new Date(Long.valueOf(blog.getTimeStamp())).getTime());
        holder.timeStamp.setText(formattedDate);
        imageURL = blog.getImage();


        //use picasso library to load image
        Picasso.get().load(imageURL).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;
        public TextView timeStamp;
        public ImageView image;
        public String userId;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.postTitleList);
            description = itemView.findViewById(R.id.postTextList);
            timeStamp = itemView.findViewById(R.id.timestampList);
            image = itemView.findViewById(R.id.postImageList);
            userId = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Todo: go to next activity i.e. the detail activity of the blog
                }
            });

        }
    }
}
