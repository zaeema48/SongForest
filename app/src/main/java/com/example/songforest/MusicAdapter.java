package com.example.songforest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{

    //first thing after creating a recycler view
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView album_art, more;
        TextView file_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.songName);
            album_art=itemView.findViewById(R.id.music_img);
            more=itemView.findViewById(R.id.more);
        }
    }

    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    //making constructor to store data into local storage
    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_items,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());

        byte[] image=getAlbumArt(mFiles.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }
        else {
            Glide.with(mContext)
                    .load(R.drawable.make_cover)
                    .into(holder.album_art);
        }

        //on clicking will start playing the song in other activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext,PlayerActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu= new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete: {
                                deleteFile(position, view);
                                break;
                            }
                            case R.id.fav:
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void deleteFile(int position, View view){
//        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(mFiles.get(position)
//                .get))
        mFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mFiles.size());
        Snackbar.make(view,"File Deleted",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    //for search query
    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles=new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
