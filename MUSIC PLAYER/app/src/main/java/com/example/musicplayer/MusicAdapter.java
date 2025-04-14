// MusicAdapter.java
package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private List<MusicList> list;
    private final Context context;
    private int playingPosition = 0;
    private final SongChangeListener songChangeListener;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener) context);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, final int position) {
        MusicList musicList = list.get(position);
        holder.title.setText(musicList.getTitle());

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(playingPosition).setPlaying(false);
                list.get(position).setPlaying(true);
                songChangeListener.onChanged(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<MusicList> musicLists) {
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rootLayout;
        private final TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            title = itemView.findViewById(R.id.musicTitle);
        }
    }
}
