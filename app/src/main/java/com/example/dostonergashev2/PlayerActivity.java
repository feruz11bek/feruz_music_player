package com.example.dostonergashev2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Bundle songExtraData;
    ImageView imageView;
    int position;
    SeekBar seekBar;
    TextView songname;
    static MediaPlayer mMediaPlayer;
    Button orqaga,oldinga,play;
    ArrayList<File> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        imageView=(ImageView)findViewById(R.id.imageView);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        songname=(TextView) findViewById(R.id.songname);
        orqaga=(Button) findViewById(R.id.orqaga);
        play=(Button) findViewById(R.id.play);
        oldinga=(Button) findViewById(R.id.oldinga);



        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
        }


        Intent intent=getIntent();
        songExtraData=intent.getExtras();

        musicList=(ArrayList)songExtraData.getParcelableArrayList("songsList");
        position=songExtraData.getInt("position",0);

        initializeMusicPlayer(position);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        oldinga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<musicList.size()-1){
                    position++;
                }
                else{
                    position=0;
                }
                initializeMusicPlayer(position);
            }
        });

        orqaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<=0){
                    position=musicList.size();
                }
                else{
                    position++;
                }
                initializeMusicPlayer(position);
            }
        });


    }

    private void initializeMusicPlayer(int position){
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.reset();
        }

        String name=musicList.get(position).getName();
        songname.setText(name);

        Uri uri= Uri.parse(musicList.get(position).toString());
        mMediaPlayer=MediaPlayer.create(this,uri);

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mMediaPlayer.getDuration());
                play.setBackgroundResource(R.drawable.pause);
                mMediaPlayer.start();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                play.setBackgroundResource(R.drawable.play);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    seekBar.setProgress(progress);
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer!=null){
                    try{
                        if(mMediaPlayer.isPlaying()){
                            Message message=new Message();
                            message.what=mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler() {
           @Override
        public void handleMessage(Message msg){
               seekBar.setProgress(msg.what);
           }
    };

    private  void play(){

        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            play.setBackgroundResource(R.drawable.play);
        }
        else{
            mMediaPlayer.start();
            play.setBackgroundResource(R.drawable.pause);
        }
    }
}