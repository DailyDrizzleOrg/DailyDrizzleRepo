package com.project.dailydrizzle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.project.dailydrizzle.adapters.TabAdapter;
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

/**
 * Created by Neeraj on 07,October,2020
 */
public class TabActivity extends AppCompatActivity {

    private TabLayout tab_layout;
    private ArrayList<Category> categoryList;
    ArrayList<Video> videoList;
    private ViewPager view_pager;
    public static TabAdapter viewPagerAdapter;

    private static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    private static final String TAG = "TabActivity";
    SharedPreferences sharedpreferences;
    Drawable drawableAccount;
    Dialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        int orange = getColor(R.color.colorPrimaryDark);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(orange), Color.green(orange), Color.blue(orange))));

        getSupportActionBar().setTitle(Html.fromHtml("<font color=" + htmlColor + ">" + getString(R.string.app_name) + "</font>"));


        tab_layout = findViewById(R.id.tab_layout);

        view_pager = findViewById(R.id.view_pager);
        categoryList = new ArrayList<>();
        tab_layout = findViewById(R.id.tab_layout);
        getCategories();

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                view_pager.setCurrentItem(tab.getPosition());



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
                dialog = new Dialog(TabActivity.this);
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
                                                        jsnVideo.getString("desc"), jsnVideo.getString("link"), jsnVideo.getString("categoryid"), false, jsn.getString("category_name"), jsnVideo.getString("link"));
                                                videoArrayList.add(video);

                                            } else {
                                                Video video;

                                                if (TextUtils.equals(jsnVideo.getString("type"), "playlist")) {
                                                    video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                            jsnVideo.getString("desc"), AWS_URL + jsnVideo.getString("video"), jsnVideo.getString("categoryid"), false, jsn.getString("category_name"), jsnVideo.getString("video"));
                                                } else {
                                                    video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                            jsnVideo.getString("desc"), VIDEO_URL + jsnVideo.getString("video"), jsnVideo.getString("categoryid"), false, jsn.getString("category_name"), jsnVideo.getString("video"));
                                                }

                                                videoArrayList.add(video);
                                            }

                                        }

                                        Category category = new Category(jsn.getString("cid"), jsn.getString("category_name"), videoArrayList);
                                        categoryList.add(category);
                                        tab_layout.addTab(tab_layout.newTab(), i);
                                        tab_layout.getTabAt(i).setText(jsn.getString("category_name"));


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
                            Toast.makeText(TabActivity.this, "Oops something went wrong..", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TabActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(TabActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(TabActivity.this, "Authentication failed.",
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
