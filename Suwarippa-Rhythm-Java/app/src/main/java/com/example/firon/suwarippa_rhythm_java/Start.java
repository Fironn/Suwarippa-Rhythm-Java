package com.example.firon.suwarippa_rhythm_java;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;


public class Start extends Activity implements View.OnClickListener,MediaPlayer.OnCompletionListener {

    private Button startButton;
    private FrameLayout layout;
    private PaintCanvas arc;
    private int endAngle = 0;
    private int animationPeriod = 2000;
    private CreateAnimation animation;
    private boolean showCanvas;

    private int count = 1;
    private MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        final Context context = this;
        BufferedReader in = null;

        layout = (FrameLayout) findViewById(R.id.frameLayout);
        startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(this);
        arc = this.findViewById(R.id.arc);

        arc.showCanvas(true);
        showCanvas = true;

    }

    @Override
    public void onClick(View v) {
        if (v.equals(startButton)) {

            start();

            if (mediaPlayer!= null)stop();
            audioPlay();

//            if (showCanvas) {
//                arc.showCanvas(false);
//                showCanvas = false;
//            } else {
//                arc.showCanvas(true);
//                showCanvas = true;
//            }

            startButton.setVisibility(View.INVISIBLE);

        }
    }

    private void setAnime(int num,int len) throws InterruptedException {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0);

        int winWidth=layout.getWidth();
        int winHeight=layout.getHeight();
        int caWidth = arc.getWidth();
        int caHeight = arc.getHeight();
        int imageWidth = caWidth;
        int imageHeight = caHeight;
        float sumH=(caHeight/2-50)*1f/winHeight;
        float sumW=(caWidth/2-50)*1f/winWidth;


        if (num == 1) {

            imageHeight = 50;
            imageWidth = caWidth;

            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, -0.5f,
                    Animation.RELATIVE_TO_PARENT, -sumH
            );

        } else if (num == 2) {

            imageHeight = 50;
            imageWidth = caWidth;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0.5f,
                    Animation.RELATIVE_TO_PARENT, sumH
            );

        } else if (num == 3) {

            imageHeight = caHeight;
            imageWidth = 50;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -0.5f,
                    Animation.RELATIVE_TO_PARENT, -sumW,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f
            );

        } else if (num == 4) {

            imageHeight = caHeight;
            imageWidth = 50;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.5f,
                    Animation.RELATIVE_TO_PARENT, sumW,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f
            );

        } else if( num ==0){

            imageHeight = 0;
            imageWidth = 0;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f
            );
        }

        translateAnimation.setDuration(len);
        // 繰り返し回数
        translateAnimation.setRepeatCount(0);
        // animationが終わったそのまま表示にする
        translateAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.addAnimation(translateAnimation);


        // ImageViewのインスタンス生成
        ImageView imageView = new ImageView(this);

        // drawableの画像を指定
        if (count % 3 == 0) {
            imageView.setImageResource(R.color.colorPrimaryDark);
        } else if (count % 3 == 1) {
            imageView.setImageResource(R.color.colorAccent);
        } else {
            imageView.setImageResource(R.color.colorPrimary);
        }

        // 画像の縦横サイズをimageViewのサイズとして設定
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(imageWidth, imageHeight);

        layoutParams.gravity = Gravity.CENTER;
//        layoutParams.leftMargin = 10;
//        layoutParams.rightMargin = 10;

        imageView.setLayoutParams(layoutParams);

// layoutにimageViewを追加
        layout.addView(imageView);
        count++;

        imageView.startAnimation(animationSet);
    }

    private void readFile() {
    }

    private void start(){

        final int noteLen,len;
        int note[],tmp[],re[];
        note= new int[0];
        tmp= new int[0];
        re= new int[2];

        int i=0;

        len=2000;

        try {

            String data = getString(R.string.data1);
            JSONObject json = new JSONObject(data);

            JSONArray jarray = new JSONArray(json.getString("note"));
            noteLen=jarray.length();

            note= new int[noteLen+1];

            for (i=0; i<noteLen; i++){
                note[i]= Integer.parseInt(jarray.getString(i));
            }

            jarray = new JSONArray(json.getString("tmp"));

            tmp= new int[jarray.length()+1];

            for (i=0; i<jarray.length(); i++){
                tmp[i]= Integer.parseInt(jarray.getString(i));
            }

            final Handler handler = new Handler();

            final int[] finalNote = note;

            final int[] count = new int[1];
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    // UIスレッド
                    count[0]++;
                    if (count[0] > noteLen) { // 5回実行したら終了
                        return;
                    }

                    try {
                        setAnime(finalNote[count[0]],len);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.postDelayed(this, len/3);
                }
            };

            handler.post(r);

            setAnime(1,len);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean audioSetup(){
        boolean fileCheck = false;

        mediaPlayer = new MediaPlayer();

        String filePath = "Nolove.mp3";

        try{

            AssetFileDescriptor afdescripter = getAssets().openFd(filePath);

            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();

            fileCheck = true;

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return fileCheck;
    }

    private void audioPlay() {

        if (mediaPlayer == null) {
            if (audioSetup()){
                Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }

        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
                startButton.setVisibility(View.VISIBLE);
            }
        });

    }


    private void stop() {
        if (mediaPlayer==null) {
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}