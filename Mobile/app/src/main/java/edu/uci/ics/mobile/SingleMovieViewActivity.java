package edu.uci.ics.mobile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class SingleMovieViewActivity extends Activity {
    private String url;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovieview);
        context = getApplicationContext();

        Bundle b = getIntent().getExtras();
        String id = b != null ? b.getString("movieId") : "";

        url = "https://184.72.155.237:8443/webapp/api/";

        getMovie(id);
    }

    public void getMovie(String id) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest starRequest = new StringRequest(Request.Method.GET, url + "movie?id=" + id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("Response", jsonObject.toString());
                            loadMovie(jsonObject.getJSONObject("data"));
                        } catch (Exception e) {
                            Log.d("JSON error", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("SingleMovieError.error", error.toString());
                    }
                }
            );

        Log.d("ID", id);
        queue.add(starRequest);
    }

    public void loadMovie(JSONObject movieObj) {
        try {
            String movieTitle = movieObj.getString("movie_title");
            int movieYear = movieObj.getInt("movie_year");
            String movieDirector = movieObj.getString("movie_director");
            String movieRatings = movieObj.getString("movie_ratings");
            JSONArray genresArr = movieObj.getJSONArray("movie_genres");
            JSONArray starsArr = movieObj.getJSONArray("movie_stars");

            TextView titleView = findViewById(R.id.title);
            TextView yearView = findViewById(R.id.year);
            TextView directorView = findViewById(R.id.director);
            TextView ratingView = findViewById(R.id.rating);
            ViewGroup genreListView = findViewById(R.id.genreList);
            ViewGroup starListView = findViewById(R.id.starList);

            titleView.setText(movieTitle);
            yearView.setText(movieYear + "");
            directorView.setText(movieDirector);
            ratingView.setText(movieRatings);

            LinearLayout.LayoutParams genrelparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            genrelparams.setMargins(0,5,5,5);

            for (int j = 0; j < genresArr.length(); j++) {
                JSONObject genreObj = genresArr.getJSONObject(j);
                String genre = genreObj.getString("genre");

                Button genreBtn = new Button(context);
                genreBtn.setLayoutParams(genrelparams);
                genreBtn.setText(genre);
                genreBtn.setTextColor(context.getResources().getColor(R.color.blue));
                genreBtn.setBackgroundColor(Color.WHITE);
                genreListView.addView(genreBtn);
            }

            LinearLayout.LayoutParams starlparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            starlparams.setMargins(5,5,0,5);

            for (int j = 0; j < starsArr.length(); j++) {
                JSONObject starObj = starsArr.getJSONObject(j);
                String starName = starObj.getString("star_name");

                Button starBtn = new Button(context);
                starBtn.setLayoutParams(starlparams);
                starBtn.setText(starName);
                starBtn.setTextColor(context.getResources().getColor(R.color.blue));
                starBtn.setBackgroundColor(Color.WHITE);
                starListView.addView(starBtn);
            }
        } catch (Exception e) {
            Log.d("JSON error", e.toString());
        }
    }
}