package com.project.dailydrizzle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.cast.DefaultMediaItemConverter;
import com.google.android.exoplayer2.ext.cast.MediaItem;
import com.google.android.exoplayer2.ext.cast.MediaItemConverter;
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.project.dailydrizzle.adapters.TabAdapter;
import com.project.dailydrizzle.adapters.VideoAdapter;
import com.project.dailydrizzle.api.ApiConfig;
import com.project.dailydrizzle.api.ApiConnection;
import com.project.dailydrizzle.api.OnApiResponseListener;
import com.project.dailydrizzle.models.Category;
import com.project.dailydrizzle.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static com.project.dailydrizzle.api.ApiConfig.AWS_URL;
import static com.project.dailydrizzle.api.ApiConfig.VIDEO_URL;
import static com.project.dailydrizzle.models.DemoUtil.MIME_TYPE_VIDEO_MP4;

/**
 * Created by Neeraj on 06,July,2020
 */
public class HomeActivity extends AppCompatActivity implements OnItemClickListenerVideo, SessionAvailabilityListener, Player.EventListener {


    Dialog dialog;
    private static final String USER_AGENT = "DailyDrizzle";
    private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY =
            new DefaultHttpDataSourceFactory(USER_AGENT);

    private TabLayout tab_layout;
    private ArrayList<Category> categoryList;
    PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private boolean playWhenReady = true;
    private long playbackPosition = 0;
    private long currentWindow = 0;
    ArrayList<Video> videoList, tempVideoList;
    ConcatenatingMediaSource concatenatingMediaSource;
    DataSource.Factory dataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private CastPlayer castPlayer;
    private CastContext castContext;
    private PlayerControlView castControlView;
    public static ArrayList<MediaItem> mediaQueue;

    private MediaItemConverter mediaItemConverter;
    private Player currentPlayer;
    int categoryId;
    // Activity lifecycle methods.
    Category selectedCategory;
    boolean isCategoryChanged = false;
    boolean isError = false;
    int currentTab = 0;
    private ViewPager view_pager;
    public static TabAdapter viewPagerAdapter;

    private static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    private static final String TAG = "HomeActivity";
    SharedPreferences sharedpreferences;
    Drawable drawableAccount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_home);
        view_pager = findViewById(R.id.view_pager);
        int orange = getColor(R.color.colorPrimaryDark);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(orange), Color.green(orange), Color.blue(orange))));

        getSupportActionBar().setTitle(Html.fromHtml("<font color=" + htmlColor + ">" + getString(R.string.app_name) + "</font>"));
        playerView = findViewById(R.id.video_view);
        categoryList = new ArrayList<>();
        tab_layout = findViewById(R.id.tab_layout);

        dataSourceFactory =
                new DefaultDataSourceFactory(this, getString(R.string.app_name));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        trackSelector = new DefaultTrackSelector(this);
        exoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        exoPlayer.addListener(this);
        playerView.setPlayer(exoPlayer);

        castControlView = findViewById(R.id.cast_control_view);
        castPlayer = new CastPlayer(castContext);
        castPlayer.addListener(HomeActivity.this);
        castPlayer.setSessionAvailabilityListener(HomeActivity.this);
        castControlView.setPlayer(castPlayer);

        mediaQueue = new ArrayList<>();

        mediaItemConverter = new DefaultMediaItemConverter();

        setCurrentPlayer(castPlayer.isCastSessionAvailable() ? castPlayer : exoPlayer);

        getCategories();
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                view_pager.setCurrentItem(tab.getPosition());

                if (videoList != null) {
                    for (int i = 0; i < videoList.size(); i++) {
                        Video vid = videoList.get(i);
                        vid.setPlaying(false);
                    }

                }

                isCategoryChanged = true;
                Log.e("CategoryPOS", "" + tab.getPosition());
                selectedCategory = categoryList.get(tab.getPosition());
                categoryId = Integer.parseInt(selectedCategory.getId());
                Log.e("CategoryVideoList", "" + selectedCategory.getVideoArrayList().size());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void getCategories() {
        tab_layout.removeAllTabs();
        categoryList.clear();
        //  requestInProgress = true;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String url = ApiConfig.GET_VIDEOS_OF_CATEGORY;
                    Log.e("Url", url);
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
                                                        jsnVideo.getString("desc"), jsnVideo.getString("link"), jsnVideo.getString("categoryid"), false,jsn.getString("category_name"),jsnVideo.getString("link"));
                                                videoArrayList.add(video);

                                            } else {
                                                Video video;

                                                if (TextUtils.equals(jsnVideo.getString("type"), "playlist")) {
                                                    video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                            jsnVideo.getString("desc"), AWS_URL + jsnVideo.getString("video"), jsnVideo.getString("categoryid"), false,jsn.getString("category_name"),jsnVideo.getString("video"));
                                                } else {
                                                    video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                            jsnVideo.getString("desc"), VIDEO_URL + jsnVideo.getString("video"), jsnVideo.getString("categoryid"), false,jsn.getString("category_name"),jsnVideo.getString("video"));
                                                }

                                                videoArrayList.add(video);
                                            }

                                        }

                                        Category category = new Category(jsn.getString("cid"), jsn.getString("category_name"), videoArrayList);
                                        categoryList.add(category);
                                        tab_layout.addTab(tab_layout.newTab(), i);
                                        tab_layout.getTabAt(i).setText(jsn.getString("category_name"));

                                        if (i == currentTab) {
                                            tab_layout.getTabAt(currentTab).select();

                                            if (!exoPlayer.isPlaying()) {
                                                selectedCategory = categoryList.get(currentTab);
                                                firstVideoPlay();

                                            }

                                        }


                                    }
                                    viewPagerAdapter = new TabAdapter
                                            (getSupportFragmentManager(), tab_layout.getTabCount(), categoryList);
                                    view_pager.setAdapter(viewPagerAdapter);
                                    view_pager.setOffscreenPageLimit(1);
                                    view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

                                }
                                /*  adapter.notifyDataSetChanged();
                                // avLoadingIndicatorView.hide();

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
                            Toast.makeText(HomeActivity.this, "Oops something went wrong..", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(Video video, int pos) {

        if (currentPlayer == castPlayer) {
            if (mediaQueue != null && mediaQueue.size() > 0) {

                for (int r = 0; r < mediaQueue.size(); r++) {
                    castPlayer.removeItem(r);
                }
                mediaQueue.clear();

            }
            videoList = selectedCategory.getVideoArrayList();
            for (int i = 0; i < videoList.size(); i++) {
                Video vid = videoList.get(i);
                if (i == pos) {
                    playbackPosition = i;
                    vid.setPlaying(true);

                } else {
                    vid.setPlaying(false);
                }

                Uri uri = Uri.parse(vid.getVideoUrl());
                mediaQueue.add(new MediaItem.Builder()
                        .setUri(uri)
                        .setTitle(vid.getTitle())
                        .setMimeType(MIME_TYPE_VIDEO_MP4)
                        .build());
                castPlayer.addItems(mediaItemConverter.toMediaQueueItem(new MediaItem.Builder()
                        .setUri(uri)
                        .setTitle(vid.getTitle())
                        .setMimeType(MIME_TYPE_VIDEO_MP4)
                        .build()));

            }

            MediaQueueItem[] items = new MediaQueueItem[mediaQueue.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = mediaItemConverter.toMediaQueueItem(mediaQueue.get(i));
            }

            castPlayer.loadItems(items, (int) playbackPosition, C.TIME_UNSET, Player.REPEAT_MODE_OFF);

        } else if (currentPlayer == exoPlayer) {

            if (isCategoryChanged) {
                if (videoList != null && videoList.size() > 0) {
                    //  videoList.clear();
                    concatenatingMediaSource.clear();
                }
                videoList = selectedCategory.getVideoArrayList();
                for (int i = 0; i < videoList.size(); i++) {
                    Video vid = videoList.get(i);
                    if (i == pos) {
                        playbackPosition = i;
                        vid.setPlaying(true);
                    } else {
                        vid.setPlaying(false);
                    }
                    Uri uri = Uri.parse(vid.getVideoUrl());
                    MediaSource mediaSource = buildMediaSource(dataSourceFactory, uri, null);
                    concatenatingMediaSource.addMediaSource(mediaSource);
                }

                exoPlayer.prepare(concatenatingMediaSource);
                exoPlayer.seekTo((int) playbackPosition, C.TIME_UNSET);
                exoPlayer.setPlayWhenReady(true);
                isCategoryChanged = false;
            } else {
                for (int i = 0; i < videoList.size(); i++) {
                    Video vid = videoList.get(i);
                    if (i == pos) {
                        playbackPosition = i;
                        vid.setPlaying(true);
                    } else {
                        vid.setPlaying(false);
                    }
                }

                if (isError) {
                    exoPlayer.prepare(concatenatingMediaSource);
                    isError = false;
                }
                exoPlayer.seekTo((int) playbackPosition, C.TIME_UNSET);
                exoPlayer.setPlayWhenReady(true);
            }


        }


    }


    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
      drawableAccount = menu.getItem(1).getIcon();

        drawableAccount.mutate();
        String userId = sharedpreferences.getString("userid", "");
        if (TextUtils.isEmpty(userId)) {
            drawableAccount.setColorFilter(getColor(R.color.colorIcon), PorterDuff.Mode.SRC_IN);
        } else {
            drawableAccount.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }

        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                dialog = new Dialog(HomeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_login);
                TextView tvName = dialog.findViewById(R.id.tvName);
                TextView tvEmail = dialog.findViewById(R.id.tvEmail);
                AppCompatButton btnLogout = dialog.findViewById(R.id.btnLogout);
                String userId = sharedpreferences.getString("userid", "");
                if (TextUtils.isEmpty(userId)) {
                    dialog.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                    tvName.setVisibility(View.GONE);
                    tvEmail.setVisibility(View.GONE);
                    btnLogout.setVisibility(View.GONE);
                } else {
                    dialog.findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                    tvName.setVisibility(View.VISIBLE);
                    tvEmail.setVisibility(View.VISIBLE);
                    btnLogout.setVisibility(View.VISIBLE);
                    String name = sharedpreferences.getString("name", "");
                    String email = sharedpreferences.getString("email", "");
                    tvName.setText(String.format("Name : %s", name));
                    tvEmail.setText(String.format("Email : %s", email));
                }

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.apply();
                        drawableAccount.setColorFilter(getColor(R.color.colorIcon), PorterDuff.Mode.SRC_IN);


                    }
                });

                dialog.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signIn();
                    }
                });

                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = Objects.requireNonNull(window).getAttributes();
                wlp.gravity = Gravity.TOP | Gravity.END;
                wlp.y = 90;
                dialog.show();
                window.setAttributes(wlp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentWindow > 0) {
            if (currentPlayer == exoPlayer) {
                exoPlayer.prepare(concatenatingMediaSource);
                exoPlayer.seekTo((int) playbackPosition, C.TIME_UNSET);
                exoPlayer.setPlayWhenReady(true);
            }
        }
       /* if (currentWindow > 0) {
            Log.e("here", "onResume");
            initializePlayer();
            player.prepare(concatenatingMediaSource);
            player.seekTo((int) playbackPosition, C.TIME_UNSET);
            player.setPlayWhenReady(true);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

   /* @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();

            Log.e("Play", "" + playbackPosition);
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }*/

    public static MediaSource buildMediaSource(DataSource.Factory dataSourceFactory, Uri uri, String overrideExtension) {
        int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    @Override
    public void onCastSessionAvailable() {
        setCurrentPlayer(castPlayer);


    }

    @Override
    public void onCastSessionUnavailable() {
        setCurrentPlayer(exoPlayer);


    }

    private void setCurrentPlayer(Player cPlayer) {

        currentPlayer = cPlayer;
        // View management.
        if (currentPlayer == exoPlayer) {
            playerView.setVisibility(View.VISIBLE);
            castControlView.hide();
        } else /* currentPlayer == castPlayer */ {
            playerView.setVisibility(View.GONE);
            castControlView.show();
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        if (!isCategoryChanged) {
            int sourceIndex = exoPlayer.getCurrentWindowIndex();
            for (int i = 0; i < videoList.size(); i++) {
                Video vid = videoList.get(i);
                if (i == sourceIndex) {
                    vid.setPlaying(true);
                } else {
                    vid.setPlaying(false);
                }
            }

        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        isError = true;
        Log.e("Error", error.getMessage().toString());
    }

    public void firstVideoPlay() {
        videoList = selectedCategory.getVideoArrayList();
        for (int i = 0; i < videoList.size(); i++) {
            Video vid = videoList.get(i);
            if (i == 0) {
                playbackPosition = i;
                vid.setPlaying(true);
            } else {
                vid.setPlaying(false);
            }
            Uri uri = Uri.parse(vid.getVideoUrl());
            MediaSource mediaSource = buildMediaSource(dataSourceFactory, uri, null);
            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        exoPlayer.prepare(concatenatingMediaSource);
        exoPlayer.seekTo((int) playbackPosition, C.TIME_UNSET);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            drawableAccount.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("name", user.getDisplayName());
                            editor.putString("email", user.getEmail());
                            editor.putString("userid", user.getUid());
                            editor.apply();
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}