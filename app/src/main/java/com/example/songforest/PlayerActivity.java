package com.example.songforest;

import static com.example.songforest.HomePage.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private Handler handler=new Handler(); //gives us the method to delay any action

    private Thread playThread,prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();

        song_name.setText(ListSongs.get(position).getTitle());
        artist_name.setText(ListSongs.get(position).getArtist());

        //for dragging the seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
               if(mediaPlayer!=null && b){
                   mediaPlayer.seekTo(progress*1000);
               }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    //for automatic change in the position of the seekBar according to time
                    int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000); //1000->1s
            }
        });

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
//

    public void getIntentMethod(){
        position = getIntent().getIntExtra("position", -1);

        ListSongs = musicFiles;
        if(ListSongs!=null){
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_pause);
            uri = Uri.parse(ListSongs.get(position).getPath());
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else{
            mediaPlayer=MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);

        metaData(uri);

    }
//
    //for fetching the album photo
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal= Integer.parseInt(ListSongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));

        byte [] art= retriever.getEmbeddedPicture();
        if(art!=null){
            Glide.with(this).asBitmap()
                    .load(art).into(cover_art);
        }
        else{
            Glide.with(this).asBitmap()
                    .load(R.drawable.make_cover)
                    .into(cover_art);
        }
    }

    public String formattedTime(int mCurrentPosition){
        String totalOut= "";
        String totalNew= "";
        String seconds= String.valueOf(mCurrentPosition%60);
        String minutes= String.valueOf(mCurrentPosition/60);
        totalOut= minutes+":"+seconds;
        totalNew=minutes+":0"+seconds;
        if(seconds.length()==1){
            return totalNew;
        }
        else{
            return totalOut;
        }
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    private void playThreadBtn() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                PlayPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayPauseButtonClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void PlayPauseButtonClicked(){
        if(mediaPlayer.isPlaying()){
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        //for automatic change in the position of the seekBar according to time
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000); //1000->1s
                }
            });
        }
        else{
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        //for automatic change in the position of the seekBar according to time
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000); //1000->1s
                }
            });
        }
    }

    private void prevThreadBtn() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                prev_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevButtonClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevButtonClicked(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1) <0 ?( ListSongs.size()-1) :(position-1) );
            uri=Uri.parse(ListSongs.get(position).getPath());

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(ListSongs.get(position).getTitle());
            artist_name.setText(ListSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        //for automatic change in the position of the seekBar according to time
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000); //1000->1s
                }
            });
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1) <0 ?( ListSongs.size()-1) :(position-1) );
            uri=Uri.parse(ListSongs.get(position).getPath());

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(ListSongs.get(position).getTitle());
            artist_name.setText(ListSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        //for automatic change in the position of the seekBar according to time
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000); //1000->1s
                }
            });
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_play);

        }

    }

    private void nextThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                next_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextButtonClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextButtonClicked(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position+1)%ListSongs.size());
            uri=Uri.parse(ListSongs.get(position).getPath());

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(ListSongs.get(position).getTitle());
            artist_name.setText(ListSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        //for automatic change in the position of the seekBar according to time
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000); //1000->1s
                }
            });
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position+1)%ListSongs.size());
            uri=Uri.parse(ListSongs.get(position).getPath());

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(ListSongs.get(position).getTitle());
            artist_name.setText(ListSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        //for automatic change in the position of the seekBar according to time
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000); //1000->1s
                }
            });
            PlayPauseButton.setImageResource(R.drawable.ic_baseline_play);

        }
    }
}