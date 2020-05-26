package edu.uci.ics.mobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

import android.widget.LinearLayout.LayoutParams;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.movielistitem, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movielistitem, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView yearView = view.findViewById(R.id.year);
        TextView directorView = view.findViewById(R.id.director);
        TextView ratingView = view.findViewById(R.id.rating);

        ViewGroup genreListView = view.findViewById(R.id.genreList);
        ViewGroup starListView = view.findViewById(R.id.starList);

        titleView.setText(movie.getName());
        yearView.setText(movie.getYear() + "");
        directorView.setText(movie.getDirector());
        ratingView.setText(movie.getRating());

        LayoutParams genrelparams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        genrelparams.setMargins(0,5,5,5);


        for (String g : movie.getGenres()) {
            Button genreBtn = new Button(context);
            genreBtn.setOnClickListener(onClickGenreButton(genreBtn));
            genreBtn.setLayoutParams(genrelparams);
            genreBtn.setText(g);
            genreBtn.setTextColor(context.getResources().getColor(R.color.blue));
            genreBtn.setBackgroundColor(Color.WHITE);
            genreListView.addView(genreBtn);
        }

        LayoutParams starlparams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        starlparams.setMargins(5,5,0,5);

        for (Star s : movie.getStars()) {
            Button starBtn = new Button(context);
            starBtn.setOnClickListener(onClickStarButton(starBtn, s.getId()));
            starBtn.setLayoutParams(starlparams);
            starBtn.setText(s.getName());
            starBtn.setTextColor(context.getResources().getColor(R.color.blue));
            starBtn.setBackgroundColor(Color.WHITE);
            starListView.addView(starBtn);
        }

        return view;
    }

    View.OnClickListener onClickGenreButton(final Button btn)  {
        return new View.OnClickListener() {
            public void onClick(View v) { }
        };
    }

    View.OnClickListener onClickStarButton(final Button btn, String starId)  {
        return new View.OnClickListener() {
            public void onClick(View v) { }
        };
    }
}