package com.bytedance.androidcamp.network.dou;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private ImageButton btnLike;
    public static boolean isInnerHeart = false;
    private Animation loadHeartAnimation;

    public static void launch(Activity activity, String url) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        String url = getIntent().getStringExtra("url");
        VideoView videoView = findViewById(R.id.video_container);
        btnLike = findViewById(R.id.btn_like);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInnerHeart){
                    isInnerHeart=false;
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    loadHeartAnimation = AnimationUtils.loadAnimation(v.getContext(),R.anim.heart_animation);
                    btnLike.startAnimation(loadHeartAnimation);
                }else{
                    isInnerHeart=true;
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
                    loadHeartAnimation = AnimationUtils.loadAnimation(v.getContext(),R.anim.heart_animation);
                    btnLike.startAnimation(loadHeartAnimation);
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);
    }
}
