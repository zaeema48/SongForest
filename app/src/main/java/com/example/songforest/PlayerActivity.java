package com.example.songforest;

import static com.example.songforest.AlbumDetailsAdapter.albumFiles;
import static com.example.songforest.HomePage.musicFiles;
import static com.example.songforest.HomePage.repeatedBoolean;
import static com.example.songforest.HomePage.shuffleBoolean;
import static com.example.songforest.MusicAdapter.mFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
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
        //to change the song after it's finished
        mediaPlayer.setOnCompletionListener(this);

        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(PlayerActivity.this,HomePage.class);
                startActivity(intent);
            }
        });

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
        shuffle_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean){
                    shuffleBoolean=false;
                    shuffle_Button.setImageResource(R.drawable.ic_shuffle_off);
                }
                else{
                    shuffleBoolean=true;
                    shuffle_Button.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        repeat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatedBoolean){
                    repeatedBoolean=false;
                    repeat_Button.setImageResource(R.drawable.ic_baseline_repeat_off);
                }
                else{
                    repeatedBoolean=true;
                    repeat_Button.setImageResource(R.drawable.ic_baseline_repeat_on);
                }
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
        String sender = getIntent().getStringExtra("sender");
        if(sender!= null && sender.equals("albumDetails")){
            ListSongs=albumFiles;
        }
        else {
            ListSongs = mFiles;
        }
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
            if(shuffleBoolean && repeatedBoolean){
                position=getRandomNumber(ListSongs.size()-1);
            }
            else if(!shuffleBoolean && repeatedBoolean){
                position=((position-1) <0 ?( ListSongs.size()-1) :(position-1) );
            }

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
            mediaPlayer.setOnCompletionListener(this);
            PlayPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && repeatedBoolean){
                position=getRandomNumber(ListSongs.size()-1);
            }
            else if(!shuffleBoolean && repeatedBoolean){
                position=((position-1) <0 ?( ListSongs.size()-1) :(position-1) );
            }
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
            mediaPlayer.setOnCompletionListener(this);
            PlayPauseButton.setBackgroundResource(R.drawable.ic_baseline_play);

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
            mediaPlayer.stop();   //to stop the current music
            mediaPlayer.release(); //to release the current music
            if(shuffleBoolean && repeatedBoolean){
                position=getRandomNumber(ListSongs.size()-1);
            }
            else if(!shuffleBoolean && repeatedBoolean){
                position=((position+1)%ListSongs.size());
            }

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
            mediaPlayer.setOnCompletionListener(this);
            PlayPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && repeatedBoolean){
                position=getRandomNumber(ListSongs.size()-1);
            }
            else if(!shuffleBoolean && repeatedBoolean){
                position=((position+1)%ListSongs.size());
            }


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
            mediaPlayer.setOnCompletionListener(this);
            PlayPauseButton.setBackgroundResource(R.drawable.ic_baseline_play);

        }
    }

    private int getRandomNumber(int i){
       Random random = new Random();
        return random.nextInt(i+1);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextButtonClicked();
        if(mediaPlayer!=null){
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

//    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
//        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
//        Animation animIn= AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
//
//        animOut.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Glide.with(context).load(bitmap).into(imageView);
//                animIn.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
//                imageView.startAnimation(animIn);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        imageView.startAnimation(animOut);
//    }

}