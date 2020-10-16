package com.project.dailydrizzle.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.dailydrizzle.OnItemClickListenerVideo;
import com.project.dailydrizzle.R;
import com.project.dailydrizzle.models.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.project.dailydrizzle.api.ApiConfig.THUMBNAIL_URL;

/**
 * Created by Neeraj on 06,July,2020
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Video video;
    private Context context;
    private List<Video> videoList;
    private final OnItemClickListenerVideo itemClickListenerVideo;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvDesc;

        private ImageView ivFav, ivThumbnail, ivShare;
        CardView cardView;
        RelativeLayout relativeLayout;

        ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            ivThumbnail = view.findViewById(R.id.ivThumbnail);
            relativeLayout = view.findViewById(R.id.relBackground);
            cardView = view.findViewById(R.id.cardView);
            cardView.setOnClickListener(view1 -> {
                video = videoList.get(getAdapterPosition());
                itemClickListenerVideo.onItemClick(video, getAdapterPosition());

                video.setPlaying(true);
                notifyDataSetChanged();
            });
        }


    }

    public VideoAdapter(Context mContext, List<Video> videoList, OnItemClickListenerVideo itemClickListenerVideo) {
        this.context = mContext;
        this.videoList = videoList;
        this.itemClickListenerVideo = itemClickListenerVideo;

    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);


        return new VideoAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapter.ViewHolder holder, final int position) {

        video = videoList.get(position);

        holder.tvTitle.setText(video.getTitle());
        holder.tvDesc.setText(video.getDesc());

        String url = THUMBNAIL_URL   + ((video.getThumbnail()).replace("mp4", "jpg")).replace("Playlist","");
        Log.e("thumbnail url", url);
        Picasso.get().load(url).placeholder(R.drawable.thumbs_bg).into(holder.ivThumbnail);
        if (video.isPlaying()) {
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.colorItem));

        } else {
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
        }


    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }


}
