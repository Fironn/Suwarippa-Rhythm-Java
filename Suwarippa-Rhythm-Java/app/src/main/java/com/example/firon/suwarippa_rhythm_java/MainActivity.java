package com.example.firon.suwarippa_rhythm_java;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Toast;



public class MainActivity extends Activity implements Runnable, View.OnClickListener {
    /** tag. */
    private static final String TAG = "BluetoothSample";

    private MediaPlayer mediaPlayer;

    /** Bluetooth Adapter.  */
    private BluetoothAdapter mAdapter;

    /** Bluetoothデバイス. */
    private BluetoothDevice mDevice;

    /** Bluetooth UUID. */
    private final UUID MY_UUID = UUID.fromString("00002A00-0000-1000-8000-00805F9B34FB");

    /** デバイス名. */
    private final String DEVICE_NAME = "RNBT-2F78";

    /** Soket. */
    private BluetoothSocket mSocket;

    /** Thread. */
    private Thread mThread;

    /** Threadの状態を表す. */
    private boolean isRunning;

    /** 接続ボタン. */
    private Button connectButton;
    private Button startButton;


    /** 書込みボタン. */
//    private Button writeButton;

    /** ステータス. */
    private TextView mStatusTextView;

    /** Bluetoothから受信した値. */
    private TextView mInputTextView;

    /** Action(ステータス表示). */
    private static final int VIEW_STATUS = 0;

    /** Action(取得文字列). */
    private static final int VIEW_INPUT = 1;

    /** Connect確認用フラグ */
    private boolean connectFlg = false;
    private int i=0;

    /** BluetoothのOutputStream. */
    OutputStream mmOutputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = (WebView)findViewById(R.id.webView1);

//        レイアウトで指定したWebViewのIDを指定する。
        myWebView.setWebViewClient(new WebViewClient());



        mInputTextView = (TextView)findViewById(R.id.inputValue);
        mStatusTextView = (TextView)findViewById(R.id.statusValue);

        connectButton = (Button)findViewById(R.id.connectButton);
        startButton = (Button)findViewById(R.id.startButton);

//        writeButton = (Button)findViewById(R.id.writeButton);

        connectButton.setOnClickListener(this);
        startButton.setOnClickListener(this);

//        writeButton.setOnClickListener(this);

        // Bluetoothのデバイス名を取得
        // デバイス名は、RNBT-XXXXになるため、
        // DVICE_NAMEでデバイス名を定義
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatusTextView.setText("SearchDevice");
        Set< BluetoothDevice > devices = mAdapter.getBondedDevices();
        for ( BluetoothDevice device : devices){
            if(device.getName().equals(DEVICE_NAME)){
                mStatusTextView.setText("find: " + device.getName());
                mDevice = device;
            }
        }

        if (!connectFlg) {
            mStatusTextView.setText("try connect");

            mThread = new Thread(this);
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

            while(isRunning){


                // InputStreamの読み込み
                bytes = mmInStream.read(buffer);
//                Log.i(TAG, "bytes=" + bytes);
                // String型に変換
                String readMsg = new String(buffer, 0, bytes);

                // null以外なら表示
                if(readMsg.trim() != null && !readMsg.trim().equals("")){
//                    Log.i(TAG,"value="+readMsg.trim());

                    valueMsg = new Message();
                    valueMsg.what = VIEW_INPUT;

                    valueMsg.obj = readMsg;
                    mHandler.sendMessage(valueMsg);
//                    System.out.println(valueMsg.obj);
//                    sample.sendData.main(null, new Integer(readMsg));
//                    com.example.firon.pressrhythm.writeFile.write(null,new Integer(readMsg));

//                    i++;
//                    if(i>=4)i=0;
                }
                else{
                    // Log.i(TAG,"value=nodata");
                }

            }
        }catch(Exception e){

            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "Error1:" + e;
            mHandler.sendMessage(valueMsg);

            try{
                mSocket.close();
            }catch(Exception ee){}
            isRunning = false;
            connectFlg = false;
        }
    }

    @Override
    public void onClick(View v) {

        if(v.equals(connectButton)) {
            // 接続されていない場合のみ
            if (!connectFlg) {
                mStatusTextView.setText("try connect");

                mThread = new Thread(this);
                // Threadを起動し、Bluetooth接続
                isRunning = true;
                mThread.start();
            }
        }else if(v.equals(startButton)){

        }
//        else if(v.equals(writeButton)) {
//            // 接続中のみ書込みを行う
//            if (connectFlg) {
//                try {
//                    mmOutputStream.write("2".getBytes());
//                    mStatusTextView.setText("Write:");
//                } catch (IOException e) {
//                    Message valueMsg = new Message();
//                    valueMsg.what = VIEW_STATUS;
//                    valueMsg.obj = "Error3:" + e;
//                    mHandler.sendMessage(valueMsg);
//                }
//            } else {
//                mStatusTextView.setText("Please push the connect button");
//            }
//        }
    }

    /**
     * 描画処理はHandlerでおこなう
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;
            String msgStr = (String)msg.obj;
            if(action == VIEW_INPUT){
                mInputTextView.setText(msgStr);
            }
            else if(action == VIEW_STATUS){
                mStatusTextView.setText(msgStr);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean audioSetup(){
        boolean fileCheck = false;

        // インタンスを生成
        mediaPlayer = new MediaPlayer();

        //音楽ファイル名, あるいはパス
        String filePath = "Nolove.mp3";

        // assetsから mp3 ファイルを読み込み
        try{
            AssetFileDescriptor afdescripter = getAssets().openFd(filePath);
            // MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            // 音量調整を端末のボタンに任せる
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            fileCheck = true;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println(fileCheck);


        return fileCheck;
    }

    private void audioPlay() {

        if (mediaPlayer == null) {
            // audio ファイルを読出し
            if (audioSetup()){
                Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
        }

        // 再生する
        mediaPlayer.start();
        System.out.println("music");
    }


}