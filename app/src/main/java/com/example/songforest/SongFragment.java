package com.example.songforest;

import static com.example.songforest.HomePage.musicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SongFragment extends Fragment {
    RecyclerView recyclerView;
    static MusicAdapter musicAdapter;

    public SongFragment(){
        //Required in empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_song, container, false);

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        if(!(musicFiles.size()<1)){
            musicAdapter=new MusicAdapter(getContext(),musicFiles);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext() ,RecyclerView.VERTICAL,false));
        }
        return view;
    }
}