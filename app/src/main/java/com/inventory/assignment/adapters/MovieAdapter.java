package com.inventory.assignment.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inventory.assignment.R;
import com.inventory.assignment.model.MovieModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.inventory.assignment.R.drawable.ic_broken_image_gray_24dp;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;
    private List<MovieModel> mList;

    private static final String TAG = "MovieAdapter";

    public MovieAdapter(Context context, List<MovieModel> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        MovieModel model = mList.get(i);

        viewHolder.tvTitle.setText(model.getTitle());
        viewHolder.tvRating.setText("" + model.getVoteAverage());
        viewHolder.tvRelease.setText("" + model.getReleaseDate());

        Log.d(TAG, "onBindViewHolder: " + model.getVoteAverage());

        String posterPath = model.getPosterPath();
        if (posterPath == null) {
            viewHolder.tvTitle.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(model.getPosterPath()).config(Bitmap.Config.RGB_565).
                placeholder(ic_broken_image_gray_24dp)
                .into(viewHolder.imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.imgThumbnail.setVisibility(View.VISIBLE);
                        viewHolder.tvRating.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        viewHolder.imgThumbnail.setImageResource(ic_broken_image_gray_24dp);
                        viewHolder.tvTitle.setVisibility(View.VISIBLE);
                        viewHolder.tvRating.setVisibility(View.VISIBLE);
                    }
                });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumbnail;
        TextView tvTitle, tvRating, tvRelease;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.iv_thumb);
            tvTitle = itemView.findViewById(R.id.tv_label);
            tvRating = itemView.findViewById(R.id.tv_rate);
            tvRelease = itemView.findViewById(R.id.tv_release);
        }
    }
}
