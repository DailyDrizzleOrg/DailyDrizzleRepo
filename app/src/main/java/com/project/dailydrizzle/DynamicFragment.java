package com.project.dailydrizzle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import static com.project.dailydrizzle.api.ApiConfig.AWS_URL;
import static com.project.dailydrizzle.api.ApiConfig.VIDEO_URL;

/**
 * Created by Neeraj on 27,September,2020
 */
public class DynamicFragment extends Fragment {
    View view;

    public static DynamicFragment newInstance(int val, ArrayList<Category> categoryList) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        args.putParcelableArrayList("categoryList", categoryList);
        fragment.setArguments(args);
        return fragment;
    }

    int val;
    TextView c;
    VideoAdapter adapter;
    RecyclerView recyclerView;
    boolean isLoading = false;
    private String categoryId;
    private ArrayList<Video> videoArrayList;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);
        val = getArguments().getInt("someInt", 0);
        ArrayList<Category> categoryList= getArguments().getParcelableArrayList("categoryList");

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        Category category = categoryList.get(val);
        videoArrayList = category.getVideoArrayList();
        adapter = new VideoAdapter(getContext(), videoArrayList, (OnItemClickListenerVideo) getActivity());
        recyclerView.setAdapter(adapter);

        Log.e("TAG", "onCreateView: "+ videoArrayList.size());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                loadMore();
            }
        });

        return view;
    }

    private void loadMore()
    {
//        rowsArrayList.add(null);
//        recyclerViewAdapter.notifyItemInserted(rowsArrayList.size() - 1);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                recyclerViewAdapter.notifyDataSetChanged();
//                isLoading = false;
//            }
//        }, 2000);

//        Log.e("TAG", "loadMore: "+videoArrayList.get(0).getCategoryId());
        if(videoArrayList != null && videoArrayList.size() != 0)
        {
            categoryId = videoArrayList.get(0).getCategoryId();
        }
        swipeRefreshLayout.setRefreshing(true);

        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String url = ApiConfig.GET_VIDEOS_OF_CATEGORY;
                    Log.e("Url", url);
                    new ApiConnection().connect(new OnApiResponseListener()
                    {
                        @Override
                        public void onSuccess(JSONObject jsonObject)
                        {
                            try
                            {
                                Log.e("TAG", jsonObject.toString());
                                videoArrayList.clear();
                                if (jsonObject.has("data"))
                                {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++)
                                    {
                                        JSONObject jsn = jsonArray.getJSONObject(i);
                                        if(TextUtils.equals(jsn.getString("cid"),categoryId))
                                        {
                                            Log.e("TAG", "Yes fpound");
                                            JSONArray jsonArrayVideo = jsn.getJSONArray("videos_list");
                                            for (int j = 0; j < jsonArrayVideo.length(); j++)
                                            {
                                                JSONObject jsnVideo = jsonArrayVideo.getJSONObject(j);
                                                Video video;
                                                if (TextUtils.equals(jsnVideo.getString("type"), "playlist"))
                                                {
                                                    video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                            jsnVideo.getString("desc"), AWS_URL + jsnVideo.getString("video"), jsnVideo.getString("categoryid"), false,jsn.getString("category_name"),jsnVideo.getString("video"));
                                                } else
                                                {
                                                    video = new Video(jsnVideo.getString("video_id"), jsnVideo.getString("title"),
                                                            jsnVideo.getString("desc"), VIDEO_URL + jsnVideo.getString("video"), jsnVideo.getString("categoryid"), false,jsn.getString("category_name"),jsnVideo.getString("video"));
                                                }
                                                videoArrayList.add(video);
                                            }

                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);

//                                progressBar.setVisibility(View.GONE);
//                                isLoading = false;

                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailed(String message)
                        {
                            Toast.makeText(getActivity(), "Oops something went wrong..", Toast.LENGTH_SHORT).show();
                        }
                    }, null, url);

                } catch (Exception e) {
                }
            }
        });
    }

}