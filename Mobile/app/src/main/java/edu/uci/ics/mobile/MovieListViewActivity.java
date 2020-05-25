package edu.uci.ics.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListViewActivity extends Activity {
    private String url;
    private ArrayList<Movie> movies;
    private Context context;
    private final int limit = 20;
    private int offset;
    private int maxOffset;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movielistview);
        context = getApplicationContext();

        Bundle b = getIntent().getExtras();
        query = b != null && b.getString("query") != null ? "&query=" + b.getString("query") : "";

        url = "https://184.72.155.237:8443/webapp/api/";

        getMovies();
    }

    public void getMovies() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, url + "movies?limit=" + limit + "&offset=" + offset + query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    offset = Integer.parseInt(jsonObject.getString("offset"));
                    maxOffset = Integer.parseInt(jsonObject.getString("rowCount"));
                    loadMovies(jsonObject.getJSONArray("data"));
                } catch (Exception e) {
                    Log.d("JSON error", e.toString());
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("loginview.error", error.toString());
            }
        });

        queue.add(loginRequest);
    }

    public void loadMovies(JSONArray moviesArr) {
        try {
            movies = new ArrayList<>();

            for (int i = 0; i < moviesArr.length(); i++) {
                JSONObject movieObj = moviesArr.getJSONObject(i);
                String movieId = movieObj.getString("movie_id");
                String movieTitle = movieObj.getString("movie_title");
                int movieYear = movieObj.getInt("movie_year");
                String movieDirector = movieObj.getString("movie_director");
                double movieRatings = movieObj.getDouble("movie_ratings");
                JSONArray genresArr = movieObj.getJSONArray("movie_genres");
                JSONArray starsArr = movieObj.getJSONArray("movie_stars");

                Movie movie = new Movie(movieId, movieTitle, movieYear, movieDirector, movieRatings);

                for (int j = 0; j < genresArr.length(); j++) {
                    JSONObject genreObj = genresArr.getJSONObject(j);
                    String genre = genreObj.getString("genre");
                    movie.addGenre(genre);
                }

                for (int j = 0; j < starsArr.length(); j++) {
                    JSONObject starObj = starsArr.getJSONObject(j);
                    String starId = starObj.getString("star_id");
                    String starName = starObj.getString("star_name");
                    Star star = new Star(starId, starName);
                    movie.addStar(star);
                }
                movies.add(movie);
            }

            MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = movies.get(position);

                    Intent intent = new Intent(view.getContext(), SingleMovieViewActivity.class);
                    intent.putExtra("movieId", movie.getId());
                    view.getContext().startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.d("JSON error", e.toString());
        }
    }

    public void onPrev(View view) {
        if (offset - limit >= 0) {
            offset -= limit;
            getMovies();
        }
    }

    public void onNext(View view) {
        if (offset + limit < maxOffset) {
            offset += limit;
            getMovies();
        }
    }
}