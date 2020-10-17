package com.project.dailydrizzle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ext.cast.MediaItem;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.material.tabs.TabLayout;
import com.project.dailydrizzle.adapters.VideoAdapter;
import com.project.dailydrizzle.api.ApiConfig;
import com.project.dailydrizzle.api.ApiConnection;
import com.project.dailydrizzle.api.OnApiResponseListener;
import com.project.dailydrizzle.models.Category;
import com.project.dailydrizzle.models.PlayerManager;
import com.project.dailydrizzle.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.project.dailydrizzle.models.DemoUtil.MIME_TYPE_SS;
import static com.project.dailydrizzle.models.DemoUtil.MIME_TYPE_VIDEO_MP4;

public class MainActivity extends AppCompatActivity implements OnItemClickListenerVideo {git
    private PlayerView localPlayerView;
    private PlayerControlView castControlView;
    private PlayerManager playerManager;
    private CastContext castContext;

    private TabLayout tab_layout;
    private ArrayList<Category> categoryList;
    VideoAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Video> videoList;
    int categoryId;
    // Activity lifecycle methods.
    Category category;
    boolean isFirst = true, isCategoryChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Getting the cast context later than onStart can cause device discovery not to take place.
        try {
            castContext = CastContext.getSharedInstance(this);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof DynamiteModule.LoadingException) {
                    setContentView(R.layout.cast_context_error);
                    return;
                }
                cause = cause.getCause();
            }
            // Unknown error. We propagate it.
            throw e;
        }

        setContentView(R.layout.activity_main);

        categoryList = new ArrayList<>();
        tab_layout = findViewById(R.id.tab_layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        getCategories();
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                isCategoryChanged = true;
                category = categoryList.get(tab.getPosition());
                categoryId = Integer.parseInt(category.getId());

                adapter = new VideoAdapter(MainActivity.this, category.getVideoArrayList(), MainActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        localPlayerView = findViewById(R.id.local_player_view);
        localPlayerView.requestFocus();

        castControlView = findViewById(R.id.cast_control_view);


    }

    @Override
    public void onItemClick(Video video, int pos) {

        if (isCategoryChanged) {

            PlayerManager.concatenatingMediaSource.clear();
            PlayerManager.mediaQueue.clear();
            videoList = category.getVideoArrayList();
            isCategoryChanged = false;
            for (int v = 0; v < videoList.size(); v++) {
                Video vid = videoList.get(v);

                playerManager.addItem(new MediaItem.Builder()
                        .setUri(vid.getVideoUrl())
                        .setTitle(vid.getTitle())
                        .setMimeType(categoryId == 37 ? MIME_TYPE_SS : MIME_TYPE_VIDEO_MP4)
                        .build());
                if (v == pos) {
                    playerManager.selectQueueItem(pos);
                }

            }


        } else {
            playerManager.selectQueueItem(pos);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (castContext == null) {
            // There is no Cast context to work with. Do nothing.
            return;
        }
        playerManager =
                new PlayerManager(

                        localPlayerView,
                        castControlView,
                        /* context= */ this,
                        castContext);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (castContext == null) {
            // Nothing to release.
            return;
        }

        playerManager.release();
        playerManager = null;
    }

    // Activity input.

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        return super.dispatchKeyEvent(event) || playerManager.dispatchKeyEvent(event);
    }


    // PlayerManager.Listener implementation.


    // Internal methods.

    private void showToast(int messageId) {
        Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_LONG).show();
    }


    // Internal classes.


    private void getCategories() {
        //   categoryList.clear();
        //  requestInProgress = true;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String url = ApiConfig.GET_VIDEOS_OF_CATEGORY;

                    new ApiConnection().connect(new OnApiResponseListener() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            try {


                                Log.e("RESPONSE", jsonObject.toString());

                                if (jsonObject.has("data")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsn = jsonArray.getJSONObject(i);

                                        ArrayList<Video> videoArrayList = new ArrayList<>();
                                        videoArrayList.clear();

                                        JSONArray jsonArrayVideo = jsn.getJSONArray("videos_list");
                                        for (int j = 0; j < jsonArrayVideo.length(); j++) {

                                            JSONObject jsnVideo = jsonArrayVideo.getJSONObject(j);
                                            if (TextUtils.equals(jsn.getString("cid"), "38")) {
                                                Video video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                        jsnVideo.getString("desc"), jsnVideo.getString("link"), jsnVideo.getString("categoryid"),false,jsn.getString("category_name"),jsnVideo.getString("video"));
                                                videoArrayList.add(video);

                                            } else {

                                                 Video video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                        jsnVideo.getString("desc"), "https://newseeqvideos.s3-us-west-2.amazonaws.com/" + jsnVideo.getString("video"), jsnVideo.getString("categoryid"),false,jsn.getString("category_name"),jsnVideo.getString("video"));
                                                videoArrayList.add(video);
                                            }

                                        }
                                        Log.e(jsn.getString("category_name"), jsn.getString("cid"));
                                        Category category = new Category(jsn.getString("cid"), jsn.getString("category_name"), videoArrayList);
                                        categoryList.add(category);
                                        tab_layout.addTab(tab_layout.newTab(), i);
                                        tab_layout.getTabAt(i).setText(jsn.getString("category_name"));
                                        if (i == 0) {

                                            adapter = new VideoAdapter(MainActivity.this, videoArrayList, MainActivity.this);
                                            recyclerView.setAdapter(adapter);
                                        }


                                    }

                                }

                              /*  adapter.notifyDataSetChanged();
                                // avLoadingIndicatorView.hide();
                                swipeRefreshLayout.setRefreshing(false);
                                requestInProgress = false;*/
                            } catch (JSONException e) {
                                e.printStackTrace();
                               /* swipeRefreshLayout.setRefreshing(false);
                                requestInProgress = false;*/
                            }

                        }

                        @Override
                        public void onFailed(String message) {
                            // avLoadingIndicatorView.hide();
                            Toast.makeText(MainActivity.this, "Oops something went wrong..", Toast.LENGTH_SHORT).show();
                           /* requestInProgress = false;
                            swipeRefreshLayout.setRefreshing(false);*/
                        }
                    }, null, url);

                } catch (Exception e) {
                   /* requestInProgress = false;
                    swipeRefreshLayout.setRefreshing(false);*/
                }
            }
        });
    }
}