package com.example.firon.suwarippa_rhythm_java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button startButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(startButton)){
            Intent intent = new Intent(getApplication(), Start.class);
            startActivity(intent);
        }
    }

//    private void setAnime(){
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        // animation時間 msec
//        scaleAnimation.setDuration(2000);
//
//        RotateAnimation rotate = new RotateAnimation(0.0f, 120.0f,
//                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.5f);
//        // animation時間 msec
//        rotate.setDuration(2000);
//
//        AnimationSet animationSet = new AnimationSet( true );
//
//        // animationSetにそれぞれ追加する
//        animationSet.addAnimation( scaleAnimation );
//        animationSet.addAnimation( rotate );
//
//        imageView.startAnimation(animationSet);
//    }


}