package com.project.dailydrizzle.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.project.dailydrizzle.R;
import com.project.dailydrizzle.models.Video;

import java.util.List;

public class TabVideoAdapter extends RecyclerView.Adapter<TabVideoAdapter.ViewHolder> {

    private Video video;
    private Context context;
    private List<Video> videoList;
    private ViewPager view_pager;
    public static TabAdapter viewPagerAdapter;



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvDesc;

        private ImageView ivFav, ivThumbnail, ivShare;
        CardView cardView;
        RelativeLayout relativeLayout;

        ViewHolder(View view) {
            super(view);

            //  tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            // ivThumbnail = view.findViewById(R.id.ivThumbnail);
            //  relativeLayout = view.findViewById(R.id.relBackground);
            //  cardView = view.findViewById(R.id.cardView);

        }


    }

    public TabVideoAdapter(Context mContext, List<Video> videoList) {
        this.context = mContext;
        this.videoList = videoList;


    }

    @NonNull
    @Override
    public TabVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab_video, parent, false);


        return new TabVideoAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final TabVideoAdapter.ViewHolder holder, final int position) {

        video = videoList.get(position);

        //  holder.tvTitle.setText(video.getTitle());
        holder.tvDesc.setText(video.getDesc());

       /* String url = THUMBNAIL_URL   + ((video.getThumbnail()).replace("mp4", "jpg")).replace("Playlist","");
        Log.e("thumbnail url", url);
        Picasso.get().load(url).placeholder(R.drawable.thumbs_bg).into(holder.ivThumbnail);
        if (video.isPlaying()) {
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.colorItem));

        } else {
            holder.relativeLayout.setBackgroundColor(Color.WHITE);
        }*/


    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }


}