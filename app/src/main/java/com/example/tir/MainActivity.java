package com.example.tir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.ViewGroup.LayoutParams;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener, OnLoadCompleteListener {

    private Socket tempClientSocket;
    private ServerSocket serverSocket;
    Thread serverThread = null;
    public Handler handler;
    public static final int SERVER_PORT = 1337;
    public String url = "TIR_LTU";

    public static String scoreTime = "00";

    public static int scoreShoot = 00;
    public static int scoreShootRed = 00;

    public static int time = 600000;                         // ?????????? ????????
    public static int min = 10;                         // ?????????? ????????
    public static int sec = 0;                         // ?????????? ????????

    byte[] esp = {0x00,0x00,0x00,0x00,0x00};

    SoundPool sp;
    int soundIdStart;
    int soundIdGameOver;

    final int MAX_STREAMS = 5;

    int streamIDStart;
    int streamIDExplosion;


    private boolean startGame;
    private boolean stmStart=false;
    public static int typeGame;
    public int round;                                   // ?????????? ????????????
    public int scoreRound = 00;                              //
    public static boolean timeDelay = false;
    //boolean stopTimer;

    //private EditText SendText;
    private Toolbar toolbar;
    public static MyCountDownTimer cTimer = null;
    private static final String TAG = "MyApp";


    //----------------???????????????????? ????????????????-------------------------------------------------
    static int timeTarget = 10;
    static int amountTarget=20;
    static int onIR=1;
    static int Scenary = 0;

    char vol;

    static int volume = 8;
    static int spin = 10;
    static int damage = 10;
    static String color = "#FFFFFF";

    // ?????? ?????????? ????????????????
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TIMER = "time";
    public static final String APP_PREFERENCES_AMOUNT = "amount";
    public static final String APP_PREFERENCES_IR = "onIR";
    public static final String APP_PREFERENCES_DAMAGE = "damage";
    public static final String APP_PREFERENCES_COLOR = "color";

    private SharedPreferences mSettings;

    //--------------------------------------wifi-------------------------------------------
    public  WifiManager wifiManager;
    private WifiConfiguration wifiConfig;
    //public WifiReceiver wifiReceiver;
    public boolean wifiEnabled;

  /*  public void initWiFi() {

        // ?????????????? ?????????? ???????????? ?????? ?????????????????????? ?? ???????????????????? ??????????
        wifiConfig = new WifiConfiguration();
        // ?????????????? ???????????? ?????????????? ?????? ?????????? ???????????????? ???????????????????????? ?? ???????????? ??????????
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //???????????? ?????????????? ????????????  ?????? ??????
        wifiEnabled = wifiManager.isWifiEnabled();
        //?????? ???????????????? ?????????????? ?????????? ???????????????????? ?????? ?????????????? ?????????????? ?????? ????????????????????????, ???????? ???? ?????????? ???????????????????? ???????????? ??????????
        wifiReceiver = new WifiReceiver();

    }
*/

    //-------------------------------------------------------------------------------------



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // -----------------------?????????????? ???????????? ??????????????????????---------------------------------------
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        timeTarget = mSettings.getInt(APP_PREFERENCES_TIMER, 0);
        amountTarget = mSettings.getInt(APP_PREFERENCES_AMOUNT, 0);
        onIR = mSettings.getInt(APP_PREFERENCES_IR,0);

        //----------------------- ???????????? ???? ???????????????????? ????????????--------------------------------------
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //-----------------------------------------------------------------------------------------
        //---------------------------------?????????? ??????????--------------------------------------------
        sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);
        try {
            soundIdStart = sp.load(getAssets().openFd("startgame.ogg"), 1);
            soundIdGameOver = sp.load(getAssets().openFd("gameover.ogg"),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //------------------------WIFI-------------------------------------------------------


     /*  String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.INTERNET

        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);
*/

      /*  initWiFi();
        // ?????????????? ?????????? ???????????? ?????? ?????????????????????? ?? ???????????????????? ??????????
        wifiConfig = new WifiConfiguration();
        // ?????????????? ???????????? ?????????????? ?????? ?????????? ???????????????? ???????????????????????? ?? ???????????? ??????????
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //???????????? ?????????????? ????????????  ?????? ??????
        wifiEnabled = wifiManager.isWifiEnabled();
        //?????? ???????????????? ?????????????? ?????????? ???????????????????? ?????? ?????????????? ?????????????? ?????? ????????????????????????, ???????? ???? ?????????? ???????????????????? ???????????? ??????????
        wifiReceiver = new WifiReceiver();

        if (!wifiEnabled) {
            wifiManager.setWifiEnabled(true);
        }
        //---?????????????????? ?????????????? ????????????, ?? ???????????????????????? ???????? ?????????????????????? ?????? ???????? ????????---
        scheduleSendLocation();
*/

      //  connectToNetwork("TIR_LTU","0","OPEN Network");

        //-----------------------------------------------------------------------------

        //-----------------------------------------------------------------------------
        //handler = new Handler();

         handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView textViewScoreShoot = (TextView) findViewById(R.id.textViewScoreShoot);
                TextView textViewScoreShootRed = (TextView) findViewById(R.id.textViewScoreShootRed);
                switch (msg.arg1) {
                    case 1:
                        textViewScoreShoot.setText(String.format("%02d", msg.what));
                        break;

                    case 2:
                        textViewScoreShootRed.setText(String.format("%02d", msg.what));
                        break;
                }
            }
        };


        //--------------------------???????????? ??????????????-------------------------------------
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        //-----------------------------------------------------------------------------


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //----------------------------???????????????????? ???????????????? ???????????? ??????????-------------------------
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        //  Log.d(LOG_TAG, "onLoadComplete, sampleId = " + sampleId + ", status = " + status);
    }
    //---------------------------------------------------------------------------------------


    //------------------???????????????? ?????????????? ???????????? ?????? ?????????????????? ?????????????? ????????------------

    //--------------------------------------------------------------------------------

    //--------------------?????????????????????? ?? WiFi -----------------------------------------
  /*  public void scheduleSendLocation() {

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_LONG).show();
    }
*/

    //--------------------------------------------------------------------------------

    @Override
    public void onClick(View view) {
        Message msg;

        if (view.getId()== R.id.textViewStart) {
            TextView buttonStart = (TextView) findViewById(R.id.textViewStart);
            TextView textViewTimer = (TextView) findViewById(R.id.textViewTimer);
            TextView textViewScoreShoot = (TextView) findViewById(R.id.textViewScoreShoot);
            buttonStart.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);


                    if (startGame) {                                                                // ???????? ???????? ?????? ????????, ???? ???????????? ??????????????????
                        cTimer.cancel();                                                            //???????????? ????????

                        buttonStart.setText("START");                                               //?????????????? ???? ???????????? ??????????
                        startGame = false;                                                          //???????? ?????????????????? ????????
                        textViewTimer.setText(String.format("%02d:%02d", min, sec));                // ???????????????????? ?????????????????? ???????????????? ??????????????
                        esp[0]=0x05;
                        sendMessage(esp);                                                            // ???????????????? ?? ?????? "????????"
                        sp.play(soundIdGameOver, 1, 1, 0, 0, 1);

                    }
                    else {
                       sp.play(soundIdStart, 1, 1, 0, 0, 1);
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cTimer = new MyCountDownTimer((min*60000+sec*1000),1000);
                        scoreShoot=00;
                        scoreShootRed=00;
                        msg = handler.obtainMessage(scoreShoot, 1, 0);
                        handler.sendMessage(msg);                                                    // ?????????????????? ????????????????
                        msg = handler.obtainMessage(scoreShootRed, 2, 0);
                        handler.sendMessage(msg);
                        //handler.sendEmptyMessage(scoreShoot);     // ?????????????????? ????????????????
                        //handler.sendEmptyMessage(scoreShootRed);     // ?????????????????? ????????????????
                        //cTimer.start();                                                             // ???????????? ??????????????
                        //startGame = true;
                        //buttonStart.setText("STOP");                                                // ?????????????? ???? ???????????? ????????
                        esp[0]=0x08;
                        esp[1]= (byte) timeTarget;
                        esp[2]= (byte) amountTarget;
                        esp[3]= (byte) Scenary;
                        esp[4]=(byte) onIR;

                        sendMessage(esp);

                      //  while (!stmStart) {
                      //                                                       // ???????????????? ?? ?????? "????????
                      //  }
                        cTimer.start();                                                             // ???????????? ??????????????
                        startGame = true;
                        buttonStart.setText("STOP");

                    }


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != serverThread) {
            //sendMessage("Disconnect");
            serverThread.interrupt();
            serverThread = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onDestroy();
        if (null != serverThread) {
            //sendMessage("Disconnect");
            serverThread.interrupt();
            serverThread = null;
        }

    }


    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

    }

    //------------------------???????????????? ???????????? ??????????????---------------------------------------
    class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long Ms) {
            final TextView textViewTimer = (TextView) findViewById(R.id.textViewTimer);
            TextView buttonStart = (TextView) findViewById(R.id.textViewStart);
            textViewTimer.setText(String.format("%02d:%02d", Ms/60000, (Ms % 60000)/1000));
        }

        @Override
        public void onFinish() {
            TextView buttonStart = findViewById(R.id.textViewStart);
            TextView textViewTimer = findViewById(R.id.textViewTimer);
            buttonStart.setText("??????????");
            textViewTimer.setText(String.format("%02d:%02d", min, sec));                // ???????????????????? ?????????????????? ???????????????? ??????????????
            startGame = false;
            esp[0]=0x05;
            sendMessage(esp);                                                            // ???????????????? ?? ?????? "????????"
            sp.play(soundIdGameOver, 1, 1, 0, 0, 1);
            //statPage();+
        }
    }

    //------------------------------------------------------------------------------------


    //********************************************************************************************
    //--------------------------???????????? ???????????? ????????????----------------------------------------------
    //********************************************************************************************
    public void showMessage(final String message) {
        handler.post(new Runnable() {
            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                TextView textViewServer = (TextView) findViewById(R.id.textViewServer);
                textViewServer.setText(message);
            }
        });
    }

    //------------------------???????????????? ?????????????????? ??????????????(????????????)----------------------
    private void sendMessage(final byte[] message) {
        try {
            if (null != tempClientSocket) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedOutputStream out = null;
                        try {
                            out = new BufferedOutputStream(tempClientSocket.getOutputStream());
                            out.write(message);
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------------------------------

    class ServerThread implements Runnable {

        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            }catch (IOException e) {
                e.printStackTrace();
                showMessage("???????????? ?????????????? ??????????????");
            }
            //showMessage("?????????????????? ????????????");
            if (null !=serverSocket) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage("???????????? ?????????? ?? ????????????????"+e.getMessage());
                    }
                }
            }

        }
    }

    class CommunicationThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;



        public CommunicationThread(Socket clientSocket){
            this.clientSocket = clientSocket;
            tempClientSocket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            }catch (IOException e){
                e.printStackTrace();
                showMessage("???????????? ?????????? ?? ????????????????!!!"+e.getMessage());
            }
            showMessage("???????????? ??????????????????");

        }

        public void run() {
            final TextView textViewScoreShoot = (TextView) findViewById(R.id.textViewScoreShoot);
            Message msg;
            while (!Thread.currentThread().isInterrupted()){
                try {
                    String read = input.readLine();

                    if (null == read ){
                        Thread.interrupted();
                        //read = "CLIENT DISCONNECTED";
                        showMessage("?????????????????? ????????????");  // +read
                        break;
                    }
                    //scoreTime = read;
                    //scoreDuel = read;
                    if (read.equals("target")){
                        scoreShoot++;
                        //handler.sendEmptyMessage(scoreShoot);
                        msg = handler.obtainMessage(scoreShoot, 1, 0);
                        // ????????????????????
                        handler.sendMessage(msg);
                        //textViewScoreShoot.setText(String.format("%02d",scoreShoot));                           // ?????????? ??????????????????
                        //textViewScoreShoot.setTextColor(getResources().getColor(R.color.green));
                    }
                    if (read.equals("targetred")) {
                        scoreShootRed++;
                        //handler.sendEmptyMessage(scoreShootRed);
                        msg = handler.obtainMessage(scoreShootRed, 2, 0);
                        // ????????????????????
                        handler.sendMessage(msg);
                    }
                    if (read.equals("errshot")){
                        if (scoreShoot>0) {
                            scoreShoot--;
                            //handler.sendEmptyMessage(scoreShoot);
                            msg = handler.obtainMessage(scoreShoot, 1, 0);
                            // ????????????????????
                            handler.sendMessage(msg);
                        }
                        //textViewScoreShoot.setText(String.format("%02d",scoreShoot));                           // ?????????? ??????????????????
                        //textViewScoreShoot.setTextColor(getResources().getColor(R.color.green));
                    }
                   showMessage(read);//"????????????:"

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //--------------------------------------------------------------------------------


    public void showTime(View view) // ?????????????????? ???????????? ??????????, ?????????? ???????? ???????????? ????????????????
    {
        final TextView textViewTimer = (TextView) findViewById(R.id.textViewTimer);
        final Dialog d = new Dialog(MainActivity.this);

        d.setContentView(R.layout.dialogtime);
        TextView textDialog = (TextView) d.findViewById(R.id.textViewGreen);
        //textDialog.setText(R.string.dialogTime);
        //textDialog.setText("?????????????? ?????????? ????????");
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker npmin = (NumberPicker) d.findViewById(R.id.numberPicker1);
        npmin.setMaxValue(60);
        npmin.setMinValue(0);
        npmin.setValue(min);
        npmin.setWrapSelectorWheel(false);
        npmin.setOnValueChangedListener(this);
        final NumberPicker npsec = (NumberPicker) d.findViewById(R.id.numberPicker2);
        npsec.setMaxValue(60);
        npsec.setMinValue(0);
        npsec.setValue(sec);
        npsec.setWrapSelectorWheel(false);
        npsec.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                min = npmin.getValue();
                sec = npsec.getValue();
                textViewTimer.setText(String.format("%02d:%02d", min, sec));
                d.dismiss();

            }
        });

        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }


    class TimerDuel extends CountDownTimer {
        public TimerDuel(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long Ms) {

        }

        @Override
        public void onFinish() {
            timeDelay = true;
        }
    }


    public void onClickSetting(MenuItem item) {

        final TextView textViewTimer = (TextView) findViewById(R.id.textViewTimer);
        final Dialog d = new Dialog(MainActivity.this);

        d.setContentView(R.layout.activity_setting);

        CheckBox checkBoxIR = d.findViewById(R.id.checkBoxIR);
        if (onIR==1)
            checkBoxIR.setChecked(true);
        else
            checkBoxIR.setChecked(false);

        d.show();
       // Intent intent = new Intent(MainActivity.this, SettingActivity.class);
       // startActivity(intent);
    }






 //**************************activity setting******************************************************
 public void showTimeTarget(View view) // ?????????????????? ???????????? ??????????, ?????????? ???????? ???????????? ????????????????
 {
     //final Button button_time = (Button) findViewById(R.id.button_time);


     final Dialog dTime = new Dialog(MainActivity.this);

     dTime.setContentView(R.layout.dialog);
     TextView textDialog = (TextView) dTime.findViewById(R.id.textViewTarget);
     textDialog.setText(R.string.dialogTime);
     Button b1 = (Button) dTime.findViewById(R.id.button1);
     Button b2 = (Button) dTime.findViewById(R.id.button2);
     final NumberPicker np = (NumberPicker) dTime.findViewById(R.id.numberPicker1);
     np.setMaxValue(6);
     np.setMinValue(1);
     np.setValue(timeTarget);
     np.setWrapSelectorWheel(false);
     np.setOnValueChangedListener(this);
     b1.setOnClickListener(new View.OnClickListener()  {
         @Override
         public void onClick(View v) {
             timeTarget = np.getValue();
            // final Button button_time = (Button) findViewById();
            // button_time.setText(getString(R.string.buttonTime,timeTarget));
             dTime.dismiss();
         }
     });
     b2.setOnClickListener(new View.OnClickListener()
     {
         @Override
         public void onClick(View v) {
             dTime.dismiss();
         }
     });
     dTime.show();
 }

    public void showAmountTarget(View view) // ?????????????????? ???????????? ??????????, ?????????? ???????? ???????????? ????????????????
    {
        //final Button button_time = (Button) findViewById(R.id.button_time);


        final Dialog dTime = new Dialog(MainActivity.this);

        dTime.setContentView(R.layout.dialog);
        TextView textDialog = (TextView) dTime.findViewById(R.id.textViewTarget);
        textDialog.setText(R.string.buttonTarget);
        Button b1 = (Button) dTime.findViewById(R.id.button1);
        Button b2 = (Button) dTime.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) dTime.findViewById(R.id.numberPicker1);
        np.setMaxValue(20);
        np.setMinValue(1);
        np.setValue(amountTarget);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                amountTarget = np.getValue();
                // final Button button_time = (Button) findViewById();
                // button_time.setText(getString(R.string.buttonTime,timeTarget));
                dTime.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dTime.dismiss();
            }
        });
        dTime.show();
    }

    public void onClickcheck(View view){
        CheckBox checkBoxIR = view.findViewById(R.id.checkBoxIR);
        if(checkBoxIR.isChecked())
            onIR=1;
        else
            onIR=0;

    }


    //**************************?????????????? ???????????????????? ?? ???????????????? ????????????????*********************************
    public void saveData(View view){


        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_TIMER, timeTarget);
        editor.putInt(APP_PREFERENCES_IR, onIR);
        editor.putInt(APP_PREFERENCES_DAMAGE, damage);
        editor.putInt(APP_PREFERENCES_AMOUNT, amountTarget);
        editor.putString(APP_PREFERENCES_COLOR, color);
        editor.apply();
    }

    public void loadData(View view){
        if (mSettings.contains(APP_PREFERENCES_TIMER)) {
            // ???????????????? ?????????? ???? ????????????????
            timeTarget = mSettings.getInt(APP_PREFERENCES_TIMER, 0);
            //Button button_time = (Button) findViewById(R.id.button_time);
            //button_time.setText(getString(R.string.buttonTime,timeTarget));
        }

        if (mSettings.contains(APP_PREFERENCES_AMOUNT)) {
            spin = mSettings.getInt(APP_PREFERENCES_AMOUNT, 0);
          //  Button button_spin = (Button) findViewById(R.id.button_down);
          //  button_spin.setText(getString(R.string.buttonDown,spin));
        }

        if (mSettings.contains(APP_PREFERENCES_DAMAGE)) {
            damage = mSettings.getInt(APP_PREFERENCES_DAMAGE, 0);
            //Button button_damage = (Button) findViewById(R.id.button_damage);
            //button_damage.setText(getString(R.string.buttonDamage,damage));
        }

        if (mSettings.contains(APP_PREFERENCES_COLOR)) {
            color = mSettings.getString(APP_PREFERENCES_COLOR, "0");
           // ImageView imageColor = (ImageView) findViewById(R.id.imageColor);
           // imageColor.setBackgroundColor(Color.parseColor(color));
        }

        if (mSettings.contains(APP_PREFERENCES_IR)) {
            volume = mSettings.getInt(APP_PREFERENCES_IR, 0);
           // Button button_volume = (Button) findViewById(R.id.button_prog);
           // button_volume.setText(getString(R.string.buttonProg,volume));
        }

    }

    public void SendData(View view){
        //handler.sendMessage();
    }


    //**********************************************************************************************
    //-----------------------------???????????? ???????????? ????????????????-------------------------------------------
    //**********************************************************************************************
    public void showScena(View view) // ?????????????????? ???????????? ????????????????, ?????????? ???????? ???????????? ???????????????? /

    {
        //final TextView textViewServer = (TextView) findViewById(R.id.textViewServer);
        final Dialog d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.dialog_scena);
        //Button b1 = (Button) d.findViewById(R.id.button1);
        //Button b2 = (Button) d.findViewById(R.id.button2);

        final RadioGroup radioGroup = (RadioGroup) d.findViewById(R.id.RadioGroupScena);
        final RadioButton radioButtonStandart = (RadioButton) d.findViewById(R.id.radioButtonStandart);
        final RadioButton radioButtonColor = (RadioButton) d.findViewById(R.id.radioButtonColor);
       // final RadioButton radioButtonKT = (RadioButton) d.findViewById(R.id.radioButtonKT);
       // final RadioButton radioButtonChild = (RadioButton) d.findViewById(R.id.radioButtonChild);
       // final RadioButton radioButtonDeath = (RadioButton) d.findViewById(R.id.radioButtonDeath);
        radioGroup.clearCheck();
        switch (Scenary) {
            case 0:
                radioButtonStandart.setChecked(true);
                break;
            case 1:
                radioButtonColor.setChecked(true);
                break;
          //  case "KT":
          //      radioButtonKT.setChecked(true);
          //      break;
          //  case "Child":
          //      radioButtonChild.setChecked(true);
          //      break;
          //  case "Death":
          //      radioButtonDeath.setChecked(true);
          //      break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextView textViewScena = (TextView) findViewById(R.id.textViewScena);
                switch (checkedId) {
                    case R.id.radioButtonStandart:
                        Scenary = 0;
                        textViewScena.setText(R.string.scenaStandart);
                        break;
                    case R.id.radioButtonColor:
                        Scenary = 1;
                        textViewScena.setText(R.string.scenaColor);
                        break;
                    case R.id.radioButtonDuel:
                         Scenary = 2;
                         textViewScena.setText(R.string.scenaDuel);
                         break;
                  //  case R.id.radioButtonChild:
                  //      Scenary = "Child";
                  //      textViewServer.setText("??????????????");
                  //      break;
                  //  case R.id.radioButtonDeath:
                  //      Scenary = "Death";
                  //      textViewServer.setText("DEATHMATCH");
                  //      break;
                }
                d.dismiss();
            }
        });

       /* b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        }); */
       /* b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        */
        d.show();

    }


//************************************************************************************************
//*********************************???????????????? ?????????? ???? URL******************************************
//************************************************************************************************
    @SuppressLint("StaticFieldLeak")
    public void downloadFile(View view) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            String url="http://www.ltu.com.ua/Soft/gar.docx";
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

            new AsyncTask<String, Integer, File>() {
                private Exception m_error = null;

                @SuppressLint("StaticFieldLeak")
                @Override
                protected void onPreExecute() {
                    progressDialog.setMessage("Downloading ...");
                    progressDialog.setCancelable(false);
                    progressDialog.setMax(100);
                    progressDialog
                            .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                    progressDialog.show();
                }

                @Override
                protected File doInBackground(String... params) {
                    URL url;
                    HttpURLConnection urlConnection;
                    InputStream inputStream;
                    int totalSize;
                    int downloadedSize;
                    byte[] buffer;
                    int bufferLength;

                    File file = null;
                    FileOutputStream fos = null;

                    try {
                        url = new URL(params[0]);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        Log.e("status",folder);
                        fos = new FileOutputStream(folder+"/boot.bin");
                        inputStream = urlConnection.getInputStream();

                        totalSize = urlConnection.getContentLength();
                        downloadedSize = 0;

                        buffer = new byte[1024];
                        bufferLength = 0;

                        // ???????????? ???? ?????????? ?? ?????????? ?? ??????????,
                        // ?? ???????????? ?????????????????? ?????????????????? ????????????????
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            publishProgress(downloadedSize, totalSize);
                        }

                        fos.close();
                        inputStream.close();

                        return file;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        m_error = e;
                    } catch (IOException e) {
                        e.printStackTrace();
                        m_error = e;
                    }

                    return null;
                }

                // ?????????????????? progressDialog
                protected void onProgressUpdate(Integer... values) {
                    progressDialog
                            .setProgress((int) ((values[0] / (float) values[1]) * 100));
                };

                @Override
                protected void onPostExecute(File file) {
                    // ???????????????????? ??????????????????, ???????? ???????????????? ????????????
                    if (m_error != null) {
                        m_error.printStackTrace();
                        return;
                    }
                    // ?????????????????? ????????????????
                    progressDialog.hide();

                }
            }.execute(url);
        }
//*************************************************************************************************
/*
    public class WifiReceiver extends BroadcastReceiver {

        //   public WifiManager wifiManager;
        //   public WifiConfiguration wifiConfig;
        //   public WifiReceiver wifiReceiver;
        //   public boolean wifiEnabled;
        //   public String url = "TIR_LTU";

        @Override
        public void onReceive(Context c, Intent intent) {

            //?????????????????? ???????????? ?????????? ?? ???????????? ?????????? ????????????????
            List<ScanResult> results = wifiManager.getScanResults();
            //???????????????????? ???? ???????? ?????????????????? ????????????
            for (final ScanResult ap : results) {
                //???????? ???????????? ?????? ?????????? ?? ?????????????? ??????, ?????????? ???????????????? ???? ?????????????? ???? ??????????
                Toast.makeText(MainActivity.this, "ghbdtn22", Toast.LENGTH_LONG).show();
                if (ap.SSID.toString().trim().equals(url.trim())) {

                    // ???????????? ???????????????? ???? MAC ?? ???????????????? ?????? ??????????????????, MAC ???????????????? ???? ????????????????????
                    //?????????? ???? ?????? ???????????????? ????????????????????????
                    wifiConfig.BSSID = ap.BSSID;
                    wifiConfig.priority = 1;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.status = WifiConfiguration.Status.ENABLED;


                    //???????????????? ID ???????? ?? ???????????????? ?? ?????? ????????????????????????,
                    int netId = wifiManager.addNetwork(wifiConfig);
                    wifiManager.saveConfiguration();
                    //???????? ???????????? ???????????????? ???? ???????????????? ??????
                    wifiManager.enableNetwork(netId, true);
                    //???????? ???? ???? ?????????????? ???? ?????????????????? ?? ???????????? ???????? ???? ?????????????????????? ????????????.
                    wifiManager.reconnect();
                    break;
                }
            }
        }
    }
*/



}