package com.esotericcoder.www.movielistings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener{

    MovieAdapter adapterMovies;
    RecyclerView recyclerView;
    List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchMovies();
    }

    @Override
    public boolean onLongClick(View view) {

        Intent intent;
        Movie movie = (Movie) view.getTag();
        if(movie.getVoteAverage() >= 5){
            intent = new Intent(this, DetailedActivity.class);
        }else{
            intent = new Intent(this, QuickPlayActivity.class);
        }
        intent.putExtra("id", movie.getId());
        intent.putExtra("title", movie.getOriginalTitle());
        intent.putExtra("overview", movie.getOverview());
        intent.putExtra("rating", movie.getVoteAverage());
        intent.putExtra("release", movie.getReleaseDate());
        startActivity(intent);
        return true;
    }

    private void fetchMovies(){
        // Use OkHttpClient singleton
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/now_playing?api_key=9c69a8d528bc7457f72992f80720ecf1")
                .build();

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                JSONArray results;
                try {
                    String responseData = response.body().string();
                    // Convert response string to JSON
                    JSONObject resp = new JSONObject(responseData);
                    // Get the movies json array
                    results = resp.getJSONArray("results");

                    // Parse json array into array of model objects
                    moviesList = Movie.fromJson(results);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // For the example, you can show an error dialog or a toast
                            // on the main UI thread
                            adapterMovies = new MovieAdapter(moviesList, MainActivity.this, MainActivity.this);
                            recyclerView = findViewById(R.id.recyclerview);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerView.setAdapter(adapterMovies);
                        }
                    });

                } catch (JSONException e) {

                }
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

}
