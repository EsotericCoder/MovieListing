package com.esotericcoder.www.movielistings;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuickPlayActivity extends YouTubeBaseActivity {
    @BindView(R.id.detailedTitle) TextView title;
    @BindView(R.id.detailedReleaseDate) TextView releaseDate;
    @BindView(R.id.detailedOverview) TextView overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_play);
        ButterKnife.bind(this);

        title.setText(getIntent().getStringExtra("title"));
        overview.setText(getIntent().getStringExtra("overview"));
        releaseDate.setText(getIntent().getStringExtra("release"));
        String videoId = getIntent().getStringExtra("id");

        // Use OkHttpClient singleton
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/"+ videoId +"/trailers?api_key=9c69a8d528bc7457f72992f80720ecf1")
                .build();

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    // Convert response string to JSON
                    JSONObject resp = new JSONObject(responseData);
                    // Get the movies json array
                    JSONArray results = resp.getJSONArray("youtube");
                    JSONObject first = results.getJSONObject(0);
                    String trailer = first.getString("source");

                    loadVideo(trailer);

                } catch (JSONException e) {

                }
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    void loadVideo(String videoId) {
        class YoutubeRunnable implements Runnable {
            String str;
            YoutubeRunnable(String s) { str = s; }
            public void run() {
                YouTubePlayerView youTubePlayerView = findViewById(R.id.player);
                youTubePlayerView.initialize("AIzaSyCgU6vUO2ytnIjP41F_RR3tPeQuCNqWywk",
                        new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                YouTubePlayer youTubePlayer, boolean b) {

                                // do any work here to cue video, play video, etc.
                                youTubePlayer.cueVideo(str);
                            }

                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                YouTubeInitializationResult youTubeInitializationResult) {

                            }
                        });
            }
        }
        runOnUiThread(new YoutubeRunnable(videoId));
    }
}
