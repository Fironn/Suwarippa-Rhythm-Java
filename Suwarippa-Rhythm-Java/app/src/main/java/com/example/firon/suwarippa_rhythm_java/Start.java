package com.example.firon.suwarippa_rhythm_java;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static com.example.firon.suwarippa_rhythm_java.R.color;
import static com.example.firon.suwarippa_rhythm_java.R.id;
import static com.example.firon.suwarippa_rhythm_java.R.id.frameLayout;
import static com.example.firon.suwarippa_rhythm_java.R.id.inputValue;
import static com.example.firon.suwarippa_rhythm_java.R.id.start;
import static com.example.firon.suwarippa_rhythm_java.R.id.statusValue;
import static com.example.firon.suwarippa_rhythm_java.R.string;


public class Start extends Activity implements Runnable,View.OnClickListener,MediaPlayer.OnCompletionListener {

    private Button closeRank;
    private ProgressBar progressBar;
    private Button startButton;
    private TableLayout rankTable;
    private FrameLayout rankLayout;
    private LinearLayout scoreLayout;
    private FrameLayout layout;
    public TextView score;
    private PaintCanvas arc;
    private boolean showCanvas;
    private String name="";
    private String SCORE_FILE = "score.txt";

    public int scoreInt=0;
    public int press=0;
    private MediaPlayer mediaPlayer;

    public String data="";

    /* tag */
    private static final String TAG = "BluetoothSample";

    /* Bluetooth Adapter */
    private BluetoothAdapter mAdapter;

    /* Bluetoothデバイス */
    private BluetoothDevice mDevice;

    /* Bluetooth UUID */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /* デバイス名 */
    private final String DEVICE_NAME = "RNBT-2F78";

    private BluetoothServerSocket mBtServerSocket;
    /* Soket */
    private BluetoothSocket mSocket;
    /* Thread */
    private Thread mThread;

    /* Threadの状態を表す */
    private boolean isRunning;

    /** 接続ボタン. */
    private Button connectButton;

    /** 書込みボタン. */
    private Button writeButton;

    private Button buttonFadeIn;

    /** ステータス. */
    private TextView mStatusTextView;

    /** Bluetoothから受信した値. */
    private TextView mInputTextView;

    /** Action(ステータス表示). */
    private static final int VIEW_STATUS = 0;

    /** Action(取得文字列). */
    private static final int VIEW_INPUT = 1;

    /** Connect確認用フラグ */
    public boolean connectFlg = false;

    /** BluetoothのOutputStream. */
    OutputStream mmOutputStream = null;

    private ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        final Context context = this;
        BufferedReader in = null;

        writeScore("text"+","+"4"+"\n");

        showRank();

        closeRank=(Button)findViewById(id.closeRank);
        rankTable=(TableLayout) findViewById(id.rankTable);
        rankLayout=(FrameLayout) findViewById(id.rankLayout);
        scoreLayout=(LinearLayout) findViewById(id.scoreLayout);
        layout = (FrameLayout) findViewById(frameLayout);
        startButton = (Button) findViewById(start);
        progressBar=(ProgressBar)findViewById(id.ProgressBar);
        score = (TextView) findViewById(id.score);
        startButton.setOnClickListener(this);
        closeRank.setOnClickListener(this);
        arc = this.findViewById(id.arc);

        arc.showCanvas(true);
        showCanvas = true;

        mInputTextView = (TextView)findViewById(inputValue);
        mStatusTextView = (TextView)findViewById(statusValue);

        connectButton = (Button)findViewById(id.connectButton);
//        writeButton = (Button)findViewById(R.id.writeButton);

        connectButton.setOnClickListener(this);
//        writeButton.setOnClickListener(this);

        // Bluetoothのデバイス名を取得
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatusTextView.setText("SearchDevice");
        Set< BluetoothDevice > devices = mAdapter.getBondedDevices();
        for ( BluetoothDevice device : devices){
            if(device.getName().equals(DEVICE_NAME)){
                mStatusTextView.setText("find: " + device.getName());
                mDevice = device;
            }
        }
        connect();
    }

    public void connect(){
        if (!connectFlg) {
            mStatusTextView.setText("try connect");

            mThread = new Thread( this);
            // Threadを起動し、Bluetooth接続
            isRunning = true;
            mThread.start();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        isRunning = false;
        try{
            mSocket.close();
        }
        catch(Exception e){}
    }

    @Override
    public void run() {
        InputStream mmInStream = null;

        Message valueMsg = new Message();
        valueMsg.what = VIEW_STATUS;
        valueMsg.obj = "connecting...";
        mHandler.sendMessage(valueMsg);

        try{
//            mBtServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(this.getPackageName(),MY_UUID);
//            mSocket = mBtServerSocket.accept();

            // 取得したデバイス名を使ってBluetoothでSocket接続
            mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            mmInStream = mSocket.getInputStream();
            mmOutputStream = mSocket.getOutputStream();

            // InputStreamのバッファを格納
            byte[] buffer = new byte[1024];

            // 取得したバッファのサイズを格納
            int bytes;
            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "connected.";
            mHandler.sendMessage(valueMsg);

            connectFlg = true;

            runOnUiThread(new Runnable() {

                public void run() {
                    startButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            while(isRunning){

                // InputStreamの読み込み
                bytes = mmInStream.read(buffer);
                Log.i(TAG, "bytes=" + bytes);
                // String型に変換
                String readMsg = new String(buffer, 0, bytes);

                // null以外なら表示
                if(readMsg.trim() != null && !readMsg.trim().equals("")) {

                    Log.i(TAG, "value=" + readMsg.trim());

                    valueMsg = new Message();
                    valueMsg.what = VIEW_INPUT;
                    valueMsg.obj = readMsg;

                    if(isNum(readMsg)){
                        press=Integer.valueOf(readMsg);
                        arc.setPress(press+1);
                        arc.showCanvas(true);
                    }
                    mHandler.sendMessage(valueMsg);
                }else{
                    Log.i(TAG,"value=nodata");

                }

            }
        }catch(Exception e){
            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "Error1:" + e;
            mHandler.sendMessage(valueMsg);
            Log.i("Error1", String.valueOf(e));

            try{
                mSocket.close();
                runOnUiThread(new Runnable() {
                    public void run() {
                        scoreLayout.setVisibility(View.INVISIBLE);
                        startButton.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                    }

                });
                connect();
            }catch(Exception ee){
            }
            isRunning = false;
            connectFlg = false;
        }
    };

    public boolean isNum(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;
            String msgStr = (String)msg.obj;
            if(action == VIEW_INPUT){
                mInputTextView.setText(msgStr);
            }
            else if(action == VIEW_STATUS){
                mStatusTextView.setText(msgStr);
            }else{
                mStatusTextView.setText(msgStr);
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v.equals(startButton)) {
            start();
            if (mediaPlayer!= null)stop();
            audioPlay();

            runOnUiThread(new Runnable() {
                public void run() {
                    scoreLayout.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.INVISIBLE);

                }

            });
        } else if (v.equals(connectButton)) {
            connect();
        } else if(v.equals(closeRank)){
            runOnUiThread(new Runnable() {
                public void run() {
                    rankLayout.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void setAnime(final int num, int len) {
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

            imageHeight = 35;
            imageWidth = caWidth;

            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, -0.5f,
                    Animation.RELATIVE_TO_PARENT, -sumH
            );

        } else if (num == 2) {

            imageHeight = caHeight;
            imageWidth = 35;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -0.5f,
                    Animation.RELATIVE_TO_PARENT, -sumW,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f
            );

        } else if (num == 3) {

            imageHeight = caHeight;
            imageWidth = 35;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.5f,
                    Animation.RELATIVE_TO_PARENT, sumW,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f
            );


        } else if (num == 4) {

            imageHeight = 35;
            imageWidth = caWidth;
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0f,
                    Animation.RELATIVE_TO_PARENT, 0.5f,
                    Animation.RELATIVE_TO_PARENT, sumH
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

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(press==num-1)scoreInt+=1;
                score.setText(String.valueOf(scoreInt));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // ImageViewのインスタンス生成
        ImageView imageView = new ImageView(this);

        // drawableの画像を指定
        if (num==1) {
            imageView.setImageResource(color.colorLast1);
        } else if (num==2) {
            imageView.setImageResource(color.colorLast2);
        } else if (num==3){
            imageView.setImageResource(color.colorLast3);
        } else if (num==4){
            imageView.setImageResource(color.colorLast4);
        }

        // 画像の縦横サイズをimageViewのサイズとして設定
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(imageWidth, imageHeight);

        layoutParams.gravity = Gravity.CENTER;

        imageView.setLayoutParams(layoutParams);

// layoutにimageViewを追加
        layout.addView(imageView);

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

        scoreInt=0;

        len=2000;

        try {

            String data = getString(string.data1);
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
            count[0]=0;
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    // UIスレッド
                    count[0]++;
                    if (count[0] > noteLen) {
                        return;
                    }

                    setAnime(finalNote[count[0]],len);
                    handler.postDelayed(this, len/3);

                }
            };

            handler.post(r);

        } catch (JSONException e) {
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
                name="mayu";

//                SharedPreferences sp = getSharedPreferences("pressRhythm", MODE_PRIVATE);
//                SharedPreferences.Editor e = sp.edit();
//                e.putString("name", name);
//                e.commit();
//                name="";
//                name = sp.getString("name",name);
                writeScore(name+","+scoreInt+"\n");

                showRank();
            }
        });

    }

    private void showRank(){
        String[] rankList=readScore().split(" ");
        Log.i("FileAccess", Arrays.toString(rankList));
        int max=0;
        String[][] list = new String[0][];
        for(int i=0;i<rankList.length;i++){
            list[i]=rankList[i].split(",");
        }
        Log.i("FileAccess", Arrays.toString(list));

        Log.i("sort", Arrays.toString(list));
        Arrays.sort(list);
        Log.i("sort", Arrays.toString(list));
//        for(int i=0;i<list.length;i++){
//            addRank(list[i][0], Integer.parseInt(list[i][1]));
//        }
        runOnUiThread(new Runnable() {

            public void run() {
                rankLayout.setVisibility(View.VISIBLE);

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

    private void addRank(final String name, final int score) {

        runOnUiThread(new Runnable() {
            public void run() {
                TableRow tableRow = new TableRow(getApplicationContext());
                rankTable.addView(tableRow);
                TextView text=new TextView(getApplicationContext());
                text.setText(name);
                tableRow.addView(text);
            }
        });

    }

    private void writeScore(String Text){

        OutputStream out;
        try {
            out = openFileOutput(SCORE_FILE,MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            writer.append(Text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readScore(){

        InputStream in;
        String lineBuffer = new String();
        String result = "";
 
        try {
            in = openFileInput(SCORE_FILE);

            BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
            while( (lineBuffer = reader.readLine()) != null ){
//                Log.d("FileAccess",lineBuffer);
                result+=lineBuffer;
                result+=" ";
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineBuffer;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}