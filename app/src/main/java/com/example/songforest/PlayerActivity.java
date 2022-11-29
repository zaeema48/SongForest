package com.example.songforest;

import static com.example.songforest.HomePage.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, next_Button, prev_Button, back_Button, shuffle_Button, repeat_Button;
    FloatingActionButton PlayPauseButton;
    SeekBar seekBar;

    int position=-1;
    static ArrayList<MusicFiles> ListSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
    }
    public void initViews(){
        song_name= findViewById(R.id.songName);
        artist_name= findViewById(R.id.artistName);
        duration_played=findViewById(R.id.durationPlayed);
        duration_total=findViewById(R.id.durationTotal);
        cover_art=findViewById(R.id.cover_art);
        next_Button=findViewById(R.id.skip_next);
        prev_Button=findViewById(R.id.skip_prvs);
        back_Button=findViewById(R.id.back_btn);
        shuffle_Button=findViewById(R.id.shuffle);
        repeat_Button=findViewById(R.id.repeat);
        PlayPauseButton=findViewById(R.id.play_pause);
        seekBar=findViewById(R.id.seekBar);

    }

    public void getIntentMethod(){
        position = getIntent().getIntExtra("position", -1);

        ListSongs = musicFiles;
        if(ListSongs!=null){
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_pause);
            uri = Uri.parse(ListSongs.get(position).getPath());

        }
    }
}