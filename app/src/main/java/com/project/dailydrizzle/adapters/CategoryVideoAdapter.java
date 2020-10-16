package com.project.dailydrizzle.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.dailydrizzle.R;
import com.project.dailydrizzle.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryVideoAdapter extends RecyclerView.Adapter<CategoryVideoAdapter.SingleItemRowHolder> {

    private ArrayList<Video> itemModels;
    private Context mContext;


    CategoryVideoAdapter(ArrayList<Video> itemModels, Context mContext) {
        this.itemModels = itemModels;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_video, null);
        SingleItemRowHolder singleItemRowHolder = new SingleItemRowHolder(v);
        return singleItemRowHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemRowHolder holder, int position) {
        final Video itemModel = itemModels.get(position);
        holder.tvTitle.setText(itemModel.getDesc());

        String url = "http://newseeq.com/video_app/images/thumbs/" + (itemModel.getVideoUrl()).replace("mp4", "JPG");

        Log.e("url", url);

        Picasso.get().load(url).placeholder(R.drawable.thumbs_bg).into(holder.ivThumbnail);


      /*  holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded() && Const.AdsVisibility == 1) {
                    mInterstitialAd.show();
                } else {
                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
                    intent.putExtra("videoid", itemModel.getyCode());
                    intent.putExtra("title", itemModel.getName());
                    intent.putExtra("categoryid",itemModel.getCategoryId());
                    mContext.startActivity(intent);
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Intent intent = new Intent(mContext, VideoPlayActivity.class);
                        intent.putExtra("videoid", itemModel.getyCode());
                        intent.putExtra("title", itemModel.getName());
                        intent.putExtra("categoryid",itemModel.getCategoryId());
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when the ad is displayed.
                    }

                    @Override
                    public void onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when the interstitial ad is closed.
                        Intent intent = new Intent(mContext, VideoPlayActivity.class);
                        intent.putExtra("videoid", itemModel.getyCode());
                        intent.putExtra("title", itemModel.getName());
                        intent.putExtra("categoryid",itemModel.getCategoryId());
                        mContext.startActivity(intent);
                    }
                });

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return (null != itemModels ? itemModels.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        public ImageView ivThumbnail;

        SingleItemRowHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.ivThumbnail = itemView.findViewById(R.id.ivThumbnail);


        }
    }

}