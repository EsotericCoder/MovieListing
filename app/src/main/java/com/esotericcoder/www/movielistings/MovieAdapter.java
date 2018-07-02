package com.esotericcoder.www.movielistings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<Movie> mMovies; // Cached copy of words
    private View.OnLongClickListener longClickListener;
    private Context mContext;

    public MovieAdapter(List<Movie> mMovies, Context context, View.OnLongClickListener longClickListener) {
        this.mContext = context;
        this.mMovies = mMovies;
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mMovies.get(position).getVoteCount();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= 5) {
            return new MovieViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.popular_movie, parent, false));
        } else {
            return new MovieViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.less_popular_movie, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.movieTitle.setText(movie.getOriginalTitle());
        holder.movieOverview.setText(movie.getOverview());
        holder.moviePoster.setImageResource(R.drawable.movie_poster);
        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .resize(500,0)
                .into(holder.moviePoster);
        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/w1280" + movie.getBackdropPath())
                .resize(600,0)
                .transform(new RoundedCornersTransformation(10, 10))
                .into(holder.movieBackdrop);
        holder.itemView.setTag(movie);
        holder.itemView.setOnLongClickListener(longClickListener);

    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mMovies != null)
            return mMovies.size();
        else return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView movieTitle;
        private TextView movieOverview;
        private ImageView moviePoster;
        private ImageView movieBackdrop;

        private MovieViewHolder(View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieOverview = itemView.findViewById(R.id.movieOverview);
            moviePoster = itemView.findViewById(R.id.moviePoster);
            movieBackdrop = itemView.findViewById(R.id.movieBackdrop);
        }
    }
}
